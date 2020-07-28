package com.alegz.mermaid.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public abstract class Collider 
{
	public boolean isSensor = false;
	
	public void attachToBody(Body body, short categoryBits, short maskBits)
	{
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = getShape();
        fixtureDef.density = 1f;
        fixtureDef.isSensor = isSensor;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
	}
	
	protected abstract Shape getShape();
}
