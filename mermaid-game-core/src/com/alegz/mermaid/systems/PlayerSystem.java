package com.alegz.mermaid.systems;

import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PlayerSystem extends IteratingSystem
{
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<RigidbodyComponent> rm;
	private ComponentMapper<PlayerComponent> pm;

	public PlayerSystem() 
	{
		super(Family.all(TransformComponent.class, 
						 RigidbodyComponent.class,
						 PlayerComponent.class).get());
		tm = ComponentMapper.getFor(TransformComponent.class);
		rm = ComponentMapper.getFor(RigidbodyComponent.class);
		pm = ComponentMapper.getFor(PlayerComponent.class);
	}

	public void processEntity(Entity entity, float deltaTime) 
	{
		TransformComponent transform = tm.get(entity);
		RigidbodyComponent rigidbody = rm.get(entity);
		PlayerComponent player = pm.get(entity);
			
		transform.position.x += player.velocity.x * deltaTime;
		transform.position.y += player.velocity.y * deltaTime;
		
		Vector2 input = new Vector2(
			Gdx.input.isKeyPressed(Input.Keys.A) ? -1 : 
				(Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0),
			Gdx.input.isKeyPressed(Input.Keys.S) ? -1 : 
				(Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0));
		if (input.len2() > 1)
			input.setLength(1);
		
		//Vector2 velocity = physics.getVelocity();
		
		if (transform.position.y <= 0)
		{
			//if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
			player.velocity.mulAdd(input, 25 * deltaTime);
			if (player.velocity.len2() > 5 * 5)
				player.velocity.setLength2(5 * 5);
			player.velocity.scl(1 / (1 + 5 * deltaTime * (1 - input.len2())));
		}
		else
			player.velocity.add(0, -15 * deltaTime);
		
		float desiredRotation = -player.velocity.angle(new Vector2(0, 1)) *
			MathUtils.clamp(player.velocity.len2(), 0, 1);
		transform.rotation = MathUtils.lerpAngleDeg(transform.rotation, desiredRotation,  1 - (float)Math.pow(0.1f, deltaTime));
		transform.scale.x = transform.rotation % 360 > 180 ? 1.5f : -1.5f;
		
		//velocity.mulAdd(input, 3 * 5 * deltaTime);
		//if (velocity.len2() > 3 * 3)
		//	velocity.setLength(3);
		//velocity.scl(1 / (1 + 5 * deltaTime * (1 - input.len2())));
		
		rigidbody.body.setLinearVelocity(player.velocity);
	}
}
