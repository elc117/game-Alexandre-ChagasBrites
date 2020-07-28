package com.alegz.mermaid;

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
	
	public Rect cpy()
	{
		return new Rect(x, y, width, height);
	}
}
