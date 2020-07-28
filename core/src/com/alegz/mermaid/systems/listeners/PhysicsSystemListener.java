package com.alegz.mermaid.systems.listeners;

import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystemListener;

public interface PhysicsSystemListener extends EntitySystemListener 
{
	public void beginContact(Entity entityA, Entity entityB);
}
