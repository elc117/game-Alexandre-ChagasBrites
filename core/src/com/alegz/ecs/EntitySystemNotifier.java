package com.alegz.ecs;

public interface EntitySystemNotifier<T extends EntitySystemListener>
{
	public void addSystemListener(T listener);
}
