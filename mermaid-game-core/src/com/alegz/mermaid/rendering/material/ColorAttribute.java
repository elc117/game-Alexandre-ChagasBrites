package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ColorAttribute extends MaterialAttribute
{
	public Color color;
	
	public ColorAttribute(String key, Color color)
	{
		this.key = key;
		this.color = color;
	}
	
	public void set(ShaderProgram shader) 
	{
		shader.setUniformf(key, color);
	}
}
