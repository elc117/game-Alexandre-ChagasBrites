package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.TrashComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.utils.GameUtils;
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
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		rigidbodyComponents = engine.getComponentStorage(RigidbodyComponent.class);
		
		engine.getSystem(PhysicsSystem.class).addSystemListener(this);
		this.engine = engine;
		
		trashCount = trashEntities.size();
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

	public void beginContact(Entity entityA, Entity entityB) 
	{
		Entity trash = null;
		if (engine.hasComponent(entityA, TrashComponent.class))
			trash = entityA;
		else if (engine.hasComponent(entityB, TrashComponent.class))
			trash = entityB;
		
		if (trash != null && !pickedEntities.contains(trash))
			pickedEntities.add(trash);
	}
}