package com.alegz.ecs;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityList implements Iterable<Entity>
{
	protected ArrayList<Entity> entities;
	private ArrayList<Class<? extends Component>> allComponents;
	private ArrayList<Class<? extends Component>> oneComponents;
	private EntityListListener listener;
	
	public EntityList()
	{
		entities = new ArrayList<>();
		allComponents = new ArrayList<>();
		oneComponents = new ArrayList<>();
		listener = null;
	}
	
	@SafeVarargs
	public final EntityList has(Class<? extends Component>... components)
	{
		allComponents.clear();
		for (Class<? extends Component> component : components)
			allComponents.add(component);
		return this;
	}
	
	@SafeVarargs
	public final EntityList one(Class<? extends Component>... components)
	{
		oneComponents.clear();
		for (Class<? extends Component> component : components)
			oneComponents.add(component);
		return this;
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (entities.contains(entity))
			return;
		
		for (Class<? extends Component> component : allComponents)
		{
			if (!engine.hasComponent(entity, component))
				return;
		}
		
		if (oneComponents.size() > 0)
		{
			boolean has = false;
			for (Class<? extends Component> component : oneComponents)
			{
				if (engine.hasComponent(entity, component))
				{
					has = true;
					break;
				}
			}
			if (!has)
				return;
		}
		
		add(entity);
		if (listener != null)
			listener.entityAdded(engine, entity);
	}
	
	protected void add(Entity entity)
	{
		entities.add(entity);
	}
	
	public void entityRemoved(Engine engine, Entity entity)
	{
		if (entities.remove(entity))
		{
			if (listener != null)
				listener.entityRemoved(engine, entity);
		}
	}
	
	public void setListener(EntityListListener listener)
	{
		this.listener = listener;
	}
	
	public int size()
	{
		return entities.size();
	}

	@Override
	public Iterator<Entity> iterator() 
	{
		return entities.iterator();
	}
}
