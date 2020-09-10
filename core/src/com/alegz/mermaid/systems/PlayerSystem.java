package com.alegz.mermaid.systems;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.IteratingSystem;
import com.alegz.mermaid.Assets;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PlayerSystem extends IteratingSystem implements PhysicsSystemListener
{	
	private Water water;
	private PlatformerCamera camera;
	
	private ComponentMap<TransformComponent> transformComponents;
	private ComponentMap<RigidbodyComponent> rigidbodyComponents;
	private ComponentMap<PlayerComponent> playerComponents;

	public PlayerSystem(Engine engine, Water water, PlatformerCamera camera) 
	{
		super(engine, engine.createEntityList().has(TransformComponent.class, RigidbodyComponent.class, PlayerComponent.class));
		this.water = water;
		this.camera = camera;
		
		transformComponents = engine.getComponentMap(TransformComponent.class);
		rigidbodyComponents = engine.getComponentMap(RigidbodyComponent.class);
		playerComponents = engine.getComponentMap(PlayerComponent.class);
	}

	@Override
	public void updateEntity(Entity entity, float deltaTime) 
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
				float forcePower = 1.0f - Math.abs(deltaAngle) / 180.0f;
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
			
			player.trashRadius = 1.0f + GameUtils.smoothstep(0, player.impulse, player.velocity.len() - player.speed);
			player.trashSensor.setRadius(player.trashRadius);
		}
		else
			player.velocity.add(0, RigidbodyComponent.gravity * deltaTime);
		
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
			water.splash(transform.position.x, player.velocity.y / (player.speed + player.impulse), true);
		player.oldHeight = transform.position.y;
		
		updateTail(deltaTime, player, transform);
		updateStaminaBar(player, transform);
	}
	
	private void updateTail(float deltaTime, PlayerComponent player, TransformComponent transform)
	{
		if (player.tailEntity != null)
		{
			TransformComponent tailTransform = transformComponents.get(player.tailEntity);
			Vector2 offset = new Vector2(MathUtils.cosDeg(transform.rotation), MathUtils.sinDeg(transform.rotation)).rotate90(1);
			tailTransform.position = transform.position.cpy().sub(offset.scl(4.0f / 24.0f));
			tailTransform.rotation = MathUtils.lerpAngleDeg(tailTransform.rotation, transform.rotation, GameUtils.damp(0.01f, deltaTime));
		}
	}
	
	private void updateStaminaBar(PlayerComponent player, TransformComponent transform)
	{
		if (player.staminaBarEntity == null || player.staminaBarTransform == null ||
			player.staminaBorderEntity == null || player.staminaBorderTransform == null)
			return;
		
		if (engine.isActive(player.staminaBorderEntity))
		{
			player.staminaBarTransform.position = transform.position.cpy().add(0.0f, 1.0f).sub(player.staminaBorderTransform.scale.x * 0.5f, 0);
			player.staminaBarTransform.scale.x = player.stamina;
			player.staminaBorderTransform.position = player.staminaBarTransform.position.cpy();
		}
		
		if (player.stamina < 1.0f && !engine.isActive(player.staminaBorderEntity))
		{
			engine.setActive(player.staminaBarEntity, true);
			engine.setActive(player.staminaBorderEntity, true);
		}
		else if (player.stamina >= 1.0f && engine.isActive(player.staminaBorderEntity))
		{
			engine.setActive(player.staminaBarEntity, false);
			engine.setActive(player.staminaBorderEntity, false);
		}
	}
	
	@Override
	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider)
	{
		
	}

	@Override
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
					final float force = (float)Math.sqrt((17.0f / 16.0f) * -2.0f * RigidbodyComponent.gravity);
					if (-player.velocity.y * 0.5f < force)
						player.velocity.y = force;
					else
						player.velocity.y = -player.velocity.y * 0.5f;
					if (Math.abs(player.velocity.x) < 1.0f)
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
