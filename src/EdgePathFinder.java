import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import processing.core.PApplet;



// a singleton that holds all the path finding code
// used to find the cheapest path between two nodes, using A* as described here: https://www.redblobgames.com/pathfinding/a-star/introduction.html
// the main path finding function is public boolean findPath()
// TODO: 
// * make a variable mapping between the size of the grid that gets drawn, and the search grid (m_grid). Right now they are always 1:1 and it's
// way too slow. Try making a 5x5, 10x10 size drawn grid a single path finding node and check performance
// * Optimize the resulting path with some sort of line-of-sight test that removes points that are not needed.
// * make the path finding parameters user-editable
public class EdgePathFinder {

	private static EdgePathFinder m_instance = null; 
	
	private PathFindingNode m_grid[][];
	private int m_gridXSize;
	private int m_gridYSize;
	
	// path finding parameters
	private float m_costToEnterNode; // path finding cost to enter a node point
	private float m_costToEnterNodeProximity; // path finding cost to enter close to a node point (in its proximity, distance defined by m_nodeProximity)
	private float m_costToEnterEdge; // path finding cost to enter an edge point
	private int m_nodeProximityLength; // how far away from a node is considered its proximity
	private int m_edgeThickness; // how thick an edge line is considered to be. Used to avoid parallel lines running too close together.

	// path finding vars
	private ArrayList<PointInt> m_path; // path of grid points found by going from endnode, through the m_cameFrom linked list, to its end (null)
	private ArrayList<PointInt> m_pathAsLinePoints; // path with only line segment start/end points
	private NodePriorityComparator m_comparator; // comparator used for the PriorityQueue
	private PriorityQueue<PathFindingNode> m_queue; // the prioritized queue of nodes to check. Priority is A*: f = g(reedy) + h(euristic)
	private HashMap<PathFindingNode, Float> m_costSoFar; // for each node visited, keeps the cost to reach it. Used for comparison for multiple hits
	private HashMap<PathFindingNode, PathFindingNode> m_cameFrom; // a linked list of visited nodes. Follow one node backwards to see where it came from
	private int m_startNodeId; 
	private int m_endNodeId;
	private PathFindingNode m_startNode;
	private PathFindingNode m_endNode;
	
	private PointInt m_arrowPoint1;
	private PointInt m_arrowPoint2;
	private float m_arrowLength;
	
	// performance vars
	private long m_elapsedTime;
	private long m_longestSearchTime;
	private int m_longestSearchTimeEdgeFrom;
	private int m_longestSearchTimeEdgeTo;
	private ArrayList<Long> m_timeToFindEdgePath; // performance
	
	private EdgePathFinder() 
	{
		m_gridXSize = 0;
		m_gridYSize = 0;
		m_costToEnterNode = 8.f;
		m_costToEnterNodeProximity = 6.f;
		m_costToEnterEdge = 4.f;
		m_edgeThickness = 5;
		m_nodeProximityLength = 4;
		m_path = new ArrayList<PointInt>();
		m_comparator = new NodePriorityComparator();
		m_arrowPoint1 = new PointInt();
		m_arrowPoint2 = new PointInt();
		m_arrowLength = 6.f;
		m_elapsedTime = 0L;
		m_longestSearchTimeEdgeFrom = 0;
		m_longestSearchTimeEdgeTo = 0;
		m_timeToFindEdgePath = new ArrayList<Long>();
	}
 
	
	public static EdgePathFinder getInstance() 
	{
      
		if(m_instance == null)
			m_instance = new EdgePathFinder();
	
		return m_instance;
	}
	
	
	// performance accessors
	public long getLongestSearchTime()
	{
		return m_longestSearchTime;
	}
	
	public int getLongestSearchTimeEdgeFrom()
	{
		return m_longestSearchTimeEdgeFrom;
	}

	public int getLongestSearchTimeEdgeTo()
	{
		return m_longestSearchTimeEdgeTo;
	}
	
