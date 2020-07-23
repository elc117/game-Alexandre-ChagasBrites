package com.alegz.mermaid.systems;

import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsSystem extends EntitySystem
{
	private World world;
	
	private final static float timeStep = 1.0f / 60.0f;
	private float accumulator;
	
	private ImmutableArray<Entity> entities;
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<RigidbodyComponent> rm;
	
	public PhysicsSystem()
	{
		Vector2 gravity = new Vector2(0, 0);
		world = new World(gravity, true);
		accumulator = 0;
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		rm = ComponentMapper.getFor(RigidbodyComponent.class);
	}
	
	public void addedToEngine(Engine engine) 
	{
		entities = engine.getEntitiesFor(Family.all(TransformComponent.class, RigidbodyComponent.class).get());
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
            TransformComponent transform = tm.get(entity);
            RigidbodyComponent rigidbody = rm.get(entity);
            
            Vector2 position = rigidbody.body.getPosition();
            transform.position.x = position.x;
            transform.position.y = position.y;
            if (!rigidbody.body.isFixedRotation())
            	transform.rotation = rigidbody.body.getAngle() * MathUtils.radDeg;
        }
	}
	
	public World getWorld()
	{
		return world;
	}
}
