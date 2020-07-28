package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.alegz.mermaid.ecs.EntitySystemNotifier;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;

public class PhysicsSystem extends EntitySystem implements EntitySystemNotifier<PhysicsSystemListener>, ContactListener
{
	private World world;
	
	private final static float timeStep = 1.0f / 60.0f;
	private float accumulator;
	
	private List<Entity> entities;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, RigidbodyComponent> rigidbodyComponents;
	
	private List<PhysicsSystemListener> physicsListeners;
	
	public final static short CATEGORY_PLAYER = 1 << 0;
	public final static short CATEGORY_TRASH = 1 << 1;
	public final static short CATEGORY_WORLD = 1 << 2;
	
	public final static short MASK_PLAYER = CATEGORY_TRASH | CATEGORY_WORLD;
	public final static short MASK_TRASH = CATEGORY_PLAYER;
	public final static short MASK_WORLD = CATEGORY_PLAYER | CATEGORY_WORLD;
	
	public PhysicsSystem()
	{
		Vector2 gravity = new Vector2(0, 0);
		world = new World(gravity, true);
		world.setContactListener(this);
		
		accumulator = 0;
		entities = new ArrayList<>();
		physicsListeners = new ArrayList<>();
	}
	
	public void start(Engine engine) 
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
            
            Vector2 position = rigidbody.getBody().getPosition();
            transform.position.x = position.x;
            transform.position.y = position.y;
            if (!rigidbody.getBody().isFixedRotation())
            	transform.rotation = rigidbody.getBody().getAngle() * MathUtils.radDeg;
        }
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
			engine.hasComponent(entity, RigidbodyComponent.class) &&
			engine.getComponent(entity, RigidbodyComponent.class).getBody().getType() != BodyType.StaticBody)
			entities.add(entity);
	}
	
	public void entityRemoved(Engine engine, Entity entity)
	{
		if (entities.contains(entity))
			entities.remove(entity);
			
		if (engine.hasComponent(entity, TransformComponent.class) &&
			engine.hasComponent(entity, RigidbodyComponent.class))
			engine.getComponent(entity, RigidbodyComponent.class).getBody().setActive(false);
	}
	
	public World getWorld()
	{
		return world;
	}

	public void addSystemListener(PhysicsSystemListener listener) 
	{
		physicsListeners.add(listener);
	}

	public void beginContact(Contact contact) 
	{
		Entity entityA = (Entity)contact.getFixtureA().getBody().getUserData();
		Entity entityB = (Entity)contact.getFixtureB().getBody().getUserData();
		for (PhysicsSystemListener listener : physicsListeners)
			listener.beginContact(entityA, entityB);
	}

	public void endContact(Contact contact) 
	{
		
	}

	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		
	}

	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		
	}
}
