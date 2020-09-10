package com.alegz.mermaid.systems;

import com.alegz.ecs.ComponentMap;
import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.ecs.EntityList;
import com.alegz.ecs.EntityListComparator;
import com.alegz.ecs.EntitySystem;
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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class RenderingSystem extends EntitySystem implements EntityListComparator
{
	private Renderer renderer;
	private final static int spriteCount = 1024;
	private PlatformerCamera camera;
	private PixelFont font;
	
	private EntityList entities;
	private EntityList uiEntities;
	
	private ComponentMap<TransformComponent> transformComponents;
	private ComponentMap<RendererComponent> rendererComponents;
	private ComponentMap<SpriteRendererComponent> spriteRendererComponents;
	private ComponentMap<MeshRendererComponent> meshRendererComponents;
	private ComponentMap<TilemapRendererComponent> tilemapRendererComponents;
	private ComponentMap<UITransformComponent> uiTransformComponents;
	private ComponentMap<ImageRendererComponent> imageRendererComponents;
	private ComponentMap<TextRendererComponent> textRendererComponents;
	
	public RenderingSystem(Engine engine, PlatformerCamera camera, PixelFont font)
	{
		super(engine);
		renderer = new Renderer(spriteCount);
		this.camera = camera;
		this.font = font;
		
		entities = engine.createOrderedEntityList(this).has(TransformComponent.class).one(SpriteRendererComponent.class, TilemapRendererComponent.class);
		uiEntities = engine.createEntityList().has(UITransformComponent.class).one(ImageRendererComponent.class, TextRendererComponent.class);
		
		transformComponents = engine.getComponentMap(TransformComponent.class);
		rendererComponents = engine.getComponentMap(RendererComponent.class);
		spriteRendererComponents = engine.getComponentMap(SpriteRendererComponent.class);
		meshRendererComponents = engine.getComponentMap(MeshRendererComponent.class);
		tilemapRendererComponents = engine.getComponentMap(TilemapRendererComponent.class);
		uiTransformComponents = engine.getComponentMap(UITransformComponent.class);
		imageRendererComponents = engine.getComponentMap(ImageRendererComponent.class);
		textRendererComponents = engine.getComponentMap(TextRendererComponent.class);
	}
	
	@Override
	public void update(float deltaTime)
	{
		Gdx.gl20.glClearColor(camera.backgroundColor.r,
							  camera.backgroundColor.g,
							  camera.backgroundColor.b,
							  camera.backgroundColor.a);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.begin();
		renderer.setProjectionMatrix(camera.getProjMatrix());
		
		/*for (Entity entity : entities) 
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
		}*/
		for (Entity entity : entities) 
		{
			TransformComponent transform = transformComponents.get(entity);
			RendererComponent rendererComponent = rendererComponents.get(entity);
			rendererComponent.draw(renderer, transform);
		}
		renderer.flush(true);
		
		renderer.setProjectionMatrix(camera.getUIMatrix());
		for (Entity entity : uiEntities) 
		{
			UITransformComponent transform = uiTransformComponents.get(entity);
			ImageRendererComponent imageRenderer = imageRendererComponents.get(entity);
			TextRendererComponent textRenderer = textRendererComponents.get(entity);
			
			if (imageRenderer != null)
				imageRenderer.draw(renderer, transform);
			if (textRenderer != null)
				renderer.drawText(camera, font, transform, textRenderer);
		}
		
		renderer.end();
	}
	
	@Override
	public void dispose()
	{
		renderer.dispose();
	}

	@Override
	public boolean compare(Entity a, Entity b) 
	{
		return rendererComponents.get(a).layer >= rendererComponents.get(b).layer;
	}
}
