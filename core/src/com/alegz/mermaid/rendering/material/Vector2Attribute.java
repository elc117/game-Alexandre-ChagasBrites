package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Vector2Attribute extends MaterialAttribute
{
	public float x;
	public float y;
	
	public Vector2Attribute(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void set(String key, ShaderProgram shader) 
	{
		shader.setUniformf(key, x, y);
	}
}
