package com.alegz.mermaid.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class Collider 
{
	protected Body body;
	protected Fixture[] fixtures;
	
	public boolean isSensor = false;
	private short categoryBits = -1;
	private short maskBits = -1;
	
	public Collider(short categoryBits, short maskBits)
	{
		body = null;
		fixtures = null;
		
		this.categoryBits = categoryBits;
		this.maskBits = maskBits;
	}
	
	public void attachToBody(Body body)
	{
		this.body = body;
		Shape[] shapes = getShapes();
		fixtures = new Fixture[shapes.length];
		
		for (int i = 0; i < shapes.length; i++)
		{
			Shape shape = shapes[i];
			FixtureDef fixtureDef = new FixtureDef();
	        fixtureDef.shape = shape;
	        fixtureDef.density = 1f;
	        fixtureDef.isSensor = isSensor;
	        fixtureDef.filter.categoryBits = categoryBits;
	        fixtureDef.filter.maskBits = maskBits;

	        Fixture fixture = body.createFixture(fixtureDef);
	        fixture.setUserData(this);
	        
	        fixtures[i] = fixture;
	        fixtureDef.shape.dispose();
		}
	}
	
	protected abstract Shape[] getShapes();
}
