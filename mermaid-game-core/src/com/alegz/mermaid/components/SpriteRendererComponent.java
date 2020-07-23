package com.alegz.mermaid.components;

import com.alegz.mermaid.rendering.Sprite;
import com.badlogic.gdx.math.Vector2;

public class SpriteRendererComponent extends RendererComponent
{
	public Sprite sprite = null;
	public Vector2 pivot = new Vector2(0.5f, 0.5f);
	public float depth = 0;
}
