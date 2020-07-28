package com.alegz.mermaid.systems;

import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;

public class CameraSystem extends IteratingSystem
{
	private ObjectMap<Entity, CameraComponent> cameraComponents;
	
	public CameraSystem() 
	{
		super();
	}
	
	public void start(Engine engine) 
	{
		cameraComponents = engine.getComponentStorage(CameraComponent.class);
	}

	public void processEntity(Entity entity, float deltaTime)
	{
		CameraComponent cam = cameraComponents.get(entity);
		PlatformerCamera camera = cam.camera;
		
		camera.position.x = MathUtils.clamp(cam.target.position.x,
				cam.minBounds.x + camera.getSize().x,
				cam.maxBounds.x - camera.getSize().x);
		camera.position.y = MathUtils.clamp(cam.target.position.y,
				cam.minBounds.y + camera.getSize().y, 0);
	}
	
	protected boolean shouldAddEntity(Engine engine, Entity entity)
	{
		return engine.hasComponent(entity, CameraComponent.class);
	}
}
