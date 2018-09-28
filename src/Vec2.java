

// a two dimensional vector class mainly used by the path finding algorithm to 
// remove nodes that lie between the end points of a straight line
public class Vec2 {

	public float x;
	public float y;
	
	public Vec2()
	{
		x = 0.f;
		y = 0.f;
	}
	
	
	public Vec2(float _x, float _y)
	{
		x = _x;
		y = _y;
	}
	
	
	public float len() 
	{
		return (float)Math.sqrt(x * x + y * y);
	}

	
	public float len2() 
	{
		return x * x + y * y;
	}
	
	
	public Vec2 sub(Vec2 v) 
	{
		x -= v.x;
		y -= v.y;
		return this;
	}

	
	public Vec2 add(Vec2 v) 
	{
		x += v.x;
		y += v.y;
		return this;
	}

	
	public float dot(Vec2 v) 
	{
		return x * v.x + y * v.y;
	}
	

	public Vec2 scl(float scalar) 
	{
		x *= scalar;
		y *= scalar;
		return this;
	}

	
	public Vec2 scl(Vec2 v) 
	{
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	
	public Vec2 mulAdd(Vec2 v, float scalar) 
	{
		this.x += v.x * scalar;
		this.y += v.y * scalar;
		return this;
	}

	
	public float distanceToVec(Vec2 v) 
	{
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	
	public Vec2 normalize()
	{
		float magnitude = len();
		x = x / magnitude;
		y = y / magnitude;
		return this;
	}
	
	
	public float cross(Vec2 v) 
	{
		return x * v.y - y * v.x;
	}
	

	public Vec2 rotate(float degrees) 
	{
		return rotateRad(Math.toRadians(degrees));
	}

	
	public Vec2 rotateRad(double radians) 
	{
		float cos = (float)Math.cos(radians);
		float sin = (float)Math.sin(radians);
		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;
		this.x = newX;
		this.y = newY;

		return this;
	}
	
}
