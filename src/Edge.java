import java.util.ArrayList;


// the main point of Edge is to hold a series of line segments that connects two nodes
// the line segments are calculated in EdgePathFinder.
public class Edge {

	private int m_fromId;
	private int m_toId;
	private PointInt m_fromPos;
	private PointInt m_toPos;
	private ArrayList<LineInt> m_lineSegments; // a list of line segments that connects two nodes (m_fromId -> m_toId)

	private PointInt m_arrowPoint1; // Two lines to draw an arrow at the m_toId node.
	private PointInt m_arrowPoint2;
	
	public Edge(int fromId, int toId) 
	{
		m_fromId = fromId;
		m_toId = toId;
		m_fromPos = new PointInt();
		m_toPos = new PointInt();
		m_lineSegments = new ArrayList<LineInt>();
	}
	
	
	public int getFromId()
	{
		return m_fromId;
	}
	
	
	public int getToId()
	{
		return m_toId;
	}
	
	
	public void setFromPos(int x, int y)
	{
		m_fromPos.x = x;
		m_fromPos.y = y;
	}
	
	
	public PointInt getFromPos()
	{
		return m_fromPos;
	}
	
	public PointInt getToPos()
	{
		return m_toPos;
	}

	
	public void setToPos(int x, int y)
	{
		m_toPos.x = x;
		m_toPos.y = y;
	}
	
	
	public void addLineSegment(PointInt startPoint, PointInt endPoint)
	{
		m_lineSegments.add(new LineInt(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
	}
	
	
	public ArrayList<LineInt> getLineSegments()
	{
		return m_lineSegments;
	}
	
	
	public void addArrow(PointInt arrowPoint1, PointInt arrowPoint2)
	{
		m_arrowPoint1 = arrowPoint1;
		m_arrowPoint2 = arrowPoint2;
	}
	
	
	public PointInt getArrowPoint1()
	{
		return m_arrowPoint1;
	}
	
	
	public PointInt getArrowPoint2()
	{
		return m_arrowPoint2;
	}

}
