package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.Assets;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.TrashComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class PollutionSystem extends EntitySystem implements PhysicsSystemListener
{
	private Material waterMaterial;
	private int trashCount;
	
	private Entity playerEntity;
	private List<Entity> trashEntities;
	private List<Entity> pickedEntities;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, RigidbodyComponent> rigidbodyComponents;
	
	private Engine engine;
	
	public PollutionSystem(Material waterMaterial)
	{
		this.waterMaterial = waterMaterial;
		playerEntity = null;
		trashEntities = new ArrayList<>();
		pickedEntities = new ArrayList<>();
	}
	
	public void start(Engine engine) 
	{
		trashCount = trashEntities.size();
		
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		rigidbodyComponents = engine.getComponentStorage(RigidbodyComponent.class);
		
		this.engine = engine;
	}
	
	public void update(float deltaTime)
	{
		float pollution = 1.0f - (float)trashEntities.size() / trashCount;
		pollution = 1.0f - pollution * pollution;
		waterMaterial.setFloat("u_pollution", pollution);
		
		TransformComponent player = transformComponents.get(playerEntity);
		for (int i = 0; i < pickedEntities.size(); i++)
		{
			Entity entity = pickedEntities.get(i);
			RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
			
			Vector2 velocity = player.position.cpy();
			velocity.sub(rigidbody.getBody().getPosition());
			
			float distSquared = velocity.len2();
			float speed = 1.0f / distSquared;
			speed *= GameUtils.smoothstep(4.0f, 1.0f, distSquared);
			velocity.setLength(speed);
			
			Vector2 nextPos = rigidbody.getBody().getPosition();
			nextPos.mulAdd(velocity, deltaTime);
			nextPos.sub(player.position);
			
			if (distSquared > 4.0f)
			{
				rigidbody.getBody().setLinearVelocity(Vector2.Zero);
				pickedEntities.remove(i);
				i--;
			}
			else if (nextPos.dot(velocity) >= 0)
			{
				rigidbody.getBody().setLinearVelocity(Vector2.Zero);
				pickedEntities.remove(i);
				i--;
				engine.setActive(entity, false);
				SoundManager.play(Assets.SOUND_TRASH);
			}
			else
				rigidbody.getBody().setLinearVelocity(velocity);
		}
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
			engine.hasComponent(entity, TrashComponent.class))
			trashEntities.add(entity);
		else if (engine.hasComponent(entity, TransformComponent.class) &&
			engine.hasComponent(entity, PlayerComponent.class))
			playerEntity = entity;
	}
	
	public void entityRemoved(Engine engine, Entity entity)
	{
		if (trashEntities.contains(entity))
			trashEntities.remove(entity);
	}
	
	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider)
	{
		if (engine.hasComponent(selfEntity, TrashComponent.class))
		{
			if (!pickedEntities.contains(selfEntity))
				pickedEntities.add(selfEntity);
		}
	}

	public void beginContact(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider, Vector2 normal)
	{
		
	}
	
	public int getCurrentTrashCount()
	{
		return trashEntities.size();
	}
	
	public int getTotalTrashCount()
	{
		return trashCount;
	}
}
