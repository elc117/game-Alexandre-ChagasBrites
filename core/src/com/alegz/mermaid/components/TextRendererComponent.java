package com.alegz.mermaid.components;

import com.alegz.mermaid.rendering.Renderer;
import com.badlogic.gdx.math.Vector2;

public class TextRendererComponent extends RendererComponent
{
	public String text = "";
	public float fontSize = 1.0f;
	public Vector2 offset = new Vector2(0.0f, 0.0f);
	
	@Override
	public void draw(Renderer renderer, TransformComponent transform) 
	{
		
	}
	
	@Override
	public void draw(Renderer renderer, UITransformComponent transform) 
	{
		
	}
}
