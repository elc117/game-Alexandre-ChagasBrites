package com.alegz.mermaid.ecs;

public interface EntitySystemNotifier<T extends EntitySystemListener>
{
	public void addSystemListener(T listener);
}
