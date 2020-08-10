package com.alegz.mermaid.states;

import com.alegz.mermaid.Assets;
import com.alegz.mermaid.GameProgress;
import com.alegz.mermaid.MermaidGame;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.ButtonComponent;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.systems.ButtonSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MenuState implements GameState
{
	private MermaidGame game;
	private Assets assets;
	
	private Water water;
	private Engine engine;
	
	private Vector2 oldMousePos;
	
	public MenuState(MermaidGame game, Assets assets)
	{
		this.game = game;
		this.assets = assets;
	}
	
	public void create() 
	{
		int waterScale = 128;
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), waterScale);
		
		engine = new Engine();
		engine.addComponentStorage(TransformComponent.class);
		engine.addComponentStorage(SpriteRendererComponent.class);
		engine.addComponentStorage(MeshRendererComponent.class);
		engine.addComponentStorage(TilemapRendererComponent.class);
		engine.addComponentStorage(UITransformComponent.class);
		engine.addComponentStorage(ImageRendererComponent.class);
		engine.addComponentStorage(TextRendererComponent.class);
		
		RenderingSystem renderingSystem = new RenderingSystem(assets.getFont());
		renderingSystem.getCamera().backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		engine.addSystem(renderingSystem);
		engine.addSystem(new ButtonSystem(renderingSystem.getCamera()));
		
		//water
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(waterScale, 32);
			transform.position = new Vector2(0, -transform.scale.y * 0.5f + 0.5f);
			transform.scale.y += 1.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);
			
			Entity entity = new Entity();
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addEntity(entity);
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "SYRENE";
			textRenderer.fontSize = 3;
			textRenderer.offset.x = -58;
			textRenderer.offset.y = 16;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -8;
			
			class ButtonAction implements ButtonComponent.Action
			{
				public void onClick() 
				{
					game.setState(new PlayState(game, assets, assets.getTilemap(Assets.TILEMAP_LEVEL0)));
				}
			}
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
			ButtonComponent button = new ButtonComponent();
			button.defaultSprite = spriteAtlas.findRegion("buttonDefault");
			button.highlightSprite = spriteAtlas.findRegion("buttonHighlight");
			button.pressedSprite = spriteAtlas.findRegion("buttonPressed");
			button.sprite = button.defaultSprite;
			button.material = assets.getMaterial(Assets.MATERIAL_UI);
			button.action = new ButtonAction();
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Nivel 1";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
		}
		
		//ui
		if (GameProgress.level1 > -1)
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -28;
			
			class ButtonAction implements ButtonComponent.Action
			{
				public void onClick() 
				{
					game.setState(new PlayState(game, assets, assets.getTilemap(Assets.TILEMAP_LEVEL1)));
				}
			}
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
			ButtonComponent button = new ButtonComponent();
			button.defaultSprite = spriteAtlas.findRegion("buttonDefault");
			button.highlightSprite = spriteAtlas.findRegion("buttonHighlight");
			button.pressedSprite = spriteAtlas.findRegion("buttonPressed");
			button.sprite = button.defaultSprite;
			button.material = assets.getMaterial(Assets.MATERIAL_UI);
			button.action = new ButtonAction();
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Nivel 2";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
		}
		
		//ui
		if (GameProgress.level2 > -1)
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -48;
			
			class ButtonAction implements ButtonComponent.Action
			{
				public void onClick() 
				{
					game.setState(new PlayState(game, assets, assets.getTilemap(Assets.TILEMAP_LEVEL2)));
				}
			}
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
			ButtonComponent button = new ButtonComponent();
			button.defaultSprite = spriteAtlas.findRegion("buttonDefault");
			button.highlightSprite = spriteAtlas.findRegion("buttonHighlight");
			button.pressedSprite = spriteAtlas.findRegion("buttonPressed");
			button.sprite = button.defaultSprite;
			button.material = assets.getMaterial(Assets.MATERIAL_UI);
			button.action = new ButtonAction();
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Nivel 3";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
		}
		
		engine.start();
		oldMousePos = new Vector2();
	}

	public void update() 
	{
		float pollution = 3;
		if (GameProgress.level0 > 0)
			pollution--;
		if (GameProgress.level1 > 0)
			pollution--;
		if (GameProgress.level2 > 0)
			pollution--;
		pollution /= 3;
		assets.getMaterial(Assets.MATERIAL_WATER).setFloat("u_pollution", pollution);
		
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
		water.update(deltaTime);
		engine.update(deltaTime);
		
		PlatformerCamera camera = engine.getSystem(RenderingSystem.class).getCamera();
		Vector2 mousePos = camera.getScreenToWorldPosition(Gdx.input.getX(), Gdx.input.getY());
		if (mousePos.y * oldMousePos.y < 0)
		{
			Vector2 velocity = camera.getScreenToWorldPosition(Gdx.input.getX(), Gdx.input.getY() + Gdx.input.getDeltaY());
			velocity.sub(oldMousePos);
			water.splash(mousePos.x, velocity.y);
		}
		oldMousePos = mousePos;
	}

	public void resize(int width, int height) 
	{
		
	}
	
	public void pause()
	{
		
	}
	
	public void resume()
	{
		
	}

	public void dispose() 
	{
		engine.dispose();
	}
}
