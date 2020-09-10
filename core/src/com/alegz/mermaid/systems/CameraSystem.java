package com.alegz.mermaid.systems;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.IteratingSystem;
import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraSystem extends IteratingSystem
{
	private ComponentMap<TransformComponent> transformComponents;
	private ComponentMap<CameraComponent> cameraComponents;
	
	public CameraSystem(Engine engine) 
	{
		super(engine, engine.createEntityList().has(TransformComponent.class, CameraComponent.class));
		transformComponents = engine.getComponentMap(TransformComponent.class);
		cameraComponents = engine.getComponentMap(CameraComponent.class);
	}

	@Override
	public void updateEntity(Entity entity, float deltaTime)
	{
		TransformComponent transform = transformComponents.get(entity);
		CameraComponent camera = cameraComponents.get(entity);
		PlatformerCamera platformerCamera = camera.camera;
		
		if (camera.target != null)
		{
			Vector2 newPosition = camera.target.position.cpy();
			float lerp = GameUtils.damp(0.001f, deltaTime);
			
			if (camera.targetPlayer != null)
			{	
				Vector2 offset = camera.targetPlayer.velocity.cpy();
				float t = Math.max(0, offset.len() - camera.targetPlayer.speed);
				t /= (camera.targetPlayer.maxSpeed - camera.targetPlayer.speed);
				t *= t;
				
				offset.nor();
				offset.scl(-t);
				newPosition.add(offset);
				lerp = MathUtils.lerp(lerp, GameUtils.damp(0.01f, deltaTime), t);
			}
			
			transform.position.lerp(newPosition, lerp);
		}
		
		platformerCamera.position.x = MathUtils.clamp(transform.position.x,
				camera.minBounds.x + platformerCamera.getSize().x,
				camera.maxBounds.x - platformerCamera.getSize().x);
		platformerCamera.position.y = MathUtils.clamp(transform.position.y,
				camera.minBounds.y + platformerCamera.getSize().y, 0);
		platformerCamera.setPixelPerfectMatrix(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 12);
	}
	
	@Override
	public void resize(int width, int height)
	{
		for (Entity entity : entityList)
		{
			CameraComponent camera = cameraComponents.get(entity);
			camera.camera.setPixelPerfectMatrix(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 12);
		}
	}
}
