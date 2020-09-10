package com.alegz.ecs;

public abstract class EntitySystem 
{
	protected Engine engine;
	
	public EntitySystem(Engine engine)
	{
		this.engine = engine;
	}
	
	public void update(float deltaTime)
	{
		
	}
	
	public void resize(int width, int height)
	{
		
	}
	
	public void dispose()
	{
		
	}
}
