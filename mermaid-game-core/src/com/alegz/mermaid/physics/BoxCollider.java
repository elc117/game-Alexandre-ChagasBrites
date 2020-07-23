package com.alegz.mermaid.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class BoxCollider implements Collider
{
	public Vector2 size;
	public Vector2 offset;
	
	public BoxCollider(Vector2 size, Vector2 offset)
	{
		this.size = size;
		this.offset = offset;
	}
	
	public Fixture getFixture(Body body)
	{
		PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x * 0.5f, size.y * 0.5f, offset, 0);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
        
        return fixture;
	}
}
