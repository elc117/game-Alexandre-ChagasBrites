package com.alegz.mermaid.systems.listeners;

import com.alegz.ecs.EntitySystemListener;

public interface PollutionSystemListener extends EntitySystemListener 
{
	public void trashCountUpdated(int trashCount);
}
