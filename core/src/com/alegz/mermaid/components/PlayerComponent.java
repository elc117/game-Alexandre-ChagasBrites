package com.alegz.mermaid.components;

import com.alegz.ecs.Entity;
import com.alegz.mermaid.physics.CircleCollider;
import com.badlogic.gdx.math.Vector2;

public class PlayerComponent extends AnimalComponent
{
	public Vector2 velocity = new Vector2();
	
	public float speed = 5.0f;
	public float impulse = 5.0f;
	public float maxSpeed = 15.0f;
	public float accel = 25.0f;
	public float drag = 5.0f;
	public float impulseDrag = 0.25f;
	public float oldHeight = 0.0f;
	
	public float stamina = 1.0f;
	public float staminaRegen = 2.0f;
	
	public CircleCollider trashSensor = null;
	public float trashRadius = 1.0f;
	
	public Entity tailEntity = null;
	public Entity staminaBarEntity = null;
	public TransformComponent staminaBarTransform = null;
	public Entity staminaBorderEntity = null;
	public TransformComponent staminaBorderTransform = null;
}
