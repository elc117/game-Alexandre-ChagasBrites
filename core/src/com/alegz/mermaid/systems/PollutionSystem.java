package com.alegz.mermaid.systems;

import java.util.ArrayList;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.EntityList;
import com.alegz.ecs.EntityListListener;
import com.alegz.ecs.EntitySystem;
import com.alegz.ecs.EntitySystemNotifier;
import com.alegz.mermaid.Assets;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.TrashComponent;
import com.alegz.mermaid.physics.Collider;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.listeners.PhysicsSystemListener;
import com.alegz.mermaid.systems.listeners.PollutionSystemListener;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PollutionSystem extends EntitySystem implements EntitySystemNotifier<PollutionSystemListener>, PhysicsSystemListener, EntityListListener
{
	private Material waterMaterial;
	private Tilemap tilemap;
	private int trashCount;
	private int maxTrashCount;
	private float pollution;
	
	private EntityList trashEntities;
	private ArrayList<Entity> pickedEntities;
	
	private ArrayList<Entity> fishEntities;
	private int totalActiveFish;
	
	private ArrayList<PollutionSystemListener> pollutionListeners;
	
	public TransformComponent playerTransform;
	public PlayerComponent player;
	
	private ComponentMap<TransformComponent> transformComponents;
	private ComponentMap<RigidbodyComponent> rigidbodyComponents;
	
	public PollutionSystem(Engine engine, Material waterMaterial, Tilemap tilemap)
	{
		super(engine);
		this.waterMaterial = waterMaterial;
		this.tilemap = tilemap;
		
		trashEntities = engine.createEntityList().has(TransformComponent.class, RigidbodyComponent.class, TrashComponent.class);
		pickedEntities = new ArrayList<>();
		
		trashEntities.setListener(this);
		
		fishEntities = new ArrayList<>();
		totalActiveFish = 0;
		
		pollutionListeners = new ArrayList<>();
		
		playerTransform = null;
		player = null;
		
		trashCount = maxTrashCount = 0;
		pollution = 1.0f;
		waterMaterial.setFloat("u_pollution", 1.0f);
		
		transformComponents = engine.getComponentMap(TransformComponent.class);
		rigidbodyComponents = engine.getComponentMap(RigidbodyComponent.class);
	}
	
	@Override
	public void update(float deltaTime)
	{
		float newPollution = GameUtils.easeOut((float)trashCount / maxTrashCount);
		pollution = MathUtils.lerp(pollution, newPollution, GameUtils.damp(0.1f, deltaTime));
		waterMaterial.setFloat("u_pollution", pollution);
		
		for (int i = 0; i < pickedEntities.size(); i++)
		{
			Entity entity = pickedEntities.get(i);
			TransformComponent transform = transformComponents.get(entity);
			RigidbodyComponent rigidbody = rigidbodyComponents.get(entity);
			
			Vector2 velocity = playerTransform.position.cpy();
			velocity.sub(rigidbody.getBody().getPosition());
			
			float distSquared = velocity.len2();
			float maxDistSquared = 1.0f + player.trashRadius;
			maxDistSquared *= maxDistSquared;
			if (maxDistSquared < distSquared)
				maxDistSquared = distSquared;
			float speed = maxDistSquared / distSquared;
			if (transform.position.y < 0.0f)
				speed *= GameUtils.smoothstep(maxDistSquared, 1.0f, distSquared);
			velocity.setLength(speed);
			
			Vector2 nextPos = rigidbody.getBody().getPosition();
			nextPos.mulAdd(velocity, deltaTime);
			nextPos.sub(playerTransform.position);
			
			if (distSquared > maxDistSquared + 1.0f && transform.position.y < 0.0f)
			{
				rigidbody.getBody().setLinearVelocity(Vector2.Zero);
				pickedEntities.remove(i);
				i--;
			}
			else if (nextPos.dot(velocity) >= 0.0f && velocity.len2() > 0.0f)
			{
				rigidbody.getBody().setLinearVelocity(Vector2.Zero);
				pickedEntities.remove(i);
				i--;
				
				engine.setActive(entity, false);
				SoundManager.play(Assets.SOUND_TRASH);
				removeTrash();
			}
			else
				rigidbody.getBody().setLinearVelocity(velocity);
		}
	}
	
	private void removeTrash()
	{
		trashCount--;
		for (PollutionSystemListener listener : pollutionListeners)
			listener.trashCountUpdated(trashCount);
		
		int activeFish = (int)(tilemap.getFishCount() * (1.0f - pollution));
		if (activeFish > totalActiveFish && fishEntities.size() > 0)
		{
			Entity entity = fishEntities.get(0);
			fishEntities.remove(entity);
			engine.setActive(entity, true);
			totalActiveFish++;
		}
	}
	
	public ArrayList<Entity> getFishEntities()
	{
		return fishEntities;
	}
	
	@Override
	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider)
	{
		if (engine.hasComponent(selfEntity, TrashComponent.class))
		{
			if (!pickedEntities.contains(selfEntity))
				pickedEntities.add(selfEntity);
		}
	}

	@Override
	public void beginContact(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider, Vector2 normal)
	{
		
	}
	
	public float getPollution()
	{
		return pollution;
	}
	
	public int getCurrentTrashCount()
	{
		return trashEntities.size();
	}
	
	public int getTotalTrashCount()
	{
		return trashCount;
	}

	@Override
	public void entityAdded(Engine engine, Entity entity) 
	{
		trashCount++;
		maxTrashCount++;
	}

	@Override
	public void entityRemoved(Engine engine, Entity entity) 
	{
		
	}

	@Override
	public void addSystemListener(PollutionSystemListener listener) 
	{
		pollutionListeners.add(listener);
	}
}
