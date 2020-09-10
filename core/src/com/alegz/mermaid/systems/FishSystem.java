package com.alegz.mermaid.systems;

import java.util.ArrayList;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.EntityList;
import com.alegz.ecs.EntityListListener;
import com.alegz.ecs.EntitySystem;
import com.alegz.mermaid.QuadTree;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.AnimalComponent;
import com.alegz.mermaid.components.FishComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FishSystem extends EntitySystem implements PhysicsSystemListener, EntityListListener
{
	private Water water;
	private QuadTree<Entity> entityTree;
	
	private EntityList fishEntities;
	private EntityList collideEntities;
	
	private ComponentMap<TransformComponent> transformComponents;
	private ComponentMap<RigidbodyComponent> rigidbodyComponents;
	private ComponentMap<FishComponent> fishComponents;
	
	public FishSystem(Engine engine, Water water, Tilemap tilemap)
	{
		super(engine);
		this.water = water;
		
		Vector2 minPos = tilemap.getWorldPos(0, tilemap.getHeight());
		Vector2 maxPos = tilemap.getWorldPos(tilemap.getWidth(), 0);
		Rectangle rect = new Rectangle(minPos.x, minPos.y, maxPos.x - minPos.x, maxPos.y - minPos.y);
		entityTree = new QuadTree<>(rect, 1.0f);
		
		fishEntities = engine.createEntityList().has(TransformComponent.class, RigidbodyComponent.class, FishComponent.class);
		collideEntities = engine.createEntityList().has(TransformComponent.class, RigidbodyComponent.class, AnimalComponent.class);
		
		fishEntities.setListener(this);
		
		transformComponents = engine.getComponentMap(TransformComponent.class);
		rigidbodyComponents = engine.getComponentMap(RigidbodyComponent.class);
		fishComponents = engine.getComponentMap(FishComponent.class);
	}
	
	@Override
	public void update(float deltaTime)
	{
		for (Entity entity : collideEntities) 
		{
			TransformComponent transform = transformComponents.get(entity);
			entityTree.put(entity, transform.position.x, transform.position.y);
		}
		
		Circle circle = new Circle(0.0f, 0.0f, 1.0f);
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
				circle.x = transform.position.x;
				circle.y = transform.position.y;
				circle.radius = fish.type.dist;
				
				entityTree.get(circle, closeEntities);
				for (Entity otherEntity : closeEntities)
				{
					if (otherEntity == entity)
						continue;
					
					TransformComponent otherTransform = transformComponents.get(otherEntity);
					Vector2 offset = transform.position.cpy().sub(otherTransform.position);
					
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

	@Override
	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider) 
	{
		
	}

	@Override
	public void beginContact(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider, Vector2 normal) 
	{
		if (engine.hasComponent(selfEntity, FishComponent.class))
		{
			TransformComponent transform = transformComponents.get(selfEntity);
			RigidbodyComponent rigidbody = rigidbodyComponents.get(selfEntity);
			Vector2 velocity = rigidbody.getBody().getLinearVelocity();
			
			if (transform.position.y > 0)
			{
				if (normal.y > MathUtils.cosDeg(45.0f))
				{
					final float force = (float)Math.sqrt((17.0f / 16.0f) * -2.0f * RigidbodyComponent.gravity);
					if (-velocity.y * 0.5f < force)
						velocity.y = force;
					else
						velocity.y = -velocity.y * 0.5f;
					if (Math.abs(velocity.x) < 1.0f)
						velocity.x = MathUtils.randomSign();
				}
				else
					velocity.x = Math.abs(velocity.x) * Math.signum(normal.x);
				
				rigidbody.getBody().setLinearVelocity(velocity);
				return;
			}
			
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
	
	@Override
	public void dispose()
	{
		entityTree.clear();
	}

	@Override
	public void entityAdded(Engine engine, Entity entity)
	{
		RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
		FishComponent fish = fishComponents.get(entity);
		
		float angle = MathUtils.random(0.0f, 360.0f);
		Vector2 velocity = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
		velocity.scl(fish.type.speed);
		rigidbody.getBody().setLinearVelocity(velocity);
	}

	@Override
	public void entityRemoved(Engine engine, Entity entity) 
	{
		
	}
}
