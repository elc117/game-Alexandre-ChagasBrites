package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ColorAttribute extends MaterialAttribute
{
	public Color color;
	
	public ColorAttribute(Color color)
	{
		this.color = color;
	}
	
	public void set(String key, ShaderProgram shader) 
	{
		shader.setUniformf(key, color);
	}
}
