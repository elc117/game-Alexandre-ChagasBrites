package com.alegz.mermaid.rendering.material;

import java.util.HashMap;

import com.alegz.mermaid.rendering.Shader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;

public class Material 
{
	private Shader shader;
	private HashMap<String, MaterialAttribute> attributes;
	
	public Material(Shader shader)
	{
		this.shader = shader;
		attributes = new HashMap<String, MaterialAttribute>();
	}
	
	public Shader getShader()
	{
		return shader;
	}
	
	public void setAttributes()
	{
		for (String key : attributes.keySet())
			attributes.get(key).set(key, shader.getProgram());
	}
	
	public void setFloat(String key, float value)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			FloatAttribute floatAttribute = (FloatAttribute)attribute;
			floatAttribute.value = value;
			return;
		}
		attributes.put(key, new FloatAttribute(value));
	}
	
	public void setFloatArray(String key, float[] values, int length)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			FloatArrayAttribute floatArratAttribute = (FloatArrayAttribute)attribute;
			floatArratAttribute.values = values;
			floatArratAttribute.length = length;
			return;
		}
		attributes.put(key, new FloatArrayAttribute(values, length));
	}
	
	public void setMatrix(String key, Matrix4 matrix)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			MatrixAttribute floatArratAttribute = (MatrixAttribute)attribute;
			floatArratAttribute.matrix = matrix;
			return;
		}
		attributes.put(key, new MatrixAttribute(matrix));
	}
	
	public void setColor(String key, Color color)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			ColorAttribute colorAttribute = (ColorAttribute)attribute;
			colorAttribute.color = color;
			return;
		}
		attributes.put(key, new ColorAttribute(color));
	}
	
	public void setTexture(String key, Texture texture, int unit)
	{
		MaterialAttribute attribute = attributes.get(key);
		if (attribute != null)
		{
			TextureAttribute textureAttribute = (TextureAttribute)attribute;
			textureAttribute.texture = texture;
			textureAttribute.unit = unit;
			return;
		}
		attributes.put(key, new TextureAttribute(texture, unit));
	}
}
