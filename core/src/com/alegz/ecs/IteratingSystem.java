package com.alegz.ecs;


public abstract class IteratingSystem extends EntitySystem
{
	protected EntityList entityList;
	
	public IteratingSystem(Engine engine, EntityList entityList)
	{
		super(engine);
		this.entityList = entityList;
	}
	
	@Override
	public final void update(float deltaTime)
	{
		for (Entity entity : entityList) 
			updateEntity(entity, deltaTime);
	}
	
	protected void updateEntity(Entity entity, float deltaTime)
	{
		
	}
}
