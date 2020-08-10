package com.alegz.mermaid.ecs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.ObjectMap;

public class Engine 
{
	private ObjectMap<Class<? extends EntitySystem>, EntitySystem> systems;
	private List<EntitySystem> sortedSystems;
	private ObjectMap<Class<? extends Component>, ObjectMap<Entity, Component>> componentsStorage;
	
	private List<Entity> activeEntities;
	private List<Entity> inactiveEntities;
	private List<Entity> entityQueue;
	private boolean updating;
	
	public Engine()
	{
		systems = new ObjectMap<>();
		sortedSystems = new ArrayList<>();
		componentsStorage = new ObjectMap<>();
		
		activeEntities = new ArrayList<>();
		inactiveEntities = new ArrayList<>();
		entityQueue = new ArrayList<>();
		updating = false;
	}
	
	public void start()
	{
		for (EntitySystem system : sortedSystems)
			system.start(this);
	}
	
	public void update(float deltaTime)
	{
		updating = true;
		entityQueue.clear();
		
		for (EntitySystem system : sortedSystems)
			system.update(deltaTime);
		
		for (Entity entity : entityQueue)
			setActiveInternal(entity, inactiveEntities.contains(entity));
		updating = false;
	}
	
	public void addSystem(EntitySystem system)
	{
		systems.put(system.getClass(), system);
		sortedSystems.add(system);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType) 
	{
		return (T) systems.get(systemType);
	}
	
	public void addComponent(Entity entity, Component component)
	{
		addComponentInternal(entity, component, component.getClass());
		if (component.getClass() != component.getComponentClass())
			addComponentInternal(entity, component, component.getComponentClass());
	}
	
	private void addComponentInternal(Entity entity, Component component, Class<? extends Component> componentType)
	{
		ObjectMap<Entity, Component> componentStorage = componentsStorage.get(componentType);
		if (componentStorage == null)
		{
			componentStorage = new ObjectMap<Entity, Component>();
			componentsStorage.put(componentType, componentStorage);
		}
		componentStorage.put(entity, component);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Entity entity, Class<T> componentType)
	{
		ObjectMap<Entity, Component> componentStorage = componentsStorage.get(componentType);
		if (componentStorage == null)
			return null;
		return (T) componentStorage.get(entity);
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
		return (ObjectMap<Entity, T>)componentsStorage.get(componentType);
	}
	
	public void addEntity(Entity entity)
	{
		activeEntities.add(entity);
		for (EntitySystem system : systems.values())
			system.entityAdded(this, entity);
	}
	
	public void setActive(Entity entity, boolean active)
	{
		if (!updating)
		{
			setActiveInternal(entity, active);
			return;
		}
		
		if (entityQueue.contains(entity))
			return;
		
		if (active && inactiveEntities.contains(entity) ||
			!active && activeEntities.contains(entity))
			entityQueue.add(entity);
	}
	
	private void setActiveInternal(Entity entity, boolean active)
	{
		if (active && inactiveEntities.contains(entity))
		{
			activeEntities.add(entity);
			inactiveEntities.remove(entity);
			
			for (EntitySystem system : systems.values())
				system.entityAdded(this, entity);
		}
		else if (!active && activeEntities.contains(entity))
		{
			activeEntities.remove(entity);
			inactiveEntities.add(entity);
			
			for (EntitySystem system : systems.values())
				system.entityRemoved(this, entity);
		}
	}
	
	public boolean isActive(Entity entity)
	{
		return activeEntities.contains(entity);
	}
	
	public void dispose()
	{
		for (EntitySystem system : systems.values())
			system.dispose();
	}
}
