
import java.util.*;


// a singleton that holds all nodes and edges used in the path finding. Also calculates drawing and path finding dimensions
public class GraphHandler {

	   
	private static GraphHandler m_instance = null;
  
	private HashMap<Integer, Node> m_nodes;
	private ArrayList<Edge> m_edges;  
	private int m_layoutWidth;
	private int m_layoutHeight;
	private HashMap<Integer, Integer> m_shipmentYOffset; // Y-offset for each shipment
	private HashMap<Integer, Integer> m_shipmentYPos; // cumulative Y-offset for each shipment
	private ArrayList<Integer> m_shipmentIds; // list of shipment ids. Draw order of shipments are as they appear in the xml-file, NOT in numerical order
	   
	private int m_shipmentYSeparation;
	private int m_shipmentXOffset;
	private int m_textSize;
	   
	   
	private GraphHandler() 
	{
		m_nodes = new HashMap<Integer, Node>();
		m_edges = new ArrayList<Edge>();
		m_shipmentYOffset = new HashMap<Integer, Integer>();
		m_shipmentYPos = new HashMap<Integer, Integer>();
		m_shipmentIds = new ArrayList<Integer>();
		m_shipmentYSeparation = 100;
		m_shipmentXOffset = 50;
		m_layoutWidth = 0;
		m_layoutHeight = 0;
		m_textSize = 12;    
	}

	   
	public static GraphHandler getInstance() 
	{
	   if(m_instance == null)
	       m_instance = new GraphHandler();
	   
	   return m_instance;
	}
	
	   
	// adds a node to the list and also works out shipment ids and dimensions. called from the file parser (XmlFileParser)
	public void addNode(String alias, String title, String wpResp, int id, int shipmentId, int xPos, int yPos, int side)
	{
		int xPosWithOffset = xPos + m_shipmentXOffset;
		m_nodes.put(id, new Node(alias, title, wpResp, id, shipmentId, xPosWithOffset, yPos, side));
		if (side == 0) {
			if (shipmentId >= 0) {

				if (m_shipmentYOffset.get(shipmentId) == null) {
					m_shipmentYOffset.put(shipmentId,  yPos);
					m_shipmentIds.add(shipmentId);
				}
				else {
					int shipmentYpos = m_shipmentYOffset.get(shipmentId);
					if (shipmentYpos < yPos)
						m_shipmentYOffset.put(shipmentId, yPos);
			   }
			}	   
		}
	}
	   
	   
	// add an edge to the list. Called from the file parser (XmlFileParser)
	public void addEdge(int fromId, int toId)
	{
		m_edges.add(new Edge(fromId, toId));
	}
	    
	   
	// calculate drawing and path finding positions and dimensions
	public void calcPositionsAndDimensions() 
	{   
		// calculate cumulative y-offsets for each shipment box (drawn on top of each other)
		int cumulativeYOffset = m_shipmentYSeparation;
		for (int i=0; i < m_shipmentIds.size(); i++) {
			
			int shipmentId = m_shipmentIds.get(i);
			if (m_shipmentYOffset.get(shipmentId) == null)
				break;
			else
			{
				if (i == 0)
					m_shipmentYPos.put(shipmentId,  m_shipmentYSeparation);
				else
				{
					int shipmentIdPrevious = m_shipmentIds.get(i - 1);
					cumulativeYOffset += m_shipmentYOffset.get(shipmentIdPrevious) + m_shipmentYSeparation;
					m_shipmentYPos.put(shipmentId,  cumulativeYOffset);
				}
			}
		}

		// calculate node drawing values and the global layout width and height
		for(Map.Entry<Integer, Node> entry : m_nodes.entrySet()) {

			Node node = (Node)entry.getValue();
			if (node.getShipmentId() == -1)
			   continue; // no external objects for now
   
			int yPos = node.getYPos();
			yPos += m_shipmentYPos.get(node.getShipmentId());
			node.setYPos(yPos);
			node.calcDrawingParams();
			
			NodeRect nodeRect = node.getNodeRect();
			int nodeXmax = nodeRect.getXpos() + nodeRect.getWidth();
			int nodeYmax = nodeRect.getYpos() + nodeRect.getHeight();
			if (nodeXmax > m_layoutWidth)
				m_layoutWidth = nodeXmax + 1;
			if (nodeYmax > m_layoutHeight)
				m_layoutHeight = nodeYmax + 1;
		} 
		
		// calculate edge start/end positions
		for(int i=0; i < m_edges.size(); i++) {
			Edge edge = m_edges.get(i);
			Node fromNode = m_nodes.get(edge.getFromId());
			Node toNode = m_nodes.get(edge.getToId());
			if (fromNode == null || toNode == null)
				continue;
			if (fromNode.getShipmentId() == -1 || toNode.getShipmentId() == -1)
				continue; // skip external objects for now
			
			NodeRect fromNodeRect = fromNode.getNodeRect();
			edge.setFromPos(fromNodeRect.getCenterXpos(),  fromNodeRect.getCenterYpos());
			
			NodeRect toNodeRect = toNode.getNodeRect();
			edge.setToPos(toNodeRect.getCenterXpos(),  toNodeRect.getCenterYpos());
		}
	}
	   
	   
	public HashMap<Integer, Node> getNodes() 
	{
		return m_nodes;
	}
   

	public ArrayList<Edge> getEdges()
    {
	    return m_edges;
    }
	   
  
	public int getNodeCount() 
    {   
	    return m_nodes.size();
    }
	   
	   
	public int getEdgeCount() 
	{	   
		return m_edges.size();
	}
	   
	   
	public int getTextSize() 
	{
		return m_textSize;
	}
	   
   
	public int getLayoutWidth()
 	{
		return m_layoutWidth;
	}
   
   
	public int getLayoutHeight()
	{
		return m_layoutHeight;
	}
   
   
	public boolean isPosInsideNode(int x, int y, int nodeId)
	{
		Node node = m_nodes.get(nodeId);
		if (node == null)
			return false;
	   
		return node.getNodeRect().isInsideRect(x,  y);
	}
   
	
	public Node getNode(int nodeId)
	{
		return m_nodes.get(nodeId);
	}

   
}
