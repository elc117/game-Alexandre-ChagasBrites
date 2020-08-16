package com.alegz.mermaid.states;

import java.util.ArrayList;
import java.util.List;

import com.alegz.mermaid.Assets;
import com.alegz.mermaid.GameProgress;
import com.alegz.mermaid.MermaidGame;
import com.alegz.mermaid.SoundManager;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.ButtonComponent;
import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.FishComponent;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RendererComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.TrashComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.physics.BoxCollider;
import com.alegz.mermaid.physics.CircleCollider;
import com.alegz.mermaid.physics.TilemapCollider;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.ButtonSystem;
import com.alegz.mermaid.systems.CameraSystem;
import com.alegz.mermaid.systems.FishSystem;
import com.alegz.mermaid.systems.PhysicsDebugSystem;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PlayerSystem;
import com.alegz.mermaid.systems.PollutionSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PlayState implements GameState
{
	private MermaidGame game;
	private Assets assets;
	
	private Tilemap tilemap;
	private Water water;
	
	private Engine engine;
	private float time;
	private boolean paused;
	
	private TransformComponent playerTransform;
	private PlayerComponent player;
	
	private Entity staminaBarEntity;
	private UITransformComponent staminaBarTransform;
	private Entity staminaBorderEntity;
	private UITransformComponent staminaBorderTransform;
	
	private TextRendererComponent trashText;
	
	private ArrayList<Entity> fishEntities;
	private int totalActiveFish;
	
	private Entity nextLevelButton;
	private Entity continueButton;
	private Entity restartButton;
	private Entity quitButton;
	
	private Entity levelCompleteText;
	
	public PlayState(MermaidGame game, Assets assets, Tilemap tilemap)
	{
		this.game = game;
		this.assets = assets;
		this.tilemap = tilemap;
	}
	
	public void create() 
	{
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), tilemap.getWidth());
		
		engine = new Engine();
		engine.addComponentStorage(TransformComponent.class);
		engine.addComponentStorage(RendererComponent.class);
		engine.addComponentStorage(SpriteRendererComponent.class);
		engine.addComponentStorage(MeshRendererComponent.class);
		engine.addComponentStorage(TilemapRendererComponent.class);
		engine.addComponentStorage(UITransformComponent.class);
		engine.addComponentStorage(ImageRendererComponent.class);
		engine.addComponentStorage(TextRendererComponent.class);
		engine.addComponentStorage(RigidbodyComponent.class);
		engine.addComponentStorage(PlayerComponent.class);
		engine.addComponentStorage(FishComponent.class);
		engine.addComponentStorage(TrashComponent.class);
		engine.addComponentStorage(CameraComponent.class);
		
		PhysicsSystem physicsSystem = new PhysicsSystem();
		RenderingSystem renderingSystem = new RenderingSystem(assets.getFont());
		renderingSystem.getCamera().backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		PlayerSystem playerSystem = new PlayerSystem(water);
		physicsSystem.addSystemListener(playerSystem);
		
		FishSystem fishSystem = new FishSystem(water, tilemap);
		physicsSystem.addSystemListener(fishSystem);
		
		PollutionSystem pollutionSystem = new PollutionSystem(assets.getMaterial(Assets.MATERIAL_WATER));
		physicsSystem.addSystemListener(pollutionSystem);
		
		engine.addSystem(playerSystem);
		engine.addSystem(fishSystem);
		engine.addSystem(pollutionSystem);
		engine.addSystem(physicsSystem);
		engine.addSystem(new CameraSystem());
		engine.addSystem(renderingSystem);
		//engine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), renderingSystem.getCamera()));
		engine.addSystem(new ButtonSystem(renderingSystem.getCamera()));
		
		Material backgroundMaterial = assets.getMaterial(Assets.MATERIAL_BACKGROUND);
		backgroundMaterial.setColor("u_topColor", renderingSystem.getCamera().backgroundColor);
		backgroundMaterial.setColor("u_bottomColor", new Color(76 / 255.0f, 106 / 255.0f, 200 / 255.0f, 1));
		backgroundMaterial.setFloat("u_nearPlane", renderingSystem.getCamera().nearPlane);
		backgroundMaterial.setFloat("u_farPlane", renderingSystem.getCamera().farPlane);
		
		//background
		for (int i = 0; i < 4; i++)
		{
			for (int j = -1; j <= 1; j++)
			{
				float depth = 4.0f - i;
				float scale = 32.0f * (1.0f + depth);
				
				TransformComponent transform = new TransformComponent();
				transform.scale.scl(scale);
				transform.position = new Vector2(j * transform.scale.x, -16.0f);
				
				SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
				switch (i)
				{
				case 0:
					spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND0);
					break;
				case 1:
					spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND1);
					break;
				case 2:
					spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND2);
					break;
				case 3:
					spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND3);
					break;
				}
				spriteRenderer.depth = depth;
				spriteRenderer.material = backgroundMaterial;
				
				Entity entity = new Entity();
				engine.addComponent(entity, transform);
				engine.addComponent(entity, spriteRenderer);
				engine.addEntity(entity);
			}
		}
		
		//fish
		fishEntities = new ArrayList<>();
		{
			List<Vector2> positions = new ArrayList<Vector2>();
			for (int y = 0; y < tilemap.getHeight(); y++)
			{
				for (int x = 0; x < tilemap.getWidth(); x++)
				{
					if (tilemap.getTile(x, y) != 0)
						continue;
					
					Vector2 position = tilemap.getWorldPos(x, y);
					position.x += 0.5f;
					position.y -= 0.5f;
					if (position.y > 0)
						continue;
					positions.add(position);
				}
			}
			
			int counter = Math.min(tilemap.getFishCount(), positions.size());
			while (counter > 0)
			{
				Vector2 position = positions.get(MathUtils.random(0, positions.size() - 1));
				positions.remove(position);
				counter--;
				
				Entity entity = new Entity();
				TransformComponent transform = new TransformComponent();
				transform.position = position;
				
				SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
				spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
				spriteRenderer.layer = 1;
				
				RigidbodyComponent rigidbody = new RigidbodyComponent();
				rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.DynamicBody);
				rigidbody.getBody().setFixedRotation(true);
				
				CircleCollider collider = new CircleCollider(0.5f, PhysicsSystem.CATEGORY_FISH, PhysicsSystem.CATEGORY_WORLD);
				rigidbody.addCollider(collider);
				
				FishComponent fish = new FishComponent();
				fish.type = assets.getFishType(Assets.FISH_TYPES[MathUtils.random(0, Assets.FISH_TYPES.length - 1)]);;
				spriteRenderer.sprite = fish.type.sprite;
				
				engine.addComponent(entity, transform);
				engine.addComponent(entity, spriteRenderer);
				engine.addComponent(entity, rigidbody);
				engine.addComponent(entity, fish);
				engine.addEntity(entity);
				
				fishEntities.add(entity);
			}
		}
		
		//trash
		{
			List<Vector2> positions = new ArrayList<Vector2>();
			for (int y = 0; y < tilemap.getHeight(); y++)
			{
				for (int x = 0; x < tilemap.getWidth(); x++)
				{
					if (tilemap.getTile(x, y) != 0)
						continue;
					
					Vector2 position = tilemap.getWorldPos(x, y);
					position.x += 0.5f;
					position.y -= 0.5f;
					if (position.y > 0)
						continue;
					positions.add(position);
				}
			}
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TRASH);
			
			int counter = Math.min(tilemap.getTrashCount(), positions.size());
			while (counter > 0)
			{
				Vector2 position = positions.get(MathUtils.random(0, positions.size() - 1));
				positions.remove(position);
				counter--;
				
				Entity entity = new Entity();
				TransformComponent transform = new TransformComponent();
				transform.position = position;
				transform.position.x += MathUtils.random(-0.25f, 0.25f);
				transform.position.y += MathUtils.random(-0.25f, 0.25f);
				transform.rotation = MathUtils.random(0, 360);
				
				SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
				spriteRenderer.sprite = spriteAtlas.getRegions().get(MathUtils.random(0, spriteAtlas.getRegions().size - 1));
				spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
				spriteRenderer.layer = 2;
				
				RigidbodyComponent rigidbody = new RigidbodyComponent();
				rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.KinematicBody);
				
				CircleCollider collider = new CircleCollider(1.0f, PhysicsSystem.CATEGORY_TRASH, PhysicsSystem.CATEGORY_PLAYER);
				collider.isSensor = true;
				rigidbody.addCollider(collider);
				
				engine.addComponent(entity, transform);
				engine.addComponent(entity, spriteRenderer);
				engine.addComponent(entity, rigidbody);
				engine.addComponent(entity, new TrashComponent());
				engine.addEntity(entity);
			}
		}
		
		//player
		{
			Entity entity = new Entity();
			TransformComponent transform = new TransformComponent();
			transform.scale.x = transform.scale.y = 1.5f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER);
			spriteRenderer.pivot.y = 0.25f;
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			spriteRenderer.layer = 3;
			
			RigidbodyComponent rigidbody = new RigidbodyComponent();
			rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.DynamicBody);
			rigidbody.getBody().setFixedRotation(true);
			
			BoxCollider collider = new BoxCollider(new Vector2(0.5f, 0.5f), new Vector2(0, 0),
					PhysicsSystem.CATEGORY_PLAYER, PhysicsSystem.CATEGORY_WORLD);
			rigidbody.addCollider(collider);
			
			CircleCollider trashSensor = new CircleCollider(1.0f, PhysicsSystem.CATEGORY_PLAYER, PhysicsSystem.CATEGORY_TRASH);
			trashSensor.isSensor = true;
			rigidbody.addCollider(trashSensor);
			
			PlayerComponent player = new PlayerComponent();
			player.trashSensor = trashSensor;
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addComponent(entity, rigidbody);
			engine.addComponent(entity, player);
			engine.addEntity(entity);
			
			playerTransform = transform;
			this.player = player;
		}
		
		//camera
		{
			Entity entity = new Entity();
			TransformComponent transform = new TransformComponent();
			
			CameraComponent camera = new CameraComponent(renderingSystem.getCamera());
			camera.target = playerTransform;
			camera.targetPlayer = player;
			camera.minBounds = tilemap.getWorldPos(0, tilemap.getHeight());
			camera.maxBounds = tilemap.getWorldPos(tilemap.getWidth(), 0);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, camera);
			engine.addEntity(entity);
		}
		
		//plants
		{
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_PLANTS);
			for (int y = 0; y < tilemap.getHeight(); y++)
			{
				for (int x = 0; x < tilemap.getWidth(); x++)
				{
					if (tilemap.getTile(x, y) != 0 ||
						tilemap.getTile(x, y + 1) == 0 ||
						MathUtils.random() > 1.0f / 3.0f)
						continue;
					
					TransformComponent transform = new TransformComponent();
					transform.position = tilemap.getWorldPos(x, y);
					transform.position.x += 0.5f;
					transform.position.y -= 1.0f;
					if (transform.position.y > 0)
						continue;
					transform.scale.x = MathUtils.randomSign();
					transform.scale.y = 2;
					
					SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
					spriteRenderer.sprite = spriteAtlas.getRegions().get(MathUtils.random(0, spriteAtlas.getRegions().size - 1));
					spriteRenderer.pivot.y = 0;
					spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_PLANT);
					spriteRenderer.layer = 4;
					
					Entity entity = new Entity();
					engine.addComponent(entity, transform);
					engine.addComponent(entity, spriteRenderer);
					engine.addEntity(entity);
				}
			}
		}
		
		//tilemap
		{	
			Entity entity = new Entity();
			TransformComponent transform = new TransformComponent();
			
			TilemapRendererComponent tilemapRenderer = new TilemapRendererComponent(tilemap,
				assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TILE));
			tilemapRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			tilemapRenderer.layer = 5;
			
			RigidbodyComponent rigidbody = new RigidbodyComponent();
			rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.StaticBody);
			
			TilemapCollider collider = new TilemapCollider(tilemap, PhysicsSystem.CATEGORY_WORLD, (short)(PhysicsSystem.CATEGORY_PLAYER | PhysicsSystem.CATEGORY_FISH));
			rigidbody.addCollider(collider);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, tilemapRenderer);
			engine.addComponent(entity, rigidbody);
			engine.addEntity(entity);
		}
		
		//water
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), -tilemap.getWorldPos(0, tilemap.getHeight()).y);
			transform.position = new Vector2(0, -transform.scale.y * 0.5f + 0.5f);
			transform.scale.y += 1.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);
			spriteRenderer.layer = 6;
			
			Entity entity = new Entity();
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addEntity(entity);
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = -8;
			transform.position.y = 8;
			transform.anchor.setZero();
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
			ImageRendererComponent imageRenderer = new ImageRendererComponent();
			imageRenderer.sprite = spriteAtlas.findRegion("staminaBar");
			imageRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			imageRenderer.pivot.x = 0;
			
			transform.scale.x = imageRenderer.sprite.getRegionWidth();
			transform.scale.y = imageRenderer.sprite.getRegionHeight();
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, imageRenderer);
			engine.addEntity(entity);
			
			staminaBarEntity = entity;
			staminaBarTransform = transform;
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = -8;
			transform.position.y = 8;
			transform.anchor.setZero();
			
			TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
			ImageRendererComponent imageRenderer = new ImageRendererComponent();
			imageRenderer.sprite = spriteAtlas.findRegion("staminaBorder");
			imageRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			transform.scale.x = imageRenderer.sprite.getRegionWidth();
			transform.scale.y = imageRenderer.sprite.getRegionHeight();
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, imageRenderer);
			engine.addEntity(entity);
			
			staminaBorderEntity = entity;
			staminaBorderTransform = transform;
		}
		
		//ui
		{
			Entity entity = new Entity();
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
			engine.addEntity(entity);
			
			trashText = textRenderer;
		}
		
		//ui
		nextLevelButton = null;
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = 10;
			
			class ButtonAction implements ButtonComponent.Action
			{
				public void onClick() 
				{
					setPaused(false);
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
			textRenderer.text = "Continuar";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
			
			continueButton = entity;
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -10;
			
			class ButtonAction implements ButtonComponent.Action
			{
				public void onClick() 
				{
					game.setState(new PlayState(game, assets, tilemap));
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
			textRenderer.text = "Reiniciar";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
			
			restartButton = entity;
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 0;
			transform.position.y = -30;
			
			class ButtonAction implements ButtonComponent.Action
			{
				public void onClick() 
				{
					game.setState(new MenuState(game, assets));
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
			textRenderer.text = "Sair";
			textRenderer.offset.x = -40;
			textRenderer.offset.y = -4;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, button);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
			
			quitButton = entity;
		}
		
		//ui
		{
			Entity entity = new Entity();
			UITransformComponent transform = new UITransformComponent();
			transform.position.x = 8;
			transform.position.y = 8;
			transform.anchor.setZero();
			
			TextRendererComponent textRenderer = new TextRendererComponent();
			textRenderer.text = "Nivel completo!!!\nPressione ESCAPE para sair";
			textRenderer.offset.y = 11;
			textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, textRenderer);
			engine.addEntity(entity);
			
			levelCompleteText = entity;
		}
		
		engine.start();
		time = 0;
		setPaused(false);
		
		for (Entity entity : fishEntities)
			engine.setActive(entity, false);
		totalActiveFish = 0;
		
		engine.setActive(staminaBarEntity, false);
		engine.setActive(staminaBorderEntity, false);
		
		engine.setActive(levelCompleteText, false);
	}

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
		
		if (engine.isActive(staminaBorderEntity))
		{
			PlatformerCamera camera = engine.getSystem(RenderingSystem.class).getCamera();
			staminaBorderTransform.position = camera.getWorldToUIPosition(playerTransform.position.x, playerTransform.position.y + 1.0f);
			staminaBarTransform.position = staminaBorderTransform.position.cpy();
			staminaBarTransform.position.x -= staminaBorderTransform.scale.x * 0.5f;
			staminaBarTransform.scale.x = player.stamina * 16.0f;
		}
		
		PollutionSystem pollutionSystem = engine.getSystem(PollutionSystem.class);
		trashText.text = pollutionSystem.getCurrentTrashCount() + "";
		
		water.update(deltaTime);
		engine.update(deltaTime);
		
		if (pollutionSystem.getCurrentTrashCount() == 0 && !engine.isActive(levelCompleteText))
		{
			engine.setActive(levelCompleteText, true);
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
			
			if (tilemap != assets.getTilemap(Assets.TILEMAP_LEVEL2))
			{
				Entity entity = new Entity();
				UITransformComponent transform = new UITransformComponent();
				transform.position.x = 0;
				transform.position.y = 30;
				
				class ButtonAction implements ButtonComponent.Action
				{
					public void onClick() 
					{
						Tilemap nextTilemap = assets.getTilemap(Assets.TILEMAP_LEVEL1);
						if (tilemap == nextTilemap)
							nextTilemap = assets.getTilemap(Assets.TILEMAP_LEVEL2);
						game.setState(new PlayState(game, assets, nextTilemap));
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
				textRenderer.text = "Proximo Nivel";
				textRenderer.offset.x = -40;
				textRenderer.offset.y = -4;
				textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
				
				engine.addComponent(entity, transform);
				engine.addComponent(entity, button);
				engine.addComponent(entity, textRenderer);
				engine.addEntity(entity);
				
				nextLevelButton = entity;
				engine.setActive(nextLevelButton, paused);
			}
		}
		
		if (player.stamina < 1.0f && !engine.isActive(staminaBorderEntity))
		{
			engine.setActive(staminaBarEntity, true);
			engine.setActive(staminaBorderEntity, true);
		}
		else if (player.stamina >= 1.0f && engine.isActive(staminaBorderEntity))
		{
			engine.setActive(staminaBarEntity, false);
			engine.setActive(staminaBorderEntity, false);
		}
		
		{
			int activeFish = (int)((float)tilemap.getFishCount() * (1.0f - pollutionSystem.getPollution()));
			if (activeFish > totalActiveFish && fishEntities.size() > 0)
			{
				Entity entity = fishEntities.get(0);
				fishEntities.remove(entity);
				engine.setActive(entity, true);
				totalActiveFish++;
			}
		}
		
		time += deltaTime;
	}
	
	public void setPaused(boolean paused)
	{
		this.paused = paused;
		if (nextLevelButton != null)
			engine.setActive(nextLevelButton, paused);
		engine.setActive(continueButton, paused);
		engine.setActive(restartButton, paused);
		engine.setActive(quitButton, paused);
	}

	public void resize(int width, int height) 
	{
		
	}
	
	public void pause()
	{
		setPaused(true);
	}
	
	public void resume()
	{
		
	}

	public void dispose() 
	{
		engine.dispose();
	}
}
