package com.alegz.mermaid.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PlatformerCamera 
{
	public Vector2 position;
	public float rotation;
	
	private Vector2 size;
	public float nearPlane, farPlane;
	
	private Matrix4 projMatrix;
	private Matrix4 screenMatrix;
	private Matrix4 worldMatrix;
	
	public Color backgroundColor;
	
	public PlatformerCamera()
	{
		position = new Vector2(0, 0);
		rotation = 0;
		
		size = new Vector2(1, 1);
		nearPlane = 0.1f;
		farPlane = 10.0f;
		
		projMatrix = new Matrix4();
		screenMatrix = new Matrix4();
		worldMatrix = new Matrix4();
		
		backgroundColor = Color.BLACK.cpy();
	}
	
	private Matrix4 perspectiveMatrix(float zNear, float zFar)
	{
		Matrix4 perspectiveMatrix = new Matrix4(new float[] {
				1, 0, 0					 			       , 0,
				0, 1, 0					 			       , 0,
				0, 0, (zFar + zNear) / (zFar - zNear)      , 1,
				0, 0, -2.0f * zFar * zNear / (zFar - zNear), 0
		});
		return perspectiveMatrix;
	}
	
	public void update(float width, float height, float size)
	{
		this.size.x = size * width / height;
		this.size.y = size;
		update(width, height);
	}
	
	public void update(float width, float height)
	{
		projMatrix.idt();
		projMatrix.mul(perspectiveMatrix(nearPlane, farPlane));
		projMatrix.scl(1.0f / this.size.x, 1.0f / this.size.y, 1.0f);
		projMatrix.rotate(0, 0, 1, -rotation);
		projMatrix.translate(-position.x, -position.y, 0);
		
		screenMatrix.idt();
		screenMatrix.translate(width * 0.5f, height * 0.5f, 0);
		screenMatrix.scale(width * 0.5f, -height * 0.5f, 1);
		screenMatrix.mul(projMatrix);
		
		worldMatrix = screenMatrix.cpy();
		worldMatrix.inv();
	}
	
	public Matrix4 getProjMatrix()
	{
		return projMatrix;
	}
	
	public Vector2 getSize()
	{
		return size;
	}
	
	public Vector2 getScreenPosition(float x, float y)
	{
		Vector3 position = new Vector3(x, y, 0);
		position.mul(screenMatrix);
		return new Vector2(position.x, position.y);
	}
	
	public Vector2 getWorldPosition(float x, float y)
	{
		Vector3 position = new Vector3(x, y, 0);
		position.mul(worldMatrix);
		return new Vector2(position.x, position.y);
	}
}
