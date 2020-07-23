package com.alegz.mermaid.components;

import com.alegz.mermaid.physics.Collider;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class RigidbodyComponent implements Component
{
	public Body body;
	private Fixture fixture;
	
	public void create(World world, BodyType bodyType)
	{
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(0, 0);
        body = world.createBody(bodyDef);
        fixture = null;
	}
	
	public void setCollider(Collider collider)
	{
		if (fixture != null)
			body.destroyFixture(fixture);
		fixture = collider.getFixture(body);
	}
}
