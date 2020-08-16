package com.alegz.mermaid;

import com.badlogic.gdx.math.Vector2;

public class Rect 
{
	public float x;
	public float y;
	
	public float width;
	public float height;
	
	public Rect(float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(Vector2 vector)
	{
		return vector.x > x &&
			   vector.y > y &&
			   vector.x < x + width &&
			   vector.y < y + height;
	}
	
	public boolean intersects(Rect rect)
	{
		return x < rect.x + rect.width &&
			   y < rect.y + rect.height &&
			   x + width  > rect.x &&
			   y + height > rect.y;
	}
	
	public Rect cpy()
	{
		return new Rect(x, y, width, height);
	}
}
