package com.alegz.mermaid.rendering;

import com.alegz.mermaid.Rect;
import com.badlogic.gdx.graphics.Texture;

public class Sprite 
{
	private Texture texture;
	private Rect rect;
	
	public Sprite(Texture texture, Rect rect)
	{
		this.texture = texture;
		this.rect = rect;
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public Rect getRect()
	{
		return rect;
	}
}
