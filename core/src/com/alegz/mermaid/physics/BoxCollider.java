package com.alegz.mermaid.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class BoxCollider extends Collider
{
	public Vector2 size;
	public Vector2 offset;
	
	public BoxCollider(Vector2 size, Vector2 offset)
	{
		this.size = size;
		this.offset = offset;
	}
	
	protected Shape getShape()
	{
		PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x * 0.5f, size.y * 0.5f, offset, 0);
        return shape;
	}
}
