package com.alegz.ecs;

import com.badlogic.gdx.utils.OrderedMap;

public final class ComponentMap<T extends Component>
{
	private OrderedMap<Entity, T> components;
	
	public ComponentMap()
	{
		components = new OrderedMap<>();
	}
	
	public void put(Entity entity, T component)
	{
		components.put(entity, component);
	}
	
	public T get(Entity entity)
	{
		return components.get(entity);
	}
}
