package com.alegz.mermaid.states;

import com.alegz.mermaid.Assets;
import com.alegz.mermaid.MermaidGame;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.CameraSystem;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PlayerSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LevelEditorState implements GameState
{
	private MermaidGame game;
	private Assets assets;
	
	private Tilemap tilemap;
	private String path;
	
	private Water water;
	
	private Engine engine;
	private float time;
	
	private TilemapRendererComponent tilemapRenderer;
	
	public LevelEditorState(MermaidGame game, Assets assets, Tilemap tilemap, String path)
	{
		this.game = game;
		this.assets = assets;
		this.tilemap = tilemap;
		this.path = path;
	}
	
	public void create() 
	{
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), tilemap.getWidth());
		
		engine = new Engine();
		engine.addComponentStorage(TransformComponent.class);
		engine.addComponentStorage(SpriteRendererComponent.class);
		engine.addComponentStorage(MeshRendererComponent.class);
		engine.addComponentStorage(TilemapRendererComponent.class);
		engine.addComponentStorage(UITransformComponent.class);
		engine.addComponentStorage(ImageRendererComponent.class);
		engine.addComponentStorage(TextRendererComponent.class);
		engine.addComponentStorage(RigidbodyComponent.class);
		engine.addComponentStorage(PlayerComponent.class);
		engine.addComponentStorage(CameraComponent.class);
		
		PhysicsSystem physicsSystem = new PhysicsSystem();
		RenderingSystem renderingSystem = new RenderingSystem(assets.getFont());
		renderingSystem.getCamera().backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		engine.addSystem(new PlayerSystem(water));
		engine.addSystem(physicsSystem);
		engine.addSystem(new CameraSystem());
		engine.addSystem(renderingSystem);
		//engine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), renderingSystem.getCamera()));
		
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
		
		//tilemap
		{	
			Entity entity = new Entity();
			TransformComponent transform = new TransformComponent();
			
			TilemapRendererComponent tilemapRenderer = new TilemapRendererComponent(tilemap,
				assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TILE));
			tilemapRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, tilemapRenderer);
			engine.addEntity(entity);
			
			this.tilemapRenderer = tilemapRenderer;
		}
		
		TransformComponent playerTransform;
		PlayerComponent player;
		
		//player
		{
			Entity entity = new Entity();
			TransformComponent transform = new TransformComponent();
			transform.scale.x = transform.scale.y = 1.5f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER);
			spriteRenderer.pivot.y = 0.25f;
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
			
			RigidbodyComponent rigidbody = new RigidbodyComponent();
			rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.DynamicBody);
			rigidbody.getBody().setFixedRotation(true);
			
			player = new PlayerComponent();
			
			CameraComponent camera = new CameraComponent(renderingSystem.getCamera());
			camera.target = transform;
			camera.minBounds = tilemap.getWorldPos(0, tilemap.getHeight());
			camera.maxBounds = tilemap.getWorldPos(tilemap.getWidth(), 0);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addComponent(entity, rigidbody);
			engine.addComponent(entity, player);
			//engine.addComponent(entity, camera);
			engine.addEntity(entity);
			
			playerTransform = transform;
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
		
		//water
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), -tilemap.getWorldPos(0, tilemap.getHeight()).y);
			transform.position = new Vector2(0, -transform.scale.y * 0.5f + 0.5f);
			transform.scale.y += 1.0f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);
			
			Entity entity = new Entity();
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addEntity(entity);
		}
		
		engine.start();
		time = 0;
	}

	public void update() 
	{
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			game.setState(new MenuState(game, assets));
		
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
		
		assets.getMaterial(Assets.MATERIAL_SPRITE).setFloat("u_time", time);
		assets.getMaterial(Assets.MATERIAL_PLANT).setFloat("u_time", time);
		
		PlatformerCamera camera = engine.getSystem(RenderingSystem.class).getCamera();
		Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		mousePos = camera.getScreenToWorldPosition(mousePos.x, mousePos.y);
		
		if (Gdx.input.isButtonPressed(Buttons.LEFT))
			tilemap.setTile(mousePos, (byte)1);
		else if (Gdx.input.isButtonPressed(Buttons.RIGHT))
			tilemap.setTile(mousePos, (byte)0);
		
		tilemapRenderer.createMesh();
		
		water.update(deltaTime);
		engine.update(deltaTime);
		
		time += deltaTime;
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
		tilemap.save(path);
	}
}