	public long getTotalElapsedTime()
	{
		return m_elapsedTime;
	}
	
	
	public ArrayList<Long> getElapsedTimeArray()
	{
		return m_timeToFindEdgePath;
	}
	
	
	// remove a node from the grid. This is done with start and end nodes at the start of a search (we allow a path to the center
	// of start and end nodes with base cost).
	public void removeNodeFromGrid(int nodeId)
	{
		GraphHandler graphHandler = GraphHandler.getInstance();
		Node node = graphHandler.getNode(nodeId);
		NodeRect nodeRect = node.getNodeRect();
		
		int rectXstart = nodeRect.getXpos() - m_nodeProximityLength;
		if (rectXstart < 0)
			rectXstart = 0;
		
		int rectXend = rectXstart + nodeRect.getWidth() + m_nodeProximityLength * 2;
		if (rectXend >= m_gridXSize)
			rectXend = m_gridXSize - 1;
		
		int rectYstart = nodeRect.getYpos() - m_nodeProximityLength;
		if (rectYstart < 0)
			rectYstart = 0;
		
		int rectYend = rectYstart + nodeRect.getHeight() + m_nodeProximityLength * 2;
		if (rectYend >= m_gridYSize)
			rectYend = m_gridYSize - 1;
		
		for(int y=rectYstart; y<rectYend; y++)
			for(int x=rectXstart; x<rectXend; x++)
				m_grid[x][y].setCostToEnter(1.f);	 
	}
	
	
	// add a node to the grid, or rather, add the cost of entering a node at the grid points the node occupies.
	// also adds a cost a short distance away from the node, to discourage edges from being too close to the node.
	public void addNodeToGrid(Node node)
	{
		NodeRect nodeRect = node.getNodeRect();
		int rectXstart = nodeRect.getXpos() - m_nodeProximityLength;
		if (rectXstart < 0)
			rectXstart = 0;
		
		int rectXend = rectXstart + nodeRect.getWidth() + m_nodeProximityLength * 2;
		if (rectXend >= m_gridXSize)
			rectXend = m_gridXSize - 1;
		
		int rectYstart = nodeRect.getYpos() - m_nodeProximityLength;
		if (rectYstart < 0)
			rectYstart = 0;
		
		int rectYend = rectYstart + nodeRect.getHeight() + m_nodeProximityLength * 2;
		if (rectYend >= m_gridYSize)
			rectYend = m_gridYSize - 1;

		for(int y=rectYstart; y<rectYend; y++) {
			for(int x=rectXstart; x<rectXend; x++) {
				if (nodeRect.isInsideRect(x,  y))
					m_grid[x][y].setCostToEnter(m_costToEnterNode);
				else
					m_grid[x][y].setCostToEnter(m_costToEnterNodeProximity);
			}
		}
	}
	
	
	public void addNodeToGrid(int nodeId)
	{
		GraphHandler graphHandler = GraphHandler.getInstance();
		Node node = graphHandler.getNode(nodeId);
		addNodeToGrid(node);
	}
	
	
	// called from Main. Initializes path finding nodes
	public void initGrid()
	{
		GraphHandler graphHandler = GraphHandler.getInstance();
		m_gridXSize = graphHandler.getLayoutWidth() + 1;
		m_gridYSize = graphHandler.getLayoutHeight() + 1;
		
		// create path finding nodes
		m_grid = new PathFindingNode[m_gridXSize][m_gridYSize];
		for (int y=0; y<m_gridYSize; y++) {
			for (int x=0; x<m_gridXSize; x++) {
				PathFindingNode pathFindingNode = new PathFindingNode(x, y);
				m_grid[x][y] = pathFindingNode;
			}
		}
		
		
		// add node costs to grid
		HashMap<Integer, Node> nodes = graphHandler.getNodes();
		for(Map.Entry<Integer, Node> entry : nodes.entrySet()) {

			Node node = (Node)entry.getValue();
			if (node.getShipmentId() == -1)
				continue; // skip external objects for now

			addNodeToGrid(node);
		 }
		
		 // set neighbours
		 for (int y=0; y<m_gridYSize; y++) {
			for (int x=0; x<m_gridXSize; x++) {
				ArrayList<PathFindingNode> neighbours = new ArrayList<PathFindingNode>();
				if (x > 0)
					neighbours.add(m_grid[x-1][y]);
				if(x < m_gridXSize - 1)
					neighbours.add(m_grid[x+1][y]);
				if(y > 0)
					neighbours.add(m_grid[x][y-1]);
				if(y < m_gridYSize - 1)
					neighbours.add(m_grid[x][y+1]);	
				// add neighbours on the diagonal as well. This increases memory but makes for shorter paths.
				if (x>0 && y>0)
					neighbours.add(m_grid[x-1][y-1]);
				if (x<m_gridXSize - 1 && y<m_gridYSize - 1)
					neighbours.add(m_grid[x+1][y+1]);
				if (x<m_gridXSize - 1 && y>0)
					neighbours.add(m_grid[x+1][y-1]);
				if (x>0 && y<m_gridYSize - 1)
					neighbours.add(m_grid[x-1][y+1]);

				m_grid[x][y].setNeighbours(neighbours);
			}
		} 
	}

	
	// called from Main::draw, for debugging purposes
	public void debugDrawGrid(PApplet parent, int drawYOffset)
	{
		for (int y=0; y<m_gridYSize; y++) {
			for (int x=0; x<m_gridXSize; x++) {
				PathFindingNode pathFindingNode = m_grid[x][y];
				if (pathFindingNode.getCostToEnter() == m_costToEnterNode) {
					parent.stroke(25, 25, 25);
					parent.point(pathFindingNode.getPosition().x, pathFindingNode.getPosition().y + drawYOffset);
				}
				else if (pathFindingNode.getCostToEnter() == m_costToEnterNodeProximity) {
					parent.stroke(125, 125, 125);
					parent.point(pathFindingNode.getPosition().x, pathFindingNode.getPosition().y + drawYOffset);
				}
				else if (pathFindingNode.getCostToEnter() == m_costToEnterEdge) {
					parent.stroke(250, 25, 25);
					parent.point(pathFindingNode.getPosition().x, pathFindingNode.getPosition().y + drawYOffset);					
				}
				
			}
		}
	}
	
	
	// the "h" in the A* algorithm f = g + h 
	public float heuristic(PointInt a, PointInt b)
	{
		// manhattan distance 
		return (float)(Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
		
//		float deltaX = (float)(a.x - b.x);
//		float deltaY = (float)(a.y - b.y);
//		return (float)(Math.sqrt((double)(deltaX * deltaX + deltaY * deltaY)));
//		return (float)(deltaX * deltaX + deltaY * deltaY);
	}	
	
	
	// set initial values in preparation for finding a path between two nodes
	public void initFindPath(Edge edge)
	{
		m_path = new ArrayList<PointInt>();
		m_pathAsLinePoints = new ArrayList<PointInt>();
		
		PointInt start = edge.getFromPos();
		m_startNodeId = edge.getFromId();
		removeNodeFromGrid(m_startNodeId);
		
		PointInt end = edge.getToPos();
		m_endNodeId = edge.getToId();
		removeNodeFromGrid(m_endNodeId);
		
        m_queue = new PriorityQueue<PathFindingNode>(m_comparator);
        
        m_costSoFar = new HashMap<PathFindingNode, Float>();
        m_cameFrom = new HashMap<PathFindingNode, PathFindingNode>();
        
        m_startNode = m_grid[start.x][start.y];
        m_startNode.setPriority(0.f);
        m_costSoFar.put(m_startNode, 0.f);
        
        m_endNode = m_grid[end.x][end.y];
        m_endNode.setPriority(0.f);
        
        m_queue.add(m_startNode);	
	}
	
	
	// applies A* on a grid, as described here: https://www.redblobgames.com/pathfinding/a-star/introduction.html
	// Modified to handle neighbours on the diagonal as well
	public boolean findPath()
	{
		while(true)
		{
			if (m_queue.isEmpty())
				return false; // done, failed to find a path
			
			PathFindingNode currentNode = m_queue.peek(); // look without removing
	        	
	        if (currentNode == m_endNode)
	        	return true; // done, found a path
	
	        currentNode = m_queue.poll(); // get and remove the node from the queue
	        	
	        ArrayList<PathFindingNode> neighbours = currentNode.getNeighbours();
	        	
	        int currentXpos = currentNode.getPosition().x;
	        int currentYpos = currentNode.getPosition().y;
	        
	        for(int i=0; i<neighbours.size(); i++) {
	        	PathFindingNode nextNode = neighbours.get(i);
	        	
	        	float costToEnterNext = nextNode.getCostToEnter();
       		
	        	if (nextNode.getPosition().x != currentXpos && nextNode.getPosition().y != currentYpos)
	        		costToEnterNext *= 1.41421356f; // neighbour is on a diagonal
	        	
	        	float newCost = m_costSoFar.get(currentNode) + costToEnterNext;
	 
	        	// if not considered yet, or considered previously but cheaper on this encounter, add to queue/set cheaper value
	        	if (m_costSoFar.get(nextNode) == null || newCost < m_costSoFar.get(nextNode))
	        	{
	        		m_costSoFar.put(nextNode, newCost);
	        		float priority = newCost + heuristic(m_endNode.getPosition(), nextNode.getPosition());
	        		nextNode.setPriority(priority);
	        		m_queue.add(nextNode);
	        	    m_cameFrom.put(nextNode, currentNode);
	        	}
	        }	        
		}
	}
	
	
	public PriorityQueue<PathFindingNode> getPathFindingQueue()
	{
		return m_queue;
	}
	
	
	// get the positions of each node in the path
	// we start with the endnode, and work our way back in the chain of camefrom:s to null.
	// the result is the path between the start node and end node
	public void extractPath()
	{
       if (m_cameFrom.size() == 0)
    	   return;
       
        PathFindingNode node = m_cameFrom.get(m_endNode);
        m_path = new ArrayList<PointInt>();
        
        boolean done = false;
        while (!done) {
        	node = m_cameFrom.get(node);
        	if (node == null)
        		done = true;
        	else 
        	{
        		PathFindingNode pathFindingNode = m_grid[node.getPosition().x][node.getPosition().y];
        		if (pathFindingNode.getCostToEnter() != m_costToEnterNode)
        			m_path.add(node.getPosition());
        	}
        }
	}


	// the resulting path (m_path) is a list of neighbouring coordinates. As long as those coordinates moves in a straight line, 
	// we need only keep the start coordinate and end coordinate, and then we can use a line drawing function to draw the path.
	// This function records the start point, then create vectors that gets compared for direction. As long as their direction
	// is exactly the same, we continue to move along the path. When they differ, we store the end point and start over.
	public void createPathAsLinePoints()
	{
		if (m_path.size() < 2)
			return;
		
		m_pathAsLinePoints = new ArrayList<PointInt>();
		PointInt startPos =  m_path.get(0);
		m_pathAsLinePoints.add(new PointInt(startPos));
		
		PointInt currentPos = m_path.get(1);
		Vec2 currentVec = new Vec2((float)(currentPos.x - startPos.x), (float)(currentPos.y - startPos.y));
		currentVec.normalize();
		Vec2 newVec = new Vec2((float)(currentPos.x - startPos.x), (float)(currentPos.y - startPos.y));
		newVec.normalize();

		// arrow at end point
		Vec2 arrowVec = new Vec2(newVec.x, newVec.y);
		arrowVec = arrowVec.rotate(30.f);
		m_arrowPoint1 = new PointInt(currentPos.x + (int)(arrowVec.x * m_arrowLength), currentPos.y + (int)(arrowVec.y * m_arrowLength));
		arrowVec = new Vec2(newVec.x, newVec.y);
		arrowVec = arrowVec.rotate(-30.f);
		m_arrowPoint2 = new PointInt(currentPos.x + (int)(arrowVec.x * m_arrowLength), currentPos.y + (int)(arrowVec.y * m_arrowLength));
		
		boolean setNewVec = false;
		for(int i=2; i<m_path.size(); i++)
		{
			currentPos = m_path.get(i);
			newVec.x = (float)(currentPos.x - startPos.x);
			newVec.y = (float)(currentPos.y - startPos.y);
			newVec.normalize();
			if (setNewVec)
			{
				currentVec = new Vec2(newVec.x, newVec.y);
				currentVec.normalize();
				setNewVec = false;
			}
			float dot = newVec.dot(currentVec);
			if (dot < 1.0f || i == m_path.size() - 1)
			{
				// vectors differ, or last point in path. Store this point
				m_pathAsLinePoints.add(new PointInt(currentPos));
				startPos.x = currentPos.x;
				startPos.y = currentPos.y;
				setNewVec = true;
			}
		}
	}

	
	// puts a path point on the grid, with a thickness. The thickness discourages other edges from being placed right next to it
	public void addPathPointToGrid(PointInt pos)
	{
		int startX = pos.x - m_edgeThickness;
		if (startX < 0)
			startX = 0;
		int endX = pos.x + m_edgeThickness;
		if (endX >= m_gridXSize)
			endX = m_gridXSize - 1;
		
		int startY = pos.y - m_edgeThickness;
		if (startY < 0)
			startY = 0;
		int endY = pos.y + m_edgeThickness;
		if (endY >= m_gridYSize)
			endY = m_gridYSize - 1;

		for(int y = startY; y<=endY; y++)
			for(int x = startX; x<=endX; x++)
				m_grid[x][y].setCostToEnter(m_costToEnterEdge);
	}
	
	
	// put the path on the grid so that next edge path finding takes it into account
	public void addPathToGrid()
	{
		for(int i=0; i<m_path.size(); i++)
		{
			PointInt pos = m_path.get(i);
			if (m_grid[pos.x][pos.y].getCostToEnter() == m_costToEnterNode)
				continue; // line is inside node (happens at start and end)
			
			addPathPointToGrid(pos);
		}
	}
	
	
	// takes a path of line points and creates line segments for the edge
	public void addLineSegmentsToEdge(Edge edge)
	{
		if(m_pathAsLinePoints.size() < 2)
			return;
		
		for(int i=0; i<m_pathAsLinePoints.size() - 1; i++)
		{
			PointInt startPoint = m_pathAsLinePoints.get(i);
			PointInt endPoint = m_pathAsLinePoints.get(i + 1);
			edge.addLineSegment(startPoint, endPoint);
		}
		edge.addArrow(m_arrowPoint1, m_arrowPoint2);
	}
	
	
	// find the path of one edge between a start node and an end node
	public void findPath(Edge edge)
	{
		initFindPath(edge); // init values and containers
		boolean pathFound = findPath(); // main path finding
		addNodeToGrid(m_startNodeId); // put back the node on the grid (was removed in initFindPath(..))
		addNodeToGrid(m_endNodeId); // put back the node on the grid (was removed in initFindPath(..))
		if (pathFound)
		{
			extractPath(); // go through the m_cameFrom linked list container, starting with the endnode, and store each point
			createPathAsLinePoints(); // removes path finding nodes that lies on a straight line
			addLineSegmentsToEdge(edge); // creates line segments and stores them in the edge for later drawing
			addPathToGrid(); // puts the path on the grid so that the next path finding takes this edge into account.		
		}
		
	}
	
	// main path finding function
	public void findAllPaths()
	{
		// performance testing vars
		long startTime = System.currentTimeMillis();
		long elapsedTimePerEdge = 0L;
		m_elapsedTime = 0L;
		m_longestSearchTime = 0L;
		m_timeToFindEdgePath = new ArrayList<Long>();
		
		GraphHandler graphHandler = GraphHandler.getInstance();
		ArrayList<Edge> edges = graphHandler.getEdges();
		for(int i=0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			Node fromNode = graphHandler.getNode(edge.getFromId());
			Node toNode = graphHandler.getNode(edge.getToId());
			if (fromNode == null || toNode == null)
				continue;
			
			if (fromNode.getShipmentId() == -1 || toNode.getShipmentId() == -1)
				continue; // skip external objects for now
			
			findPath(edge);	
			
			// performance testing
			elapsedTimePerEdge = System.currentTimeMillis() - startTime;
			startTime = System.currentTimeMillis();
			if (elapsedTimePerEdge > m_longestSearchTime) {
				m_longestSearchTime = elapsedTimePerEdge;
				m_longestSearchTimeEdgeFrom = edge.getFromId();
				m_longestSearchTimeEdgeTo = edge.getToId();
			}
			m_timeToFindEdgePath.add(elapsedTimePerEdge);
			m_elapsedTime += elapsedTimePerEdge;
		}
	}
	
	
}
