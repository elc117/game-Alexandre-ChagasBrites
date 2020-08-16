package com.alegz.mermaid.systems;

import java.util.ArrayList;

import com.alegz.mermaid.QuadTree;
import com.alegz.mermaid.Rect;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.FishComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class FishSystem extends EntitySystem implements PhysicsSystemListener
{
	private Water water;
	private QuadTree<Entity> entityTree;
	
	private ArrayList<Entity> fishEntities;
	private ArrayList<Entity> collideEntities;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, RigidbodyComponent> rigidbodyComponents;
	private ObjectMap<Entity, FishComponent> fishComponents;
	
	private Engine engine;
	
	public FishSystem(Water water, Tilemap tilemap)
	{
		this.water = water;
		
		Vector2 minPos = tilemap.getWorldPos(0, tilemap.getHeight());
		Vector2 maxPos = tilemap.getWorldPos(tilemap.getWidth(), 0);
		Rect rect = new Rect(minPos.x, minPos.y, maxPos.x - minPos.x, maxPos.y - minPos.y);
		entityTree = new QuadTree<>(rect, 1.0f);
		
		fishEntities = new ArrayList<>();
		collideEntities = new ArrayList<>();
	}
	
	public void start(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		rigidbodyComponents = engine.getComponentStorage(RigidbodyComponent.class);
		fishComponents = engine.getComponentStorage(FishComponent.class);
		
		this.engine = engine;
		for (Entity entity : fishEntities)
		{
			RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
			FishComponent fish = fishComponents.get(entity);
			
			float angle = MathUtils.random(0.0f, 360.0f);
			Vector2 velocity = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
			velocity.scl(fish.type.speed);
			rigidbody.getBody().setLinearVelocity(velocity);
		}
	}
	
	public void update(float deltaTime)
	{
		for (Entity entity : collideEntities) 
		{
			TransformComponent transform = transformComponents.get(entity);
			entityTree.put(entity, transform.position.x, transform.position.y);
		}
		
		Rect rect = new Rect(0, 0, 1, 1);
		ArrayList<Entity> closeEntities = new ArrayList<>();
		for (Entity entity : fishEntities) 
		{
			TransformComponent transform = transformComponents.get(entity);
			RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
			FishComponent fish = fishComponents.get(entity);
			
			Vector2 velocity = rigidbody.getBody().getLinearVelocity();
			
			if (transform.position.y < 0)
			{
				Vector2 center = transform.position.cpy();
				
				int count = 0;
				Vector2 oldVelocity = velocity.cpy();
				//for (Entity otherEntity : collideEntities)
				//ArrayList<Entity> test = entityTree.get(transform.position.x, transform.position.y);
				rect.x = transform.position.x - fish.type.dist;
				rect.y = transform.position.y - fish.type.dist;
				rect.width = rect.height = fish.type.dist * 2.0f;
				
				entityTree.get(rect, closeEntities);
				for (Entity otherEntity : closeEntities)
				{
					if (otherEntity == entity)
						continue;
					
					TransformComponent otherTransform = transformComponents.get(otherEntity);
					Vector2 offset = transform.position.cpy().sub(otherTransform.position);
					if (offset.len2() > fish.type.dist * fish.type.dist)
						continue;
					
					float dist = offset.len2();
					Vector2 separation = offset.scl(1.0f - Math.min(1, dist / (1.0f * 1.0f)));
					velocity.mulAdd(separation, fish.type.separationForce * deltaTime);
					
					FishComponent otherFish = fishComponents.get(otherEntity);
					if (otherFish != null && otherFish.type != fish.type)
						continue;
					
					center.add(otherTransform.position);
					count++;
					
					Vector2 alignment = rigidbodyComponents.get(otherEntity).getBody().getLinearVelocity().cpy().sub(oldVelocity);
					velocity.mulAdd(alignment, GameUtils.damp(fish.type.alignmentDamp, deltaTime));
				}
				closeEntities.clear();
				
				if (count > 0)
				{
					center.scl(1.0f / (1.0f + count));
					Vector2 cohesion = center.sub(transform.position);
					velocity.mulAdd(cohesion, fish.type.cohesionForce * deltaTime);
				}
				
				velocity.mulAdd(oldVelocity.nor(), deltaTime);
				if (velocity.len2() > fish.type.speed * fish.type.speed)
					velocity.setLength(fish.type.speed);
			}
			else
				velocity.add(0, RigidbodyComponent.gravity * deltaTime);
				
			rigidbody.getBody().setLinearVelocity(velocity);
			
			transform.scale.x = velocity.angle(new Vector2(0, 1)) > 0 ? 1.0f : -1.0f;
			float desiredRotation = velocity.angle() - 90.0f;
			transform.rotation = MathUtils.lerpAngleDeg(transform.rotation, desiredRotation, GameUtils.damp(0.01f, deltaTime));
			
			if (transform.position.y * fish.oldHeight < 0)
				water.splash(transform.position.x, velocity.y / 10.0f, false);
			fish.oldHeight = transform.position.y;
		}
		entityTree.clear();
	}
	
	public final void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
		    engine.hasComponent(entity, RigidbodyComponent.class) &&
		    engine.hasComponent(entity, FishComponent.class))
			fishEntities.add(entity);
		
		if (engine.hasComponent(entity, TransformComponent.class) &&
		    engine.hasComponent(entity, RigidbodyComponent.class) &&
		    (engine.hasComponent(entity, PlayerComponent.class) ||
		     engine.hasComponent(entity, FishComponent.class)))
			collideEntities.add(entity);
	}

	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider) 
	{
		
	}

	public void beginContact(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider, Vector2 normal) 
	{
		if (engine.hasComponent(selfEntity, FishComponent.class))
		{
			RigidbodyComponent rigidbody = rigidbodyComponents.get(selfEntity);
			Vector2 velocity = rigidbody.getBody().getLinearVelocity();
			if (Math.abs(normal.y) > 0.707f)
			{
				velocity.x = MathUtils.random(-1.0f, 1.0f);
				velocity.y = MathUtils.random(0, Math.signum(normal.y));
			}
			else
			{
				velocity.x = MathUtils.random(0, Math.signum(normal.x));
				velocity.y = MathUtils.random(-1.0f, 1.0f);
			}
			rigidbody.getBody().setLinearVelocity(velocity);
		}
	}
	
	public void dispose()
	{
		entityTree.clear();
	}
}
