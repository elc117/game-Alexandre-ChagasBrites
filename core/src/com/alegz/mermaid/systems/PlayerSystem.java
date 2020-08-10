package com.alegz.mermaid.systems;

import com.alegz.mermaid.Assets;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.IteratingSystem;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class PlayerSystem extends IteratingSystem implements PhysicsSystemListener
{	
	private Water water;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, RigidbodyComponent> rigidbodyComponents;
	private ObjectMap<Entity, PlayerComponent> playerComponents;
	
	private Engine engine;

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
		this.engine = engine;
	}

	public void processEntity(Entity entity, float deltaTime) 
	{
		TransformComponent transform = transformComponents.get(entity);
		RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
		PlayerComponent player = playerComponents.get(entity);
			
		Vector2 input = new Vector2();
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.DPAD_LEFT))
			input.x--;
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.DPAD_RIGHT))
			input.x++;
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DPAD_DOWN))
			input.y--;
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.DPAD_UP))
			input.y++;
		
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
					}
					
					if (Gdx.input.isKeyJustPressed(Keys.SPACE) && player.stamina >= 1.0f)
					{
						speed = Math.min(speed + player.impulse, player.maxSpeed);
						player.stamina = 0.0f;
						SoundManager.play(Assets.SOUND_DASH);
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
		
		rigidbody.getBody().setLinearVelocity(player.velocity);
		
		player.stamina += player.staminaRegen * deltaTime * GameUtils.smoothstep(player.impulse, 0, player.velocity.len() - player.speed);
		player.stamina = Math.min(player.stamina, 1.0f);
		
		float deltaAngle = player.velocity.angle(oldVelocity);
		if (Math.abs(deltaAngle) > 0.001f)
			transform.scale.x = Math.signum(deltaAngle) * 1.5f;
		
		float desiredRotation = -player.velocity.angle(new Vector2(0, 1));
		desiredRotation *= MathUtils.clamp(player.velocity.len2(), 0, 1);
		transform.rotation = MathUtils.lerpAngleDeg(transform.rotation, desiredRotation, GameUtils.damp(0.0001f, deltaTime));

		if (transform.position.y * player.oldHeight < 0)
			water.splash(transform.position.x, player.velocity.y / (player.speed + player.impulse));
		player.oldHeight = transform.position.y;
	}

	protected boolean shouldAddEntity(Engine engine, Entity entity) 
	{
		return engine.hasComponent(entity, TransformComponent.class) &&
			   engine.hasComponent(entity, RigidbodyComponent.class) &&
			   engine.hasComponent(entity, PlayerComponent.class);
	}
	
	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider)
	{
		
	}

	public void beginContact(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider, Vector2 normal) 
	{
		if (engine.hasComponent(selfEntity, PlayerComponent.class))
		{
			TransformComponent transform = transformComponents.get(selfEntity);
			RigidbodyComponent rigidbody = rigidbodyComponents.get(selfEntity);
			PlayerComponent player = playerComponents.get(selfEntity);
			if (transform.position.y > 0)
			{
				if (normal.y > MathUtils.cosDeg(45.0f))
				{
					player.velocity.y = (float)Math.sqrt((17.0f / 16.0f) * -2.0f * player.gravity);
					if (Math.abs(player.velocity.x) < 0.1f)
						player.velocity.x = MathUtils.randomSign();
				}
				else
					player.velocity.x = Math.abs(player.velocity.x) * Math.signum(normal.x);
				
				rigidbody.getBody().setLinearVelocity(player.velocity);
				SoundManager.play(Assets.SOUND_IMPACT);
			}
		}
	}
}
