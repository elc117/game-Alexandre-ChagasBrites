package com.alegz.mermaid.systems;

import com.alegz.mermaid.components.RendererComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.rendering.Renderer;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RenderingSystem extends EntitySystem
{
	private Renderer renderer;
	private final static int spriteCount = 1024;
	private PlatformerCamera camera;
	
	private ImmutableArray<Entity> entities;
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<SpriteRendererComponent> srm;
	private ComponentMapper<TilemapRendererComponent> trm;
	
	public RenderingSystem()
	{
		renderer = new Renderer(spriteCount);
		camera = new PlatformerCamera();
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		srm = ComponentMapper.getFor(SpriteRendererComponent.class);
		trm = ComponentMapper.getFor(TilemapRendererComponent.class);
	}
	
	public void addedToEngine(Engine engine) 
	{
		entities = engine.getEntitiesFor(Family.all(TransformComponent.class).one(SpriteRendererComponent.class, TilemapRendererComponent.class).get());
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
			TransformComponent transform = tm.get(entity);
			SpriteRendererComponent spriteRenderer = srm.get(entity);
			TilemapRendererComponent tilemapRenderer = trm.get(entity);
			
			if (spriteRenderer != null)
				renderer.drawSprite(transform, spriteRenderer);
			else if (tilemapRenderer != null)
				renderer.drawTilemap(transform, tilemapRenderer);
		}
		renderer.flush(true);
		Gdx.gl20.glDisable(GL20.GL_BLEND);
		
		renderer.end();
		
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
