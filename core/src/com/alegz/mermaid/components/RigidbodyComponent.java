package com.alegz.mermaid.components;

import com.alegz.ecs.Component;
import com.alegz.ecs.Entity;
import com.alegz.mermaid.physics.Collider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class RigidbodyComponent extends Component
{
	private Body body = null;
	public Vector2 oldPosition = new Vector2(0.0f, 0.0f);
	
	public BodyType bodyType = BodyType.StaticBody;
	public boolean fixedRotation = true;
	
	public final static float gravity = -15.0f;
	
	public void create(World world, Entity entity, TransformComponent transform)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(transform.position);
		bodyDef.angle = transform.rotation * MathUtils.degRad;
        bodyDef.type = bodyType;
        bodyDef.fixedRotation = fixedRotation;
        body = world.createBody(bodyDef);
        body.setUserData(entity);
        oldPosition = transform.position.cpy();
	}
	
	public void addCollider(Collider collider)
	{
		collider.attachToBody(body);
	}
	
	public Body getBody()
	{
		return body;
	}
}
