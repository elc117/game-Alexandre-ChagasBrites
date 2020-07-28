package com.alegz.mermaid.components;

import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.ecs.Component;
import com.alegz.mermaid.ecs.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class RigidbodyComponent implements Component
{
	private Body body;
	
	private short categoryBits;
	private short maskBits;
	
	public RigidbodyComponent(short categoryBits, short maskBits)
	{
		body = null;
		this.categoryBits = categoryBits;
		this.maskBits = maskBits;
	}
	
	public void create(World world, Entity entity, TransformComponent transform, BodyType bodyType)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(transform.position);
		bodyDef.angle = transform.rotation * MathUtils.degRad;
        bodyDef.type = bodyType;
        body = world.createBody(bodyDef);
        body.setUserData(entity);
	}
	
	public void addCollider(Collider collider)
	{
		collider.attachToBody(body, categoryBits, maskBits);
	}
	
	public Body getBody()
	{
		return body;
	}
}
