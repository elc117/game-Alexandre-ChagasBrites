package com.alegz.mermaid;

import java.util.ArrayList;
import java.util.List;

import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.mermaid.components.ButtonComponent;
import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.FishComponent;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.TrashComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.physics.BoxCollider;
import com.alegz.mermaid.physics.CircleCollider;
import com.alegz.mermaid.physics.TilemapCollider;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.states.GameState;
import com.alegz.mermaid.states.PlayState;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PollutionSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameBuilder 
{
	public static void createBackground(Engine engine, Assets assets, PlatformerCamera platformerCamera)
	{
		Material backgroundMaterial = assets.getMaterial(Assets.MATERIAL_BACKGROUND);
		backgroundMaterial.setColor("u_topColor", platformerCamera.backgroundColor);
		backgroundMaterial.setColor("u_bottomColor", new Color(76 / 255.0f, 106 / 255.0f, 200 / 255.0f, 1));
		backgroundMaterial.setFloat("u_nearPlane", platformerCamera.nearPlane);
		backgroundMaterial.setFloat("u_farPlane", platformerCamera.farPlane);
		
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
				spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_BACKGROUND);
				
				Entity entity = engine.createEntity();
				engine.addComponent(entity, transform);
				engine.addComponent(entity, spriteRenderer);
				engine.setActive(entity, true);
			}
		}
	}
	
	public static void createFish(Engine engine, Assets assets, Vector2 position)
	{
		Entity entity = engine.createEntity();
		TransformComponent transform = new TransformComponent();
		transform.position = position;
		
		SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
		spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
		spriteRenderer.layer = 1;
		
		RigidbodyComponent rigidbody = new RigidbodyComponent();
		rigidbody.bodyType = BodyType.DynamicBody;
		rigidbody.create(engine.getSystem(PhysicsSystem.class).getWorld(), entity, transform);
		
		CircleCollider collider = new CircleCollider(0.5f, PhysicsSystem.CATEGORY_FISH, PhysicsSystem.CATEGORY_WORLD);
		rigidbody.addCollider(collider);
		
		FishComponent fish = new FishComponent();
		fish.type = assets.getFishType(Assets.FISH_TYPES[MathUtils.random(0, Assets.FISH_TYPES.length - 1)]);;
		spriteRenderer.sprite = fish.type.sprite;
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, spriteRenderer);
		engine.addComponent(entity, rigidbody);
		engine.addComponent(entity, fish);
		
		PollutionSystem pollutionSystem = engine.getSystem(PollutionSystem.class);
		if (pollutionSystem != null)
			pollutionSystem.getFishEntities().add(entity);
	}
	
	public static void createFishes(Engine engine, Assets assets, Tilemap tilemap)
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
			createFish(engine, assets, position);
		}
	}
	
	public static void createTrash(Engine engine, Assets assets, Tilemap tilemap)
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
			
			Entity entity = engine.createEntity();
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
			rigidbody.bodyType = BodyType.KinematicBody;
			rigidbody.create(engine.getSystem(PhysicsSystem.class).getWorld(), entity, transform);
			
			CircleCollider collider = new CircleCollider(1.0f, PhysicsSystem.CATEGORY_TRASH, PhysicsSystem.CATEGORY_PLAYER);
			collider.isSensor = true;
			rigidbody.addCollider(collider);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addComponent(entity, rigidbody);
			engine.addComponent(entity, new TrashComponent());
			engine.setActive(entity, true);
		}
	}
	
	public static Entity createPlayer(Engine engine, Assets assets)
	{
		Entity playerEntity;
		Entity tailEntity;
		PlayerComponent player;
		//player
		{
			playerEntity = engine.createEntity();
			TransformComponent transform = new TransformComponent();
			transform.scale.x = transform.scale.y = 24.0f / 16.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER);
			spriteRenderer.pivot.y = 0.25f;
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			spriteRenderer.layer = 3;
			
			RigidbodyComponent rigidbody = new RigidbodyComponent();
			rigidbody.bodyType = BodyType.DynamicBody;
			rigidbody.create(engine.getSystem(PhysicsSystem.class).getWorld(), playerEntity, transform);
			
			BoxCollider collider = new BoxCollider(new Vector2(0.5f, 0.5f), new Vector2(0, 0),
					PhysicsSystem.CATEGORY_PLAYER, PhysicsSystem.CATEGORY_WORLD);
			rigidbody.addCollider(collider);
			
			CircleCollider trashSensor = new CircleCollider(1.0f, PhysicsSystem.CATEGORY_PLAYER, PhysicsSystem.CATEGORY_TRASH);
			trashSensor.isSensor = true;
			rigidbody.addCollider(trashSensor);
			
			player = new PlayerComponent();
			player.trashSensor = trashSensor;
			
			engine.addComponent(playerEntity, transform);
			engine.addComponent(playerEntity, spriteRenderer);
			engine.addComponent(playerEntity, rigidbody);
			engine.addComponent(playerEntity, player);
		}
		//tail
		{
			tailEntity = engine.createEntity();
			TransformComponent transform = new TransformComponent();
			transform.scale.x = transform.scale.y = 12.0f / 16.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER_TAIL);
			spriteRenderer.pivot.y = 9.5f / 12.0f;
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			spriteRenderer.layer = 3;
			
			engine.addComponent(tailEntity, transform);
			engine.addComponent(tailEntity, spriteRenderer);
			
			player.tailEntity = tailEntity;
		}
		//ui
		{
			Entity entity = engine.createEntity();
			TransformComponent transform = new TransformComponent();
			transform.position.x += 0.5f;
			transform.position.y -= 1.0f;
			transform.scale.y = 4.0f / 16.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER_STAMINA_BAR);
			spriteRenderer.pivot.x = 0;
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			spriteRenderer.layer = 7;
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			
			player.staminaBarEntity = entity;
			player.staminaBarTransform = transform;
		}
		//ui
		{
			Entity entity = engine.createEntity();
			TransformComponent transform = new TransformComponent();
			transform.position.x += 0.5f;
			transform.position.y -= 1.0f;
			transform.scale.y = 4.0f / 16.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER_STAMINA_BORDER);
			spriteRenderer.pivot.x = 0;
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			spriteRenderer.layer = 7;
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			
			player.staminaBorderEntity = entity;
			player.staminaBorderTransform = transform;
		}
		engine.setActive(tailEntity, true);
		engine.setActive(playerEntity, true);
		return playerEntity;
	}
	
	public static void createCamera(Engine engine, Assets assets, Tilemap tilemap, PlatformerCamera platformerCamera,
			TransformComponent playerTransform, PlayerComponent player)
	{
		Entity entity = engine.createEntity();
		TransformComponent transform = new TransformComponent();
		
		CameraComponent camera = new CameraComponent(platformerCamera);
		camera.target = playerTransform;
		camera.targetPlayer = player;
		camera.minBounds = tilemap.getWorldPos(0, tilemap.getHeight());
		camera.maxBounds = tilemap.getWorldPos(tilemap.getWidth(), 0);
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, camera);
		engine.setActive(entity, true);
	}
	
	public static void createPlants(Engine engine, Assets assets, Tilemap tilemap)
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
				
				Entity entity = engine.createEntity();
				engine.addComponent(entity, transform);
				engine.addComponent(entity, spriteRenderer);
				engine.setActive(entity, true);
			}
		}
	}
	
	public static void createTilemap(Engine engine, Assets assets, Tilemap tilemap)
	{	
		Entity entity = engine.createEntity();
		TransformComponent transform = new TransformComponent();
		
		TilemapRendererComponent tilemapRenderer = new TilemapRendererComponent(tilemap,
			assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TILE));
		tilemapRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
		tilemapRenderer.layer = 5;
		
		RigidbodyComponent rigidbody = new RigidbodyComponent();
		rigidbody.create(engine.getSystem(PhysicsSystem.class).getWorld(), entity, transform);
		
		TilemapCollider collider = new TilemapCollider(tilemap, PhysicsSystem.CATEGORY_WORLD, (short)(PhysicsSystem.CATEGORY_PLAYER | PhysicsSystem.CATEGORY_FISH));
		rigidbody.addCollider(collider);
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, tilemapRenderer);
		engine.addComponent(entity, rigidbody);
		engine.setActive(entity, true);
	}
	
	public static void createWater(Engine engine, Assets assets, Tilemap tilemap)
	{
		TransformComponent transform = new TransformComponent();
		transform.scale = new Vector2(tilemap.getWidth(), -tilemap.getWorldPos(0, tilemap.getHeight()).y);
		transform.position = new Vector2(0, -transform.scale.y * 0.5f + 0.5f);
		transform.scale.y += 1.0f;
		
		SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
		spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);
		spriteRenderer.layer = 6;
		
		Entity entity = engine.createEntity();
		engine.addComponent(entity, transform);
		engine.addComponent(entity, spriteRenderer);
		engine.setActive(entity, true);
	}
	
	public static Entity createImage(Engine engine, Assets assets, TextureRegion sprite, Vector2 position, Vector2 anchor)
	{
		Entity entity = engine.createEntity();
		UITransformComponent transform = new UITransformComponent();
		transform.position = position;
		transform.anchor = anchor;
		
		ImageRendererComponent imageRenderer = new ImageRendererComponent();
		imageRenderer.sprite = sprite;
		imageRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
		imageRenderer.pivot.x = 0;
		
		transform.scale.x = imageRenderer.sprite.getRegionWidth();
		transform.scale.y = imageRenderer.sprite.getRegionHeight();
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, imageRenderer);
		return entity;
	}
	
	public static Entity createImageWithText(Engine engine, Assets assets, TextureRegion sprite, Vector2 position, Vector2 anchor, String text, Vector2 offset)
	{
		Entity entity = createImage(engine, assets, sprite, position, anchor);
		
		TextRendererComponent textRenderer = new TextRendererComponent();
		textRenderer.text = text;
		textRenderer.offset = offset;
		textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
		
		engine.addComponent(entity, textRenderer);
		return entity;
	}
	
	public static Entity createButton(Engine engine, Assets assets, ButtonComponent.Action action, Vector2 position, String text, Vector2 offset)
	{
		Entity entity = engine.createEntity();
		UITransformComponent transform = new UITransformComponent();
		transform.position = position;
		
		TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
		ButtonComponent button = new ButtonComponent();
		button.defaultSprite = spriteAtlas.findRegion("buttonDefault");
		button.highlightSprite = spriteAtlas.findRegion("buttonHighlight");
		button.pressedSprite = spriteAtlas.findRegion("buttonPressed");
		button.sprite = button.defaultSprite;
		button.material = assets.getMaterial(Assets.MATERIAL_UI);
		button.action = action;
		
		transform.scale.x = button.sprite.getRegionWidth();
		transform.scale.y = button.sprite.getRegionHeight();
		
		TextRendererComponent textRenderer = new TextRendererComponent();
		textRenderer.text = text;
		textRenderer.offset = offset;
		textRenderer.material = assets.getMaterial(Assets.MATERIAL_UI);
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, button);
		engine.addComponent(entity, textRenderer);
		engine.setActive(entity, true);
		return entity;
	}
	
	public static ButtonComponent createButtonComponent(Engine engine, Assets assets, Entity entity, UITransformComponent transform)
	{
		TextureAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_UI);
		ButtonComponent button = new ButtonComponent();
		button.defaultSprite = spriteAtlas.findRegion("buttonDefault");
		button.highlightSprite = spriteAtlas.findRegion("buttonHighlight");
		button.pressedSprite = spriteAtlas.findRegion("buttonPressed");
		button.sprite = button.defaultSprite;
		button.material = assets.getMaterial(Assets.MATERIAL_UI);
		
		transform.scale.x = button.sprite.getRegionWidth();
		transform.scale.y = button.sprite.getRegionHeight();
		
		engine.addComponent(entity, transform);
		engine.addComponent(entity, button);
		return button;
	}
	
	public static SetStateButton setStateButton(MermaidGame game, GameState state)
	{
		return new SetStateButton(game, state);
	}
	
	public static SetStateButton setLevelButton(MermaidGame game, Assets assets, Tilemap tilemap)
	{
		return new SetStateButton(game, new PlayState(game, assets, tilemap));
	}
	
	public static SetStateButton setLevelButton(MermaidGame game, Assets assets, String name)
	{
		return new SetStateButton(game, new PlayState(game, assets, assets.getTilemap(name)));
	}
	
	private static class SetStateButton implements ButtonComponent.Action 
	{
		public MermaidGame game;
		public GameState state;
		
		public SetStateButton(MermaidGame game, GameState state)
		{
			this.game = game;
			this.state = state;
		}
		
		@Override
		public void onClick() 
		{
			game.setState(state);
		}
	}
}
