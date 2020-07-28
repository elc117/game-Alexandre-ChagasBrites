package com.alegz.mermaid.physics;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class CircleCollider extends Collider
{
	public float radius;
	
	public CircleCollider(float radius)
	{
		this.radius = radius;
	}
	
	protected Shape getShape()
	{
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		return shape;
	}
}
