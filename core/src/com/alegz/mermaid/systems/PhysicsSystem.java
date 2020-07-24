package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

public class PhysicsSystem extends EntitySystem
{
	private World world;
	
	private final static float timeStep = 1.0f / 60.0f;
	private float accumulator;
	
	private List<Entity> entities;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, RigidbodyComponent> rigidbodyComponents;
	
	public PhysicsSystem()
	{
		Vector2 gravity = new Vector2(0, 0);
		world = new World(gravity, true);
		accumulator = 0;
		
		entities = new ArrayList<Entity>();
	}
	
	public void addedToEngine(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		rigidbodyComponents = engine.getComponentStorage(RigidbodyComponent.class);
	}
	
	public void update(float deltaTime)
	{
        accumulator += deltaTime;
        if(accumulator >= timeStep) 
        {
            world.step(timeStep, 6, 2);
            accumulator -= timeStep;
        }
        
        for (Entity entity : entities) 
        {
            TransformComponent transform = transformComponents.get(entity);
            RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
            
            Vector2 position = rigidbody.body.getPosition();
            transform.position.x = position.x;
            transform.position.y = position.y;
            if (!rigidbody.body.isFixedRotation())
            	transform.rotation = rigidbody.body.getAngle() * MathUtils.radDeg;
        }
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
			engine.hasComponent(entity, RigidbodyComponent.class))
			entities.add(entity);
	}
	
	public World getWorld()
	{
		return world;
	}
}
