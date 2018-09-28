import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.Set;
import processing.core.PApplet;
import processing.core.PFont;


// main class. Uses Processing.core to get a convenient interface for drawing stuff to the screen
// Uses callbacks that Processing calls to set things up, draw stuff and handle mouse clicks and dragging 
public class Main extends PApplet {

	private static boolean m_initDone; // because Processing callbacks runs on a separate thread
	private static boolean m_mouseDragging;
	private static int m_drawXOffset;
	private static int m_drawYOffset;
	private static int m_mouseYOffset;
	private static int m_mouseXOffset;
	private static PointInt m_windowDimensions;
	
	
	public static void main(String[] args) 
	{
		m_windowDimensions = new PointInt(1000, 900);
		
		PApplet.main("Main");
		 
		m_mouseDragging = false;
		m_initDone = false;
		m_drawXOffset = 0;
		m_drawYOffset = 0;
		m_mouseXOffset = 0;
		m_mouseYOffset = 0;
		
		XmlFileParser.parseFile("data\\Concept Car.ana");
//		XmlFileParser.parseFile("data\\anatomianatomi m inkrement.ana");
		
		
		GraphHandler graphHandler = GraphHandler.getInstance();
		graphHandler.calcPositionsAndDimensions();
		 
		//int nodeCount = graphHandler.getNodeCount();
		//int edgeCount = graphHandler.getEdgeCount();
		 
		EdgePathFinder edgePathFinder = EdgePathFinder.getInstance();
		edgePathFinder.initGrid();
		edgePathFinder.findAllPaths();
		 
		m_initDone = true;
	}

	
	// Processing callbacks start
	// ----------------------------------------------------------------------------------------------------------
	 public void settings()
	 {
		 size(m_windowDimensions.x, m_windowDimensions.y);
	 }

	 public void setup() 
	 {
		 //String[] fontList = PFont.list();
		 // printArray(fontList);
		 GraphHandler graphHandler = GraphHandler.getInstance();
		 PFont myFont;
		 myFont = createFont("Courier New Bold", graphHandler.getTextSize());
		 textFont(myFont);
	 }

