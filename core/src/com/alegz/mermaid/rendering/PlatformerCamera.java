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
	
	private Matrix4 screenMatrix;
	private Matrix4 projMatrix;
	private Matrix4 uiMatrix;
	private float uiWidth, uiHeight;
	
	private Matrix4 screenToWorldMatrix;
	private Matrix4 screenToUIMatrix;
	private Matrix4 worldToUIMatrix;
	
	public Color backgroundColor;
	
	public PlatformerCamera()
	{
		position = new Vector2(0, 0);
		rotation = 0;
		
		size = new Vector2(1, 1);
		nearPlane = 0.1f;
		farPlane = 10.0f;
		
		screenMatrix = new Matrix4();
		projMatrix = new Matrix4();
		uiMatrix = new Matrix4();
		
		screenToWorldMatrix = new Matrix4();
		screenToUIMatrix = new Matrix4();
		worldToUIMatrix = new Matrix4();
		
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
	
	public void setPixelPerfectMatrix(int width, int height, int size)
	{
		int cell = height / size;
		float multiplier = (float)Math.ceil((float)cell / 16.0f);
		setProjMatrix(width, height, height / (32.0f * multiplier));
		setUIMatrix(width / (int)multiplier, height / (int)multiplier);
	}
	
	public void setProjMatrix(float width, float height, float size)
	{
		this.size.x = size * width / height;
		this.size.y = size;
		setProjMatrix(width, height);
	}
	
	public void setProjMatrix(float width, float height)
	{
		projMatrix.idt();
		projMatrix.mul(perspectiveMatrix(nearPlane, farPlane));
		projMatrix.scl(1.0f / this.size.x, 1.0f / this.size.y, 1.0f);
		projMatrix.rotate(0.0f, 0.0f, 1.0f, -rotation);
		projMatrix.translate(-position.x, -position.y, 1.0f);
		
		screenMatrix.idt();
		screenMatrix.translate(width * 0.5f, height * 0.5f, 0.0f);
		screenMatrix.scale(width * 0.5f, -height * 0.5f, 1.0f);
		
		screenToWorldMatrix = screenMatrix.cpy();
		screenToWorldMatrix.mul(projMatrix);
		screenToWorldMatrix.inv();
	}
	
	public void setUIMatrix(float width, float height)
	{
		uiWidth = width;
		uiHeight = height;
		
		uiMatrix.idt();
		uiMatrix.translate(-1.0f, -1.0f, 0.0f);
		uiMatrix.scale(2.0f / width, 2.0f / height, 1.0f);
		
		screenToUIMatrix = screenMatrix.cpy();
		screenToUIMatrix.inv();
		
		Matrix4 invUI = uiMatrix.cpy();
		invUI.inv();
		screenToUIMatrix.mulLeft(invUI);
		
		worldToUIMatrix = projMatrix.cpy();
		worldToUIMatrix.mulLeft(invUI);
	}
	
	public Matrix4 getProjMatrix()
	{
		return projMatrix;
	}
	
	public Matrix4 getUIMatrix()
	{
		return uiMatrix;
	}
	
	public Vector2 getSize()
	{
		return size;
	}
	
	public float getUIWidth()
	{
		return uiWidth;
	}
	
	public float getUIHeight()
	{
		return uiHeight;
	}
	
	public Vector2 getScreenToWorldPosition(float x, float y)
	{
		Vector3 position = new Vector3(x, y, projMatrix.getValues()[14]);
		position.mul(screenToWorldMatrix);
		return new Vector2(position.x, position.y);
	}
	
	public Vector2 getScreenToUIPosition(float x, float y)
	{
		Vector3 position = new Vector3(x, y, 0);
		position.mul(screenToUIMatrix);
		return new Vector2(position.x, position.y);
	}
	
	public Vector2 getWorldToUIPosition(float x, float y)
	{
		Vector3 position = new Vector3(x, y, 0);
		position.mul(worldToUIMatrix);
		return new Vector2(position.x, position.y);
	}
}
