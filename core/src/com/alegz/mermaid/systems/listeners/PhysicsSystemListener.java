package com.alegz.mermaid.systems.listeners;

import com.alegz.ecs.Entity;
import com.alegz.ecs.EntitySystemListener;
import com.alegz.mermaid.physics.Collider;
import com.badlogic.gdx.math.Vector2;

public interface PhysicsSystemListener extends EntitySystemListener 
{
	public void beginSensor(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider);
	public void beginContact(Entity selfEntity, Collider selfCollider, Entity otherEntity, Collider otherCollider, Vector2 normal);
}
