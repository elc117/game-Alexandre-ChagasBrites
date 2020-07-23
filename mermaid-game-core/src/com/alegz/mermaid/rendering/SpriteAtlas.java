package com.alegz.mermaid.rendering;

import com.alegz.mermaid.Rect;
import com.badlogic.gdx.graphics.Texture;

public class SpriteAtlas 
{
	public Texture texture;
	
	public SpriteAtlas(Texture texture)
	{
		this.texture = texture;
	}
	
	public Rect getRect(Rect rect)
	{
		rect.x /= (float)texture.getWidth();
		rect.y /= (float)texture.getHeight();
		rect.width /= (float)texture.getWidth();
		rect.height /= (float)texture.getHeight();
		return rect;
	}
}
