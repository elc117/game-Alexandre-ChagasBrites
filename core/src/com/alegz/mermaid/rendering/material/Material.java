package com.alegz.mermaid.rendering.material;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Material 
{
	private ShaderProgram shader;
	private HashMap<String, MaterialAttribute> attributes;
	
	public Material(ShaderProgram shader)
	{
		this.shader = shader;
		attributes = new HashMap<String, MaterialAttribute>();
	}
	
	public ShaderProgram getShader()
	{
		return shader;
	}
	
	public void setAttributes()
	{
		for (String key : attributes.keySet())
			attributes.get(key).set(key, shader);
	}
	
	public void setColor(String key, Color color)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			ColorAttribute colorAttribute = (ColorAttribute) attribute;
			colorAttribute.color = color;
			return;
		}
		attributes.put(key, new ColorAttribute(color));
	}
	
	public void setFloat(String key, float value)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			FloatAttribute floatAttribute = (FloatAttribute) attribute;
			floatAttribute.value = value;
			return;
		}
		attributes.put(key, new FloatAttribute(value));
	}
}
