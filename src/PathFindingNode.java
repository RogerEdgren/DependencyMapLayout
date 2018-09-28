
import java.util.*;


// the main path finding object. holds a cost to enter it, a position in the path finding grid, as well as all its neighbours
// during path finding it also records the priority (f = g + h) of this node, given the current search state.
public class PathFindingNode {

	private PointInt m_position;
	private ArrayList<PathFindingNode> m_neighbours;
	private float m_costToEnter;
	private float m_priority;
	
	
	public PathFindingNode(int x, int y)
	{
		m_position = new PointInt(x, y);
		m_neighbours = new ArrayList<PathFindingNode>();
		m_costToEnter = 1.f;
		m_priority = 0.f;
	}
	
	
	public void setCostToEnter(float cost)
	{
		m_costToEnter = cost;
	}
	
	
	public float getCostToEnter()
	{
		return m_costToEnter;
	}
	
	
	public void setNeighbours(ArrayList<PathFindingNode> neighbours)
	{
		m_neighbours = neighbours;
	}
	
	
	public ArrayList<PathFindingNode> getNeighbours()
	{
		return m_neighbours;
	}
	
	
	public void setPriority(float priority)
	{
		m_priority = priority;
	}
	
	
	public float getPriority()
	{
		return m_priority;
	}
	
	
	public PointInt getPosition()
	{
		return m_position;
	}
}
