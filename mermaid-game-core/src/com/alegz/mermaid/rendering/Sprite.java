package com.alegz.mermaid.rendering;

import com.alegz.mermaid.Rect;
import com.badlogic.gdx.graphics.Texture;

public class Sprite 
{
	public Texture texture;
	public Rect rect;
	
	public Sprite(Texture texture, Rect rect)
	{
		this.texture = texture;
		this.rect = rect;
	}
}
