

// a super simple class used for storing points in a container 
public class PointInt {

	public int x;
	public int y;
	
	public PointInt()
	{
		x = 0;
		y = 0;
	}
	
	
	public PointInt(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
	
	
	public PointInt(PointInt point)
	{
		x = point.x;
		y = point.y;
	}
	
	
}
