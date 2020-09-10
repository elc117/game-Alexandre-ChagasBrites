package com.alegz.mermaid.states;

import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.mermaid.Assets;
import com.alegz.mermaid.GameBuilder;
import com.alegz.mermaid.GameProgress;
import com.alegz.mermaid.MermaidGame;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.systems.RenderingSystem;
import com.alegz.mermaid.systems.UISystem;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class MenuState implements GameState
{
	private MermaidGame game;
	private Assets assets;
	
	private PlatformerCamera platformerCamera;
	private Water water;
	private Engine engine;
	
	private Vector2 oldMousePos;
	
	public MenuState(MermaidGame game, Assets assets)
	{
		this.game = game;
		this.assets = assets;
	}
	
	@Override
	public void create() 
	{
		platformerCamera = new PlatformerCamera();
		platformerCamera.setPixelPerfectMatrix(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 12);
		
		int waterScale = 128;
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), waterScale);
		
		engine = new Engine(1024);
		
		UISystem uiSystem = new UISystem(engine, platformerCamera);
		RenderingSystem renderingSystem = new RenderingSystem(engine, platformerCamera, assets.getFont());
		platformerCamera.backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		engine.addSystem(uiSystem);
		engine.addSystem(renderingSystem);
		
		//water
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(waterScale, 32);
			transform.position = new Vector2(0, -transform.scale.y * 0.5f + 0.5f);
			transform.scale.y += 1.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);
			
			Entity entity = engine.createEntity();
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.setActive(entity, true);
		}
		
		//ui
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "SYRENE";
			textRenderer.fontSize = 3;
			textRenderer.offset.x = -58;
			textRenderer.offset.y = 16;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, textRenderer);
			engine.setActive(entity, true);
		}
		
		//ui
		GameBuilder.createButton(engine, assets, GameBuilder.setLevelButton(game, assets, Assets.TILEMAP_LEVEL0), 
				new Vector2(0, -8), "Nivel 1", new Vector2(-40, -4));
		if (GameProgress.level0 > 0)
			createCheckmark(new Vector2(48.0f, -8.0f));
		
		//ui
		if (GameProgress.level1 > -1)
			GameBuilder.createButton(engine, assets, GameBuilder.setLevelButton(game, assets, Assets.TILEMAP_LEVEL1), 
				new Vector2(0, -28), "Nivel 2", new Vector2(-40, -4));
		if (GameProgress.level1 > 0)
			createCheckmark(new Vector2(48.0f, -28.0f));
		
		//ui
		if (GameProgress.level2 > -1)
			GameBuilder.createButton(engine, assets, GameBuilder.setLevelButton(game, assets, Assets.TILEMAP_LEVEL2), 
				new Vector2(0, -48), "Nivel 3", new Vector2(-40, -4));
		if (GameProgress.level2 > 0)
			createCheckmark(new Vector2(48.0f, -48.0f));
		
		oldMousePos = new Vector2();
		
		float pollution = 3;
		if (GameProgress.level0 > 0)
			pollution--;
		if (GameProgress.level1 > 0)
			pollution--;
		if (GameProgress.level2 > 0)
			pollution--;
		pollution /= 3;
		pollution = GameUtils.easeOut(pollution);
		assets.getMaterial(Assets.MATERIAL_WATER).setFloat("u_pollution", pollution);
	}

	private void createCheckmark(Vector2 position)
	{
		Entity entity = engine.createEntity();
		UITransformComponent transform = new UITransformComponent();
		transform.position = position;
		
		TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
		ImageRendererComponent imageRenderer = new ImageRendererComponent();
		imageRenderer.sprite = spriteAtlas.findRegion("checkmark");
		imageRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
		
		transform.scale.x = imageRenderer.sprite.getRegionWidth();
		transform.scale.y = imageRenderer.sprite.getRegionHeight();
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, imageRenderer);
		engine.setActive(entity, true);
	}
	
	@Override
	public void update() 
	{
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
		water.update(deltaTime);
		engine.update(deltaTime);
		
		Vector2 mousePos = platformerCamera.getScreenToWorldPosition(Gdx.input.getX(), Gdx.input.getY());
		if (mousePos.y * oldMousePos.y < 0)
		{
			Vector2 velocity = platformerCamera.getScreenToWorldPosition(Gdx.input.getX(), Gdx.input.getY() + Gdx.input.getDeltaY());
			velocity.sub(oldMousePos);
			water.splash(mousePos.x, velocity.y, true);
		}
		oldMousePos = mousePos;
	}

	@Override
	public void resize(int width, int height) 
	{
		platformerCamera.setPixelPerfectMatrix(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 12);
		engine.resize(width, height);
	}
	
	@Override
	public void pause()
	{
		
	}
	
	@Override
	public void resume()
	{
		
	}

	@Override
	public void dispose() 
	{
		engine.dispose();
	}
}
