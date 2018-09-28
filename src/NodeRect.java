
// holds the position and dimension of a node
public class NodeRect {

	private int m_xPos;
	private int m_yPos;
	private int m_width;
	private int m_height;
	private int m_centerXpos;
	private int m_centerYpos;
	
	
	public NodeRect(int xPos, int yPos, int width, int height) 
	{
		m_xPos = xPos;
		m_yPos = yPos;
		m_width = width;
		m_height = height;
		m_centerXpos = xPos + (width / 2);
		m_centerYpos = yPos + (height / 2);
	}
	
	
	public boolean isInsideRect(int xPos, int yPos)
	{
		return (xPos >= m_xPos && xPos <= (m_xPos + m_width) && yPos >= m_yPos && yPos <= (m_yPos + m_height));
	}
	
	
	public int getCenterXpos() 
	{
		return m_centerXpos;
	}
	
	
	public int getCenterYpos() 
	{
		return m_centerYpos;
	}
	
	
	public int getXpos() 
	{
		return m_xPos;
	}

	
	public int getYpos() 
	{
		return m_yPos;
	}

	
	public int getWidth() 
	{
		return m_width;
	}

	
	public int getHeight() 
	{
		return m_height;
	}

}
