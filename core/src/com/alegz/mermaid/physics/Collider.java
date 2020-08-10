package com.alegz.mermaid.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class Collider 
{
	public boolean isSensor = false;
	private short categoryBits = -1;
	private short maskBits = -1;
	
	public Collider(short categoryBits, short maskBits)
	{
		this.categoryBits = categoryBits;
		this.maskBits = maskBits;
	}
	
	public void attachToBody(Body body)
	{
		Shape[] shapes = getShapes();
		for (Shape shape : shapes)
		{
			FixtureDef fixtureDef = new FixtureDef();
	        fixtureDef.shape = shape;
	        fixtureDef.density = 1f;
	        fixtureDef.isSensor = isSensor;
	        fixtureDef.filter.categoryBits = categoryBits;
	        fixtureDef.filter.maskBits = maskBits;

	        Fixture fixture = body.createFixture(fixtureDef);
	        fixture.setUserData(this);
	        fixtureDef.shape.dispose();
		}
	}
	
	protected abstract Shape[] getShapes();
}
