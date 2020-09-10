package com.alegz.ecs;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

public class Engine 
{
	private OrderedMap<Class<? extends EntitySystem>, EntitySystem> systems;
	private OrderedMap<Class<? extends Component>, ComponentMap<? extends Component>> components;
	
	private Array<Entity> entities;
	private ArrayList<EntityList> entitiesList;
	private ArrayList<EntityScript> entityScripts;
	
	private int activeEntities;
	private int createdEntities;
	private final int maxCreatedEntities;
	
	private boolean updating;
	private ArrayList<Entity> queuedEntities;
	private ArrayList<EntityScript> queuedScripts;
	
	public Engine(int entityCount)
	{
		systems = new OrderedMap<>();
		components = new OrderedMap<>();
		
		entities = new Array<>(false, entityCount);
		for (int i = 0; i < entityCount; i++)
			entities.add(new Entity());
		entitiesList = new ArrayList<>();
		entityScripts = new ArrayList<>();
		
		activeEntities = 0;
		createdEntities = 0;
		maxCreatedEntities = entityCount;
		
		updating = false;
		queuedEntities = new ArrayList<>();
		queuedScripts = new ArrayList<>();
	}
	
	public void update(float deltaTime)
	{
		updating = true;
		for (EntitySystem system : systems.values())
			system.update(deltaTime);
		for (EntityScript script : entityScripts)
			script.update(deltaTime);
		updating = false;
		
		for (Entity entity : queuedEntities)
			setActive(entity, !isActive(entity));
		queuedEntities.clear();
		
		for (EntityScript script : queuedScripts)
		{
			script.dispose();
			entityScripts.remove(script);
		}
		queuedScripts.clear();
	}
	
	public void resize(int width, int height)
	{
		for (EntitySystem system : systems.values())
			system.resize(width, height);
	}
	
	public void addSystem(EntitySystem system)
	{
		systems.put(system.getClass(), system);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EntitySystem> T getSystem(Class<T> systemType)
	{
		return (T) systems.get(systemType);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> boolean addComponent(Entity entity, T component)
	{
		Class<? extends Component> componentType = component.getClass();
		/*while (componentType != Component.class)
		{	
			if (hasComponent(entity, componentType))
				return false;
			componentType = (Class<? extends Component>) componentType.getSuperclass();
		}
		
		componentType = component.getClass();*/
		while (componentType != Component.class)
		{
			ComponentMap<T> componentMap = (ComponentMap<T>) getComponentMap(componentType);
			componentMap.put(entity, component);
			componentType = (Class<? extends Component>) componentType.getSuperclass();
		}
		return true;
	}
	
	public <T extends Component> T getComponent(Entity entity, Class<T> componentType)
	{
		ComponentMap<T> componentMap = getComponentMap(componentType);
		return componentMap.get(entity);
	}
	
	public boolean hasComponent(Entity entity, Class<? extends Component> componentType)
	{
		return getComponent(entity, componentType) != null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> ComponentMap<T> getComponentMap(Class<T> componentType)
	{
		ComponentMap<T> componentMap = (ComponentMap<T>) components.get(componentType);
		if (componentMap == null)
		{
			componentMap = new ComponentMap<T>();
			components.put(componentType, componentMap);
		}
		return componentMap;
	}
	
	public Entity createEntity()
	{
		if (createdEntities == maxCreatedEntities)
			return null;
		return entities.get(createdEntities++);
	}
	
	public boolean isActive(Entity entity)
	{
		if (!entities.contains(entity, true))
			return false;
		return entities.indexOf(entity, true) < activeEntities;
	}
	
	public void setActive(Entity entity, boolean active)
	{
		if (!entities.contains(entity, true))
			return;
		
		if (updating && active != isActive(entity) && !queuedEntities.contains(entity))
		{
			queuedEntities.add(entity);
			return;
		}
			
		int index = entities.indexOf(entity, true);
		if (active && index >= activeEntities)
		{
			if (index != activeEntities)
				entities.swap(index, activeEntities);
			activeEntities++;
			
			for (EntityList family : entitiesList)
				family.entityAdded(this, entity);
		}
		else if (!active && index < activeEntities)
		{
			activeEntities--;
			if (index != activeEntities)
				entities.swap(index, activeEntities);
			
			for (EntityList family : entitiesList)
				family.entityRemoved(this, entity);
		}
	}
	
	public EntityList createEntityList()
	{
		EntityList entityList = new EntityList();
		entitiesList.add(entityList);
		return entityList;
	}
	
	public OrderedEntityList createOrderedEntityList(EntityListComparator comparator)
	{
		OrderedEntityList entityList = new OrderedEntityList(comparator);
		entitiesList.add(entityList);
		return entityList;
	}
	
	public void addScript(EntityScript script)
	{
		entityScripts.add(script);
	}
	
	public void removeScript(EntityScript script)
	{
		if (updating && !queuedScripts.contains(script))
		{
			queuedScripts.add(script);
			return;
		}
		
		script.dispose();
		entityScripts.remove(script);
	}
	
	public void dispose()
	{
		for (EntitySystem system : systems.values())
			system.dispose();
		for (EntityScript script : entityScripts)
			script.dispose();
		systems.clear();
		components.clear();
		entities.clear();
		entitiesList.clear();
	}
}
