package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.rendering.Renderer;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.ecs.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ObjectMap;

public class RenderingSystem extends EntitySystem
{
	private Renderer renderer;
	private final static int spriteCount = 1024;
	private PlatformerCamera camera;
	
	private List<Entity> entities;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, SpriteRendererComponent> spriteRendererComponents;
	private ObjectMap<Entity, TilemapRendererComponent> tilemapRendererComponents;
	
	public RenderingSystem()
	{
		renderer = new Renderer(spriteCount);
		camera = new PlatformerCamera();
		
		entities = new ArrayList<Entity>();
	}
	
	public void addedToEngine(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		spriteRendererComponents = engine.getComponentStorage(SpriteRendererComponent.class);
		tilemapRendererComponents = engine.getComponentStorage(TilemapRendererComponent.class);
	}
	
	public void update(float deltaTime)
	{
		camera.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 
			Gdx.graphics.getHeight() / (16.0f * 8.0f));
		
		Gdx.gl20.glClearColor(camera.backgroundColor.r,
							  camera.backgroundColor.g,
							  camera.backgroundColor.b,
							  camera.backgroundColor.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.begin();
		renderer.setProjectionMatrix(camera.getProjMatrix());
		
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for (Entity entity : entities) 
		{
			TransformComponent transform = transformComponents.get(entity);
			SpriteRendererComponent spriteRenderer = spriteRendererComponents.get(entity);
			TilemapRendererComponent tilemapRenderer = tilemapRendererComponents.get(entity);
			
			if (spriteRenderer != null)
				renderer.drawSprite(transform, spriteRenderer);
			else if (tilemapRenderer != null)
				renderer.drawTilemap(transform, tilemapRenderer);
		}
		renderer.flush(true);
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		
		renderer.end();
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
			(engine.hasComponent(entity, SpriteRendererComponent.class) ||
			 engine.hasComponent(entity, TilemapRendererComponent.class)))
			entities.add(entity);
	}
	
	public PlatformerCamera getCamera()
	{
		return camera;
	}
	
	public void dispose()
	{
		renderer.dispose();
	}
}
