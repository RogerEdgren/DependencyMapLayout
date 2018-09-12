
import java.util.*;

public class Node {

	private int m_id;
	private int m_shipmentId;
	private String m_title;
	private int m_xPos;
	private int m_yPos;
	private int m_side;
	
	private ArrayList<Integer> m_edgesFromNodes;
	private ArrayList<Integer> m_edgesToNodes;

	
	public Node(String title, int id, int shipmentId, int xPos, int yPos, int side) {
		m_id = id;
		m_shipmentId = shipmentId;
		m_title = title;
		m_xPos = xPos;
		m_yPos = yPos;
		m_side = side;
		
		m_edgesFromNodes = new ArrayList<Integer>();
		m_edgesToNodes = new ArrayList<Integer>();
	}

	public void addEdgeFromNode(int id) {
		m_edgesFromNodes.add(id);
	}
	
	public void addEdgeToNode(int id) {
		m_edgesToNodes.add(id);
	}
	
}
