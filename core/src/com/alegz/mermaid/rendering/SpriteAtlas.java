package com.alegz.mermaid.rendering;

import com.alegz.mermaid.Rect;
import com.badlogic.gdx.graphics.Texture;

public class SpriteAtlas 
{
	private Texture texture;
	private Rect rect;
	
	public SpriteAtlas(Texture texture, int width, int height)
	{
		this.texture = texture;
		rect = new Rect(0, 0, (float)width / texture.getWidth(), (float)height / texture.getHeight());
	}
	
	public Sprite getSprite(int x, int y)
	{
		return new Sprite(texture, getRect(x, y).cpy());
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public Rect getRect(int x, int y)
	{
		rect.x = x * rect.width;
		rect.y = y * rect.height;
		return rect;
	}
}