	 public void draw() 
	 {
				 
		 if (!m_initDone)
			 return;
		 
		 background(200, 200, 200);
		 strokeWeight(1);
		 GraphHandler graphHandler = GraphHandler.getInstance();
		 int textSize = graphHandler.getTextSize(); 
		 textSize(textSize);
		 
		 if (!mousePressed && m_mouseDragging) 
			 m_mouseDragging = false;
		 
		 if (m_mouseDragging) {
			 
			 m_mouseXOffset = m_mouseXOffset - pmouseX + mouseX;
			 m_mouseYOffset = m_mouseYOffset - pmouseY + mouseY;

			 int minDrawXOffset = -(graphHandler.getLayoutWidth() - m_windowDimensions.x);
			 if (m_mouseXOffset < minDrawXOffset)
				 m_mouseXOffset = minDrawXOffset;
			 
			 int minDrawYOffset = -(graphHandler.getLayoutHeight() - m_windowDimensions.y + 250);
			 if (m_mouseYOffset < minDrawYOffset)
				 m_mouseYOffset = minDrawYOffset;

			 if (m_mouseXOffset > 0)
				 m_mouseXOffset = 0;
			 
			 if (m_mouseYOffset > 0)
				 m_mouseYOffset = 0;

		 }
		 
		 m_drawXOffset = m_mouseXOffset;
		 m_drawYOffset = m_mouseYOffset + 100;
		 stroke(0, 0, 0);
		 // draw the nodes
		 HashMap<Integer, Node> nodes = graphHandler.getNodes();
		 for(Map.Entry<Integer, Node> entry : nodes.entrySet()) {

			 Node node = (Node)entry.getValue();
			 if (node.getShipmentId() == -1)
				 continue; // skip external objects for now

			 String alias = node.getAlias();
			 ArrayList<String> title = node.getTitle();
			 String wpResp = node.getWpResp();
			 int xPos = node.getXPos() + m_drawXOffset;
			 int yPos = node.getYPos() + m_drawYOffset;
			 
			 fill(250, 250, 250);
			 NodeRect nodeRect = node.getNodeRect();
			 rect(nodeRect.getXpos() + m_drawXOffset, nodeRect.getYpos() + m_drawYOffset, nodeRect.getWidth(), nodeRect.getHeight());
			 fill(0, 0, 150);
			 text(alias, xPos, yPos);
			 yPos += textSize;
			 for(int i=0; i<title.size(); i++) {
				 String titleString = title.get(i);
				 text(titleString, xPos, yPos);
				 yPos += textSize;
			 }
			 text(wpResp, xPos, yPos);
		 }
		
		 // draw the edges
		 ArrayList<Edge> edges = graphHandler.getEdges();
		 for(int i=0; i<edges.size(); i++) {
			 Edge edge = edges.get(i);
			 ArrayList<LineInt> lineSegments = edge.getLineSegments();
			 if (lineSegments.size() == 0)
				 continue;
			 
			 LineInt firstLine = lineSegments.get(0);
			 PointInt arrowPoint1 = edge.getArrowPoint1();
			 PointInt arrowPoint2 = edge.getArrowPoint2();
			 line(firstLine.startX + m_drawXOffset, firstLine.startY + m_drawYOffset, arrowPoint1.x + m_drawXOffset, arrowPoint1.y + m_drawYOffset);
			 line(firstLine.startX + m_drawXOffset, firstLine.startY + m_drawYOffset, arrowPoint2.x + m_drawXOffset, arrowPoint2.y + m_drawYOffset);

			 
			 for(int j=0; j<lineSegments.size(); j++) {
				 LineInt lineInt = lineSegments.get(j);
				 line(lineInt.startX + m_drawXOffset, lineInt.startY + m_drawYOffset, lineInt.endX + m_drawXOffset, lineInt.endY + m_drawYOffset);
			 }
		 }

		 // draw performance results
		 strokeWeight(2);
		 EdgePathFinder edgePathFinder = EdgePathFinder.getInstance();
		 long totalElapsedTime = edgePathFinder.getTotalElapsedTime();		 
		 long longestSearchTime = edgePathFinder.getLongestSearchTime();
		 ArrayList<Long> elapsedTimePerEdges = edgePathFinder.getElapsedTimeArray();
		 String totalElapsedTimeAsString = Long.toString(totalElapsedTime);
		 int elapsedTimeEdgeCount = elapsedTimePerEdges.size();
		 text("Number of edges: " + Integer.toString(elapsedTimeEdgeCount), 10 + m_mouseXOffset, 20 + m_mouseYOffset);
		 text("Total path finding time: " + totalElapsedTimeAsString + "ms", 10 + m_mouseXOffset, 35 + m_mouseYOffset);
		 String longestSearchTimeAsString = Long.toString(longestSearchTime);
		 float averageSearchTime = (float)totalElapsedTime / (float)elapsedTimeEdgeCount;
		 text("Average search time: " + Float.toString(averageSearchTime) + "ms", 10 + m_mouseXOffset, 50 + m_mouseYOffset);
		 text("Longest search time: " + longestSearchTimeAsString + "ms", 10 + m_mouseXOffset, 65 + m_mouseYOffset);
		 int longestSearchTimeFrom = edgePathFinder.getLongestSearchTimeEdgeFrom();
		 String longestSearchTimeFromString = Integer.toString(longestSearchTimeFrom);
		 int longestSearchTimeTo = edgePathFinder.getLongestSearchTimeEdgeTo();
		 String longestSearchTimeToString = Integer.toString(longestSearchTimeTo);
		 text("Longest search time edge from: " + longestSearchTimeFromString, 10 + m_mouseXOffset, 80 + m_mouseYOffset);
		 text("Longest search time edge to: " + longestSearchTimeToString, 10 + m_mouseXOffset, 95 + m_mouseYOffset);

		 float scale = 175.f / (float)longestSearchTime;
		 for (int i=0; i<elapsedTimeEdgeCount; i++) {
			 int yPos = (int)((float)elapsedTimePerEdges.get(i).intValue() * scale);
			 line((i * 2) + 250 + m_mouseXOffset, 175 + m_mouseYOffset, (i * 2) + 250 + m_mouseXOffset, 175 - yPos + m_mouseYOffset);
		 }
			 
//		 edgePathFinder.debugDrawGrid(this, m_drawYOffset);	 

	 }
	 
	 public void mousePressed()
	 {
		 m_mouseDragging = true;
	 }
	 // ----------------------------------------------------------------------------------------------------------
	// Processing callbacks end
}
