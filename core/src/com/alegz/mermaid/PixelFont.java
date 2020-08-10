package com.alegz.mermaid;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;

public class PixelFont 
{	
	private HashMap<String, PixelFontGlyph> glyphs;
	private Texture texture;
	
	@SuppressWarnings("unchecked")
	public PixelFont(Texture texture, String path)
	{
		Json json = new Json();
		json.setUsePrototypes(false);
		this.glyphs = json.fromJson(HashMap.class, Gdx.files.internal(path).readString());
		this.texture = texture;
	}
	
	public PixelFontGlyph getGlyph(Character character)
	{
		return glyphs.get(character.toString());
	}
	
	public Texture getTexture()
	{
		return texture;
	}
}
