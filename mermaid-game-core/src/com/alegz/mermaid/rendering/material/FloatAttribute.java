package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FloatAttribute extends MaterialAttribute
{
	public float value;
	
	public FloatAttribute(String key, float value)
	{
		this.key = key;
		this.value = value;
	}
	
	public void set(ShaderProgram shader) 
	{
		shader.setUniformf(key, value);
	}
}