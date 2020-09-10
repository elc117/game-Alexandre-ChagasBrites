package com.alegz.mermaid.states;

import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.mermaid.Assets;
import com.alegz.mermaid.GameBuilder;
import com.alegz.mermaid.GameProgress;
import com.alegz.mermaid.MermaidGame;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.ButtonComponent;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.systems.CameraSystem;
import com.alegz.mermaid.systems.FishSystem;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PlayerSystem;
import com.alegz.mermaid.systems.PollutionSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.alegz.mermaid.systems.UISystem;
import com.alegz.mermaid.utils.GameUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

public class PlayState implements GameState
{
	private MermaidGame game;
	private Assets assets;
	
	private PlatformerCamera platformerCamera;
	private Tilemap tilemap;
	private Water water;
	
	private Engine engine;
	private float time;
	private boolean paused;
	
	private TextRendererComponent trashText;
	
	private Entity continueButton;
	private Entity restartButton;
	private Entity quitButton;
	
	private float levelAnimation;
	private Entity levelCompleteText;
	private Entity levelCompleteButton;
	
	public PlayState(MermaidGame game, Assets assets, Tilemap tilemap)
	{
		this.game = game;
		this.assets = assets;
		this.tilemap = tilemap;
	}
	
	@Override
	public void create() 
	{
		platformerCamera = new PlatformerCamera();
		platformerCamera.backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), tilemap.getWidth());
		engine = new Engine(1024);
		
		PhysicsSystem physicsSystem = new PhysicsSystem(engine);
		PlayerSystem playerSystem = new PlayerSystem(engine, water, platformerCamera);
		FishSystem fishSystem = new FishSystem(engine, water, tilemap);
		PollutionSystem pollutionSystem = new PollutionSystem(engine, assets.getMaterial(Assets.MATERIAL_WATER), tilemap);
		CameraSystem cameraSystem = new CameraSystem(engine);
		UISystem uiSystem = new UISystem(engine, platformerCamera);
		RenderingSystem renderingSystem = new RenderingSystem(engine, platformerCamera, assets.getFont());
		
		physicsSystem.addSystemListener(playerSystem);
		physicsSystem.addSystemListener(fishSystem);
		physicsSystem.addSystemListener(pollutionSystem);
		
		engine.addSystem(physicsSystem);
		engine.addSystem(playerSystem);
		engine.addSystem(fishSystem);
		engine.addSystem(pollutionSystem);
		engine.addSystem(cameraSystem);
		engine.addSystem(uiSystem);
		engine.addSystem(renderingSystem);
		//engine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), renderingSystem.getCamera()));
		
		GameBuilder.createBackground(engine, assets, platformerCamera);
		GameBuilder.createFishes(engine, assets, tilemap);
		GameBuilder.createTrash(engine, assets, tilemap);
		
		Entity playerEntity = GameBuilder.createPlayer(engine, assets);
		TransformComponent playerTransform = engine.getComponent(playerEntity, TransformComponent.class);
		PlayerComponent player = engine.getComponent(playerEntity, PlayerComponent.class);
		
		pollutionSystem.playerTransform = playerTransform;
		pollutionSystem.player = player;
		
		GameBuilder.createCamera(engine, assets, tilemap, platformerCamera, playerTransform, player);
		GameBuilder.createPlants(engine, assets, tilemap);
		GameBuilder.createTilemap(engine, assets, tilemap);
		GameBuilder.createWater(engine, assets, tilemap);
		createUI(); 
		
		time = 0;
		setPaused(false);
	}
	
	private void createUI()
	{
		//ui
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = -8;
			transform.position.y = 8;
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TRASH);
			ImageRendererComponent imageRenderer = new ImageRendererComponent();
			imageRenderer.sprite = spriteAtlas.getRegions().get(MathUtils.random(0, spriteAtlas.getRegions().size - 1));
			imageRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			transform.anchor.x = imageRenderer.pivot.x = 1.0f;
			transform.anchor.y = imageRenderer.pivot.y = 0.0f;
			
			transform.scale.x = imageRenderer.sprite.getRegionWidth();
			transform.scale.y = imageRenderer.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "";
			textRenderer.offset.x = -36;
			textRenderer.offset.y = 2;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, imageRenderer);
			engine.addComponent(entity, textRenderer);
			engine.setActive(entity, true);
			
			trashText = textRenderer;
		}
		
		//ui
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = 10;
			
			ButtonComponent button = GameBuilder.createButtonComponent(engine, assets, entity, transform);
			button.action = new ButtonComponent.Action() {
				@Override
				public void onClick() {
					setPaused(false);
				}
			};
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Continuar";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.setActive(entity, true);
			
			continueButton = entity;
		}
		
		//ui
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -10;
			
			ButtonComponent button = GameBuilder.createButtonComponent(engine, assets, entity, transform);
			button.action = GameBuilder.setLevelButton(game, assets, tilemap);
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Reiniciar";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.setActive(entity, true);
			
			restartButton = entity;
		}
		
		//ui
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -30;
			
			ButtonComponent button = GameBuilder.createButtonComponent(engine, assets, entity, transform);
			button.action = GameBuilder.setStateButton(game, new MenuState(game, assets));
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Sair";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.setActive(entity, true);
			
			quitButton = entity;
		}
		
		//ui
		levelAnimation = 0.0f;
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Nivel completo!!!";
			textRenderer.fontSize = 0;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, textRenderer);
			
			levelCompleteText = entity;
		}
		
		//ui
		levelCompleteButton = null;
		{
			Entity entity = engine.createEntity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 8;
			transform.position.y = 8;
			transform.anchor.setZero();
			
			ButtonComponent button = GameBuilder.createButtonComponent(engine, assets, entity, transform);
			button.pivot.setZero();
			
			transform.scale.x = button.sprite.getRegionWidth();
			transform.scale.y = button.sprite.getRegionHeight();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.offset.x = 16;
			textRenderer.offset.y = 4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			if (tilemap != assets.getTilemap(Assets.TILEMAP_LEVEL2))
			{
				Tilemap nextTilemap = assets.getTilemap(Assets.TILEMAP_LEVEL1);
				if (tilemap == nextTilemap)
					nextTilemap = assets.getTilemap(Assets.TILEMAP_LEVEL2);
				button.action = GameBuilder.setLevelButton(game, assets, nextTilemap);
				textRenderer.text = "Proximo Nivel";
				
			}
			else
			{
				button.action = GameBuilder.setStateButton(game, new MenuState(game, assets));
				textRenderer.text = "Voltar ao menu";
			}
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			
			levelCompleteButton = entity;
		}
	}

	@Override
	public void update() 
	{
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
		{
			setPaused(!paused);
			if (!paused)
				SoundManager.play(Assets.SOUND_PAUSE_OUT);
			else
				SoundManager.play(Assets.SOUND_PAUSE_IN);
		}
		
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
		if (paused)
			deltaTime = 0;
		
		assets.getMaterial(Assets.MATERIAL_SPRITE).setFloat("u_time", time);
		assets.getMaterial(Assets.MATERIAL_PLANT).setFloat("u_time", time);
		
		PollutionSystem pollutionSystem = engine.getSystem(PollutionSystem.class);
		trashText.text = pollutionSystem.getCurrentTrashCount() + "";
		
		water.update(deltaTime);
		engine.update(deltaTime);
		
		levelComplete();
		
		time += deltaTime;
	}
	
	private void levelComplete()
	{
		if (engine.isActive(levelCompleteText))
		{
			float t = levelAnimation;
			if (t < 2.0f)
				t = GameUtils.easeOutBounce(t * 0.5f);
			else if (t < 3.0f)
				t = 1.0f;
			else if (t > 3.0f)
				t = MathUtils.lerp(1.0f, 0.0f, GameUtils.easeIn(t - 3.0f));
			
			TextRendererComponent textRenderer = engine.getComponent(levelCompleteText, TextRendererComponent.class);
			textRenderer.fontSize = 2.0f * t;
			textRenderer.offset.x = -80 * t;
			textRenderer.offset.y = -8 * t;
			
			levelAnimation += Gdx.graphics.getDeltaTime();
			if (levelAnimation > 4.0f)
				engine.setActive(levelCompleteText, false);
		}
		
		PollutionSystem pollutionSystem = engine.getSystem(PollutionSystem.class);
		if (pollutionSystem.getCurrentTrashCount() == 0 && !engine.isActive(levelCompleteButton))
		{
			levelAnimation = 0.0f;
			engine.setActive(levelCompleteText, true);
			engine.setActive(levelCompleteButton, true);
			SoundManager.play(Assets.SOUND_LEVEL_COMPLETE);
			
			if (tilemap == assets.getTilemap(Assets.TILEMAP_LEVEL0))
			{
				GameProgress.level0 = 1;
				if (GameProgress.level1 < 0)
					GameProgress.level1 = 0;
			}
			else if (tilemap == assets.getTilemap(Assets.TILEMAP_LEVEL1))
			{
				GameProgress.level1 = 1;
				if (GameProgress.level2 < 0)
					GameProgress.level2 = 0;
			}
			else if (tilemap == assets.getTilemap(Assets.TILEMAP_LEVEL2))
			{
				GameProgress.level2 = 1;
			}
		}
	}
	
	public void setPaused(boolean paused)
	{
		this.paused = paused;
		engine.setActive(continueButton, paused);
		engine.setActive(restartButton, paused);
		engine.setActive(quitButton, paused);
	}

	@Override
	public void resize(int width, int height) 
	{
		engine.resize(width, height);
	}
	
	@Override
	public void pause()
	{
		setPaused(true);
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
