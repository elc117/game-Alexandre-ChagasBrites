package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class FloatArrayAttribute extends MaterialAttribute
{
	public float[] values;
	public int length;
	
	public FloatArrayAttribute(float[] values, int length)
	{
		this.values = values;
		this.length = length;
	}
	
	public void set(String key, ShaderProgram shader) 
	{
		shader.setUniform1fv(key, values, 0, length);
	}
}
