package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.components.MeshRendererComponent;
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
	private ObjectMap<Entity, MeshRendererComponent> meshRendererComponents;
	private ObjectMap<Entity, TilemapRendererComponent> tilemapRendererComponents;
	
	public RenderingSystem()
	{
		renderer = new Renderer(spriteCount);
		camera = new PlatformerCamera();
		
		entities = new ArrayList<>();
	}
	
	public void start(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		spriteRendererComponents = engine.getComponentStorage(SpriteRendererComponent.class);
		meshRendererComponents = engine.getComponentStorage(MeshRendererComponent.class);
		tilemapRendererComponents = engine.getComponentStorage(TilemapRendererComponent.class);
	}
	
	public void update(float deltaTime)
	{
		camera.setPixelPerfectProjMatrix(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 12);
		
		Gdx.gl20.glClearColor(camera.backgroundColor.r,
							  camera.backgroundColor.g,
							  camera.backgroundColor.b,
							  camera.backgroundColor.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.begin();
		renderer.setProjectionMatrix(camera.getProjMatrix());
		
		for (Entity entity : entities) 
		{
			TransformComponent transform = transformComponents.get(entity);
			SpriteRendererComponent spriteRenderer = spriteRendererComponents.get(entity);
			MeshRendererComponent meshRenderer = meshRendererComponents.get(entity);
			TilemapRendererComponent tilemapRenderer = tilemapRendererComponents.get(entity);
			
			if (spriteRenderer != null)
				renderer.drawSprite(transform, spriteRenderer);
			else if (meshRenderer != null)
				renderer.drawMesh(transform, meshRenderer);
			else if (tilemapRenderer != null)
				renderer.drawTilemap(transform, tilemapRenderer);
		}
		renderer.flush(true);
		
		renderer.end();
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
			(engine.hasComponent(entity, SpriteRendererComponent.class) ||
			 engine.hasComponent(entity, MeshRendererComponent.class) ||
			 engine.hasComponent(entity, TilemapRendererComponent.class)))
			entities.add(entity);
	}
	
	public void entityRemoved(Engine engine, Entity entity)
	{
		if (entities.contains(entity))
			entities.remove(entity);
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
