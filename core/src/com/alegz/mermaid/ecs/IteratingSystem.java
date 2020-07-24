package com.alegz.mermaid.ecs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

public abstract class IteratingSystem extends EntitySystem
{
	private List<Entity> entities;
	
	public IteratingSystem()
	{
		entities = new ArrayList<Entity>();
	}
	
	public final void update(float deltaTime)
	{
		for (Entity entity : entities) 
			processEntity(entity, deltaTime);
	}
	
	protected void processEntity(Entity entity, float deltaTime)
	{
		
	}
	
	public final void entityAdded(Engine engine, Entity entity)
	{
		if (shouldAddEntity(engine, entity))
			entities.add(entity);
	}
	
	protected abstract boolean shouldAddEntity(Engine engine, Entity entity);
}
