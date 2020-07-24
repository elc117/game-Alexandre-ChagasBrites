package com.alegz.mermaid.systems;

import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

public class CameraSystem extends IteratingSystem
{
	private ComponentMapper<CameraComponent> cm;
	
	public CameraSystem() 
	{
		super(Family.all(CameraComponent.class).get());
		cm = ComponentMapper.getFor(CameraComponent.class);
	}

	public void processEntity(Entity entity, float deltaTime)
	{
		CameraComponent cam = cm.get(entity);
		PlatformerCamera camera = cam.camera;
		
		camera.position.x = MathUtils.clamp(cam.target.position.x,
				cam.minBounds.x + camera.getSize().x,
				cam.maxBounds.x - camera.getSize().x);
		camera.position.y = MathUtils.clamp(cam.target.position.y,
				cam.minBounds.y + camera.getSize().y, 0);
	}
}
