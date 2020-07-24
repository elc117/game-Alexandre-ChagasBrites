package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FloatAttribute extends MaterialAttribute
{
	public float value;
	
	public FloatAttribute(float value)
	{
		this.value = value;
	}
	
	public void set(String key, ShaderProgram shader) 
	{
		shader.setUniformf(key, value);
	}
}