package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component
{
	public Vector2 position = new Vector2(0, 0);
	public float rotation = 0;
	public Vector2 scale = new Vector2(1, 1);
}
