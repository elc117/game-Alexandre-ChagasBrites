package com.alegz.mermaid.components;

import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.ecs.Component;
import com.alegz.mermaid.ecs.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class RigidbodyComponent implements Component
{
	private Body body;
	
	public RigidbodyComponent()
	{
		body = null;
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
		collider.attachToBody(body);
	}
	
	public Body getBody()
	{
		return body;
	}
	
	public Class<? extends Component> getComponentClass()
	{
		return RigidbodyComponent.class;
	}
}
