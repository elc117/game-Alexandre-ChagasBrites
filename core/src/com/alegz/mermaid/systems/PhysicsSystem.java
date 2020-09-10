package com.alegz.mermaid.systems;

import java.util.ArrayList;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.EntityList;
import com.alegz.ecs.EntityListListener;
import com.alegz.ecs.EntitySystem;
import com.alegz.ecs.EntitySystemNotifier;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsSystem extends EntitySystem implements EntityListListener, EntitySystemNotifier<PhysicsSystemListener>, ContactListener 
{
	private World world;
	
	private final static float timeStep = 1.0f / 30.0f;
	private float accumulator;
	
	private EntityList entities;
	private ArrayList<PhysicsSystemListener> physicsListeners;
	
	private ComponentMap<TransformComponent> transformComponents;
	private ComponentMap<RigidbodyComponent> rigidbodyComponents;
	
	public final static short CATEGORY_PLAYER = 1 << 0;
	public final static short CATEGORY_FISH   = 1 << 1;
	public final static short CATEGORY_TRASH  = 1 << 2;
	public final static short CATEGORY_WORLD  = 1 << 3;
	
	public PhysicsSystem(Engine engine)
	{
		super(engine);
		world = new World(new Vector2(), true);
		world.setContactListener(this);
		
		accumulator = 0;
		entities = engine.createEntityList().has(TransformComponent.class, RigidbodyComponent.class);
		physicsListeners = new ArrayList<>();
		
		entities.setListener(this);
		
		transformComponents = engine.getComponentMap(TransformComponent.class);
		rigidbodyComponents = engine.getComponentMap(RigidbodyComponent.class);
	}
	
	@Override
	public void update(float deltaTime)
	{
        accumulator += deltaTime;
    	while (accumulator >= timeStep)
        {
    		for (Entity entity : entities) 
            {
                RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
                rigidbody.oldPosition.x = rigidbody.getBody().getPosition().x;
                rigidbody.oldPosition.y = rigidbody.getBody().getPosition().y;
            }
    		
            world.step(timeStep, 6, 2);
            accumulator -= timeStep;
        }
        
        float t = accumulator / timeStep;
        for (Entity entity : entities) 
        {
            TransformComponent transform = transformComponents.get(entity);
            RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
            
            transform.position = rigidbody.oldPosition.cpy().lerp(rigidbody.getBody().getPosition(), t);
            if (!rigidbody.getBody().isFixedRotation())
            	transform.rotation = rigidbody.getBody().getAngle() * MathUtils.radDeg;
        }
	}
	
	public World getWorld()
	{
		return world;
	}
	
	@Override
	public void entityAdded(Engine engine, Entity entity) 
	{
		rigidbodyComponents.get(entity).getBody().setActive(true);
	}

	@Override
	public void entityRemoved(Engine engine, Entity entity) 
	{
		rigidbodyComponents.get(entity).getBody().setActive(false);
	}

	@Override
	public void addSystemListener(PhysicsSystemListener listener) 
	{
		physicsListeners.add(listener);
	}

	@Override
	public void beginContact(Contact contact) 
	{
		Entity entityA = (Entity)contact.getFixtureA().getBody().getUserData();
		Entity entityB = (Entity)contact.getFixtureB().getBody().getUserData();
		Collider colliderA = (Collider)contact.getFixtureA().getUserData();
		Collider colliderB = (Collider)contact.getFixtureB().getUserData();
		for (PhysicsSystemListener listener : physicsListeners)
		{
			listener.beginSensor(entityA, colliderA, entityB, colliderB);
			listener.beginSensor(entityB, colliderB, entityA, colliderA);
		}
	}

	@Override
	public void endContact(Contact contact) 
	{
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) 
	{
		Entity entityA = (Entity)contact.getFixtureA().getBody().getUserData();
		Entity entityB = (Entity)contact.getFixtureB().getBody().getUserData();
		Collider colliderA = (Collider)contact.getFixtureA().getUserData();
		Collider colliderB = (Collider)contact.getFixtureB().getUserData();
		Vector2 normal = contact.getWorldManifold().getNormal();
		for (PhysicsSystemListener listener : physicsListeners)
		{
			listener.beginContact(entityA, colliderA, entityB, colliderB, normal);
			listener.beginContact(entityB, colliderB, entityA, colliderA, normal);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) 
	{
		
	}
	
	@Override
	public void dispose()
	{
		world.dispose();
	}
}
