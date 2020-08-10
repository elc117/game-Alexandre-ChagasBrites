package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.alegz.mermaid.physics.Collider;
import com.badlogic.gdx.math.Vector2;

public class PlayerComponent implements Component
{
	public Vector2 velocity = new Vector2();
	public float speed = 5.0f;
	public float impulse = 5.0f;
	public float maxSpeed = 15.0f;
	public float accel = 25.0f;
	public float drag = 5.0f;
	public float impulseDrag = 0.25f;
	public float gravity = -15.0f;
	public float oldHeight = 0.0f;
	public float stamina = 1.0f;
	public float staminaRegen = 2.0f;
	
	public Class<? extends Component> getComponentClass()
	{
		return PlayerComponent.class;
	}
}
