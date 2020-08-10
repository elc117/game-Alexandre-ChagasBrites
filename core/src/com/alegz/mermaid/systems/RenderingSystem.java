package com.alegz.mermaid.systems;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.PixelFont;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.RendererComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.UITransformComponent;
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
	private PixelFont font;
	
	private List<Entity> entities;
	private List<Entity> uiEntities;
	
	private ObjectMap<Entity, TransformComponent> transformComponents;
	private ObjectMap<Entity, SpriteRendererComponent> spriteRendererComponents;
	private ObjectMap<Entity, MeshRendererComponent> meshRendererComponents;
	private ObjectMap<Entity, UITransformComponent> uiTransformComponents;
	private ObjectMap<Entity, TilemapRendererComponent> tilemapRendererComponents;
	private ObjectMap<Entity, ImageRendererComponent> imageRendererComponents;
	private ObjectMap<Entity, TextRendererComponent> textRendererComponents;
	
	public RenderingSystem(PixelFont font)
	{
		renderer = new Renderer(spriteCount);
		camera = new PlatformerCamera();
		this.font = font;
		
		entities = new ArrayList<>();
		uiEntities = new ArrayList<>();
	}
	
	public void start(Engine engine) 
	{
		transformComponents = engine.getComponentStorage(TransformComponent.class);
		spriteRendererComponents = engine.getComponentStorage(SpriteRendererComponent.class);
		meshRendererComponents = engine.getComponentStorage(MeshRendererComponent.class);
		tilemapRendererComponents = engine.getComponentStorage(TilemapRendererComponent.class);
		uiTransformComponents = engine.getComponentStorage(UITransformComponent.class);
		imageRendererComponents = engine.getComponentStorage(ImageRendererComponent.class);
		textRendererComponents = engine.getComponentStorage(TextRendererComponent.class);
	}
	
	public void update(float deltaTime)
	{
		camera.setPixelPerfectMatrix(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 12);
		
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
		
		renderer.setProjectionMatrix(camera.getUIMatrix());
		for (Entity entity : uiEntities) 
		{
			UITransformComponent transform = uiTransformComponents.get(entity);
			ImageRendererComponent imageRenderer = imageRendererComponents.get(entity);
			TextRendererComponent textRenderer = textRendererComponents.get(entity);
			
			if (imageRenderer != null)
				renderer.drawImage(camera, transform, imageRenderer);
			if (textRenderer != null)
				renderer.drawText(camera, font, transform, textRenderer);
		}
		
		renderer.end();
	}
	
	public void entityAdded(Engine engine, Entity entity)
	{
		if (engine.hasComponent(entity, TransformComponent.class) &&
			(engine.hasComponent(entity, SpriteRendererComponent.class) ||
			 engine.hasComponent(entity, MeshRendererComponent.class) ||
			 engine.hasComponent(entity, TilemapRendererComponent.class)))
			entities.add(entity);
		else if (engine.hasComponent(entity, UITransformComponent.class) &&
				 (engine.hasComponent(entity, ImageRendererComponent.class) ||
				  engine.hasComponent(entity, TextRendererComponent.class)))
			uiEntities.add(entity);
	}
	
	public void entityRemoved(Engine engine, Entity entity)
	{
		if (entities.contains(entity))
			entities.remove(entity);
		else if (uiEntities.contains(entity))
			uiEntities.remove(entity);
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
