
import java.util.*;


// holds information about a node, such as its text content, dimensions and x,y-coordinate
public class Node {

	private int m_id;
	private int m_shipmentId;
	private String m_alias;
	private ArrayList<String> m_title;
	private String m_wpResp;
	private int m_xPos;
	private int m_yPos;
	private int m_side;
	private int m_maxTextLength;
	private NodeRect m_nodeRect = null;

	
	public Node(String alias, String title, String wpResp, int id, int shipmentId, int xPos, int yPos, int side) 
	{
		m_id = id;
		m_shipmentId = shipmentId;
		m_alias = alias;
		m_wpResp = wpResp;
		m_xPos = xPos;
		m_yPos = yPos;
		m_side = side;
		m_maxTextLength = 10;
//		buildTitleStrings("12345 67890 12345 67890 12345 67890 12345 67890A");
		buildTitleStrings(title);

	}

	// if the title is too long, we chop it up into pieces to get a node that is narrower. 
	// This helps the path finding function a lot by allowing it to backtrack less and 
	// "fall over the edge" of the node sooner on its way to the end node.
	public void buildTitleStrings(String title)
	{
		int maxStringLength = m_maxTextLength;
		m_title = new ArrayList<String>();

		while(title.length() > maxStringLength) {
			int startOfSpace = title.indexOf(32, maxStringLength - 1);  // look for space
			
			if (startOfSpace == -1) // no space in remaining string
			{
				if (title.length() > m_maxTextLength)
					m_maxTextLength = title.length();
				break;
			}

			if (startOfSpace > maxStringLength) {
				m_maxTextLength = startOfSpace;
				maxStringLength = m_maxTextLength;
			}
			
			String titleFront = title.substring(0,  startOfSpace);
			
			m_title.add(titleFront);
			title = title.substring(startOfSpace + 1, title.length());		
		}
		m_title.add(title);
	
	}

	
	public void calcDrawingParams() 
	{
		GraphHandler graphHandler = GraphHandler.getInstance();
		 		
		if (m_title.size() == 1) {
			String title = m_title.get(0);
			m_maxTextLength = title.length();
			
			if(m_alias.length() > title.length() && m_alias.length() > m_wpResp.length())
				m_maxTextLength = m_alias.length();
			else if(m_wpResp.length() > title.length() && m_wpResp.length() > m_alias.length())
				m_maxTextLength = m_wpResp.length();
		}
		int textLinesCount = m_title.size() + 2;
		m_nodeRect = new NodeRect(m_xPos - 5, m_yPos - graphHandler.getTextSize(), m_maxTextLength * 8, (graphHandler.getTextSize() * textLinesCount) + 5);	
	}
	

	public int getMaxTextLength()
	{
		return m_maxTextLength;
	}
	
	
	public String getAlias() 
	{
		return m_alias;
	}
	
	public ArrayList<String> getTitle()
	{
		return m_title;
	}
	
	
	public String getWpResp() 
	{
		return m_wpResp;
	}
	
	
	public int getXPos() 
	{
		return m_xPos;
	}
	
	
	public int getYPos() 
	{
		return m_yPos;
	}
	
	
	public void setYPos(int yPos) 
	{
		m_yPos = yPos;
	}
	
	
	public int getShipmentId() 
	{
		return m_shipmentId;
	}
	
	
	public NodeRect getNodeRect() 
	{
		return m_nodeRect;
	}
}
