package com.alegz.ecs;

public class OrderedEntityList extends EntityList
{
	private EntityListComparator comparator;
	
	public OrderedEntityList(EntityListComparator comparator)
	{
		super();
		this.comparator = comparator;
	}
	
	@Override
	protected void add(Entity entity)
	{
		int index = 0;
		for (Entity otherEntity : entities)
		{
			if (comparator.compare(entity, otherEntity))
				index++;
		}
		entities.add(index, entity);
	}
}
