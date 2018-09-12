
import java.util.*;

public class GraphHandler {

	   private static GraphHandler m_instance = null;

	   private HashMap<Integer, Node> m_nodes = new HashMap<Integer, Node>();
	   private ArrayList<Edge> m_edges;
	   
	   private GraphHandler() {

		   m_nodes = new HashMap<Integer, Node>();
		   m_edges = new ArrayList<Edge>();
	   }

	   
	   public static GraphHandler getInstance() {
	      if(m_instance == null) {
	         m_instance = new GraphHandler();
	      }
	      return m_instance;
	   }
	
	   
	   void addNode(String title, int id, int shipmentId, int xPos, int yPos, int side)
	   {
		   m_nodes.put(id, new Node(title, id, shipmentId, xPos, yPos, side));
	   }
	   
	   
	   void addEdge(int fromId, int toId)
	   {
		   m_edges.add(new Edge(fromId, toId));
		   Node fromNode = m_nodes.get(fromId);
		   fromNode.addEdgeToNode(toId);
		   Node toNode = m_nodes.get(toId);
		   toNode.addEdgeFromNode(fromId);
	   }
	   
	   
	   int getNodeCount() {
		   
		   return m_nodes.size();
	   }
	   
	   
	   int getEdgeCount() {
		   
		   return m_edges.size();
	   }
}
