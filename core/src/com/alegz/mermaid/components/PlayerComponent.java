package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.badlogic.gdx.math.Vector2;

public class PlayerComponent implements Component
{
	public Vector2 velocity = new Vector2();
	public float speed = 5.0f;
	public float impulse = 5.0f;
	public float accel = 25.0f;
	public float drag = 5.0f;
	public float impulseDrag = 0.25f;
	public float gravity = -15.0f;
}
