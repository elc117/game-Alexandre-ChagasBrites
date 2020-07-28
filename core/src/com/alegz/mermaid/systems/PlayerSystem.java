package com.alegz.mermaid.systems;

import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.IteratingSystem;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerSystem extends IteratingSystem
{	
	private Water water;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, RigidbodyComponent> rigidbodyComponents;
	private ObjectMap<Entity, PlayerComponent> playerComponents;

	public PlayerSystem(Water water) 
	{
		super();
		this.water = water;
	}
	
	public void start(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		rigidbodyComponents = engine.getComponentStorage(RigidbodyComponent.class);
		playerComponents = engine.getComponentStorage(PlayerComponent.class);
	}

	public void processEntity(Entity entity, float deltaTime) 
	{
		TransformComponent transform = transformComponents.get(entity);
		RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
		PlayerComponent player = playerComponents.get(entity);
			
		Vector2 input = new Vector2(
			Gdx.input.isKeyPressed(Keys.A) ? -1 : 
			(Gdx.input.isKeyPressed(Keys.D) ? 1 : 0),
			Gdx.input.isKeyPressed(Keys.S) ? -1 : 
			(Gdx.input.isKeyPressed(Keys.W) ? 1 : 0)
		);
		if (input.len2() > 1.0f)
			input.setLength(1.0f);
		
		Vector2 oldVelocity = player.velocity.cpy();
		if (transform.position.y <= 0)
		{
			if (input.len2() > 0)
			{
				float speed = player.velocity.len();
				
				Vector2 forceA = Vector2.Zero;
				{
					float angle = player.velocity.angle();
					float desiredAngle = input.angle();
					
					float t = GameUtils.damp(0.01f, deltaTime);
					t = MathUtils.lerp(1.0f, t, GameUtils.smoothstep(0, player.speed, speed));
					angle = MathUtils.lerpAngleDeg(angle, desiredAngle, t);
					
					if (speed <= player.speed)
					{
						speed += input.len2() * player.accel * deltaTime;
						speed = Math.min(speed, player.speed);
						
						if (Gdx.input.isKeyJustPressed(Keys.SPACE))
							speed += player.impulse;
					}
					
					forceA = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
					forceA.scl(speed);
					forceA.sub(player.velocity);
				}
				
				Vector2 forceB = Vector2.Zero;
				{
					Vector2 desiredVelocity = input.cpy();
					desiredVelocity.scl(player.speed);
					
					forceB = desiredVelocity.cpy();
					forceB.sub(player.velocity);
					forceB.scl(GameUtils.damp(0.01f, deltaTime));
				}
				
				float deltaAngle = input.angle(player.velocity);
				float forcePower = GameUtils.smoothstep(90, 180, Math.abs(deltaAngle));
				forcePower = 1.0f - Math.abs(deltaAngle) / 180.0f;
				forcePower = 1.0f - forcePower * forcePower;
				forcePower *= GameUtils.smoothstep(0, player.speed, speed);
				forcePower *= GameUtils.smoothstep(player.speed + player.impulse, player.speed, speed);
				Vector2 force = forceA.lerp(forceB, forcePower);
				
				player.velocity.add(force);
			}
			
			float drag = player.drag * (1 - input.len2());
			drag = MathUtils.lerp(drag, player.impulseDrag, 
				GameUtils.smoothstep(0, player.impulse, player.velocity.len() - player.speed));
			player.velocity.scl(1 / (1 + drag * deltaTime));
		}
		else
			player.velocity.add(0, player.gravity * deltaTime);
		
		float deltaAngle = player.velocity.angle(oldVelocity);
		if (Math.abs(deltaAngle) > 0.001f)
			transform.scale.x = Math.signum(deltaAngle) * 1.5f;
		
		float desiredRotation = -player.velocity.angle(new Vector2(0, 1));
		desiredRotation *= MathUtils.clamp(player.velocity.len2(), 0, 1);
		transform.rotation = MathUtils.lerpAngleDeg(transform.rotation, desiredRotation, GameUtils.damp(0.0001f, deltaTime));

		rigidbody.getBody().setLinearVelocity(player.velocity);
		
		float nextPosition = transform.position.y + player.velocity.y * deltaTime;
		if (transform.position.y * nextPosition < 0)
			water.splash(transform.position.x, player.velocity.y / (player.speed + player.impulse));
	}

	protected boolean shouldAddEntity(Engine engine, Entity entity) 
	{
		return engine.hasComponent(entity, TransformComponent.class) &&
			   engine.hasComponent(entity, RigidbodyComponent.class) &&
			   engine.hasComponent(entity, PlayerComponent.class);
	}
}
