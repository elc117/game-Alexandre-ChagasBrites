package com.alegz.mermaid.physics;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class CircleCollider extends Collider
{
	private float radius;
	
	public CircleCollider(float radius, short categoryBits, short maskBits)
	{
		super(categoryBits, maskBits);
		this.radius = radius;
	}
	
	public void setRadius(float radius)
	{
		this.radius = radius;
		if (fixtures != null)
			fixtures[0].getShape().setRadius(radius);
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	@Override
	protected Shape[] getShapes()
	{
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		return new Shape[] {shape};
	}
}
