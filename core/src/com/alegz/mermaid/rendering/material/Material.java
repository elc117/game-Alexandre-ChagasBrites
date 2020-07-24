package com.alegz.mermaid.rendering.material;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Material 
{
	private ShaderProgram shader;
	private List<MaterialAttribute> attributes;
	
	public Material(ShaderProgram shader)
	{
		this.shader = shader;
		attributes = new ArrayList<MaterialAttribute>();
	}
	
	public ShaderProgram getShader()
	{
		return shader;
	}
	
	public void setAttributes()
	{
		for (MaterialAttribute attribute : attributes)
			attribute.set(shader);
	}
	
	public void setColor(String key, Color color)
	{
		for (MaterialAttribute attribute : attributes)
		{
			if (ColorAttribute.class.isInstance(attribute) && attribute.key == key)
			{
				ColorAttribute.class.cast(attribute).color = color;
				return;
			}
		}
		attributes.add(new ColorAttribute(key, color));
	}
	
	public void setFloat(String key, float value)
	{
		for (MaterialAttribute attribute : attributes)
		{
			if (FloatAttribute.class.isInstance(attribute) && attribute.key == key)
			{
				FloatAttribute.class.cast(attribute).value = value;
				return;
			}
		}
		attributes.add(new FloatAttribute(key, value));
	}
}
