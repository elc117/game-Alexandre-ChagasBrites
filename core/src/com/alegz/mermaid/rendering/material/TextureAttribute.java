package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class TextureAttribute extends MaterialAttribute
{
	public Texture texture;
	public int unit;
	
	public TextureAttribute(Texture texture, int unit)
	{
		this.texture = texture;
		this.unit = unit;
	}
	
	@Override
	public void set(String key, ShaderProgram shader) 
	{
		texture.bind(unit);
		shader.setUniformi(key, unit);
	}
}
