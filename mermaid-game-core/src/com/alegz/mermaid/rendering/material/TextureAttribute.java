package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TextureAttribute extends MaterialAttribute
{
	public Texture texture;
	
	public TextureAttribute(String key, Texture texture)
	{
		this.key = key;
		this.texture = texture;
	}
	
	public void set(ShaderProgram shader) 
	{
		shader.setUniformi(key, 0);
		texture.bind(0);
	}
}
