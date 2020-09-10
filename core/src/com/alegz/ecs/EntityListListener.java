package com.alegz.ecs;

public interface EntityListListener 
{
	public void entityAdded(Engine engine, Entity entity);
	public void entityRemoved(Engine engine, Entity entity);
}
