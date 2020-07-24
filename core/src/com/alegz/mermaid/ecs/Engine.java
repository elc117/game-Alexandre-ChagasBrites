package com.alegz.mermaid.ecs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.ObjectMap;

public class Engine 
{
	private ObjectMap<Class<?>, EntitySystem> systems;
	private List<EntitySystem> sortedSystems;
	private ObjectMap<Class<?>, ObjectMap<Entity, Component>> componentsStorage;
	
	public Engine()
	{
		systems = new ObjectMap<Class<?>, EntitySystem>();
		sortedSystems = new ArrayList<EntitySystem>();
		componentsStorage = new ObjectMap<Class<?>, ObjectMap<Entity, Component>>();
	}
	
	public void update(float deltaTime)
	{
		for (EntitySystem system : sortedSystems)
			system.update(deltaTime);
	}
	
	public void addSystem(EntitySystem system)
	{
		Class<? extends EntitySystem> systemType = system.getClass();
		systems.put(systemType, system);
		sortedSystems.add(system);
		system.addedToEngine(this);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) 
	{
		return (T) systems.get(systemType);
	}
	
	public void addComponent(Entity entity, Component component)
	{
		Class<? extends Component> componentType = component.getClass();
		ObjectMap<Entity, Component> componentStorage = componentsStorage.get(componentType);
		if (componentStorage == null)
		{
			componentStorage = new ObjectMap<Entity, Component>();
			componentsStorage.put(componentType, componentStorage);
		}
		componentStorage.put(entity, component);
	}
	
	public boolean hasComponent(Entity entity, Class<? extends Component> componentType)
	{
		ObjectMap<Entity, Component> componentStorage = componentsStorage.get(componentType);
		if (componentStorage == null)
			return false;
		return componentStorage.get(entity) != null;
	}
	
	public void addComponentStorage(Class<? extends Component> componentType)
	{
		if (componentsStorage.get(componentType) == null)
			componentsStorage.put(componentType, new ObjectMap<Entity, Component>());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> ObjectMap<Entity, T> getComponentStorage(Class<T> componentType)
	{
		return (ObjectMap<Entity, T>) componentsStorage.get(componentType);
	}
	
	public void addEntity(Entity entity)
	{
		for (EntitySystem system : systems.values())
			system.entityAdded(this, entity);
	}
	
	public void setActive(Entity entity, boolean active)
	{
		
	}
}
