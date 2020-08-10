package com.alegz.mermaid.systems;

import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.utils.GameUtils;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class CameraSystem extends IteratingSystem
{
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, CameraComponent> cameraComponents;
	
	public CameraSystem() 
	{
		super();
	}
	
	public void start(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		cameraComponents = engine.getComponentStorage(CameraComponent.class);
	}

	public void processEntity(Entity entity, float deltaTime)
	{
		TransformComponent transform = transformComponents.get(entity);
		CameraComponent cam = cameraComponents.get(entity);
		PlatformerCamera camera = cam.camera;
		
		if (cam.target != null)
		{
			Vector2 newPosition = cam.target.position.cpy();
			float lerp = GameUtils.damp(0.001f, deltaTime);
			
			if (cam.targetPlayer != null)
			{	
				Vector2 offset = cam.targetPlayer.velocity.cpy();
				float t = Math.max(0, offset.len() - cam.targetPlayer.speed);
				t /= (cam.targetPlayer.maxSpeed - cam.targetPlayer.speed);
				t *= t;
				
				offset.nor();
				offset.scl(-t);
				newPosition.add(offset);
				lerp = MathUtils.lerp(lerp, GameUtils.damp(0.01f, deltaTime), t);
			}
			
			transform.position.lerp(newPosition, lerp);
		}
		
		camera.position.x = MathUtils.clamp(transform.position.x,
											cam.minBounds.x + camera.getSize().x,
											cam.maxBounds.x - camera.getSize().x);
		camera.position.y = MathUtils.clamp(transform.position.y,
											cam.minBounds.y + camera.getSize().y, 
											0);
	}
	
	protected boolean shouldAddEntity(Engine engine, Entity entity)
	{
		return engine.hasComponent(entity, TransformComponent.class) &&
			   engine.hasComponent(entity, CameraComponent.class);
	}
}
