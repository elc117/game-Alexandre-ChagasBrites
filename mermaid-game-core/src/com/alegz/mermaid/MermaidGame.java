package com.alegz.mermaid;

import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.physics.BoxCollider;
import com.alegz.mermaid.physics.TilemapCollider;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.CameraSystem;
import com.alegz.mermaid.systems.PhysicsDebugSystem;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PlayerSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MermaidGame extends ApplicationAdapter 
{
	private Assets assets;
	private Engine engine;
	
	public void create () 
	{
		assets = new Assets();
		assets.load();
		Tilemap tilemap = assets.getTilemap(Assets.TILEMAP_LEVEL0);
		
		engine = new Engine();
		PhysicsSystem physicsSystem = new PhysicsSystem();
		RenderingSystem renderingSystem = new RenderingSystem();
		renderingSystem.getCamera().backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		engine.addSystem(new PlayerSystem());
		engine.addSystem(physicsSystem);
		engine.addSystem(new CameraSystem());
		engine.addSystem(renderingSystem);
		engine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), renderingSystem.getCamera()));
		
		Material spriteMaterial = new Material(assets.getShader(Assets.SHADER_SPRITE));
		
		Material waterMaterial = new Material(assets.getShader(Assets.SHADER_WATER));
		
		Material backgroundMaterial = new Material(assets.getShader(Assets.SHADER_BACKGROUND));
		backgroundMaterial.setColor("u_topColor", renderingSystem.getCamera().backgroundColor);
		backgroundMaterial.setColor("u_bottomColor", new Color(76 / 255.0f, 106 / 255.0f, 200 / 255.0f, 1));
		backgroundMaterial.setFloat("u_nearPlane", renderingSystem.getCamera().nearPlane);
		backgroundMaterial.setFloat("u_farPlane", renderingSystem.getCamera().farPlane);
		
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), -tilemap.getWorldPos(0, tilemap.getHeight()).y);
			transform.position = new Vector2(0, -transform.scale.y * 0.5f);
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_WATERGRADIENT);
			spriteRenderer.material = spriteMaterial;
			
			Entity entity = new Entity();
			entity.add(transform);
			entity.add(spriteRenderer);
			engine.addEntity(entity);
		}
		
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), tilemap.getHeight());
			transform.position = new Vector2(0, tilemap.getWorldPos(0, tilemap.getHeight() / 2).y);
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND2);
			spriteRenderer.depth = 1.5f;
			spriteRenderer.material = backgroundMaterial;
			
			Entity entity = new Entity();
			entity.add(transform);
			entity.add(spriteRenderer);
			engine.addEntity(entity);
		}
		
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), tilemap.getHeight());
			transform.position = new Vector2(0, tilemap.getWorldPos(0, tilemap.getHeight() / 2).y);
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND1);
			spriteRenderer.depth = 1.0f;
			spriteRenderer.material = backgroundMaterial;
			
			Entity entity = new Entity();
			entity.add(transform);
			entity.add(spriteRenderer);
			engine.addEntity(entity);
		}
		
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), tilemap.getHeight());
			transform.position = new Vector2(0, tilemap.getWorldPos(0, tilemap.getHeight() / 2).y);
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_BACKGROUND0);
			spriteRenderer.depth = 0.5f;
			spriteRenderer.material = backgroundMaterial;
			
			Entity entity = new Entity();
			entity.add(transform);
			entity.add(spriteRenderer);
			engine.addEntity(entity);
		}
		
		//player
		{
			TransformComponent transform = new TransformComponent();
			transform.scale.x = transform.scale.y = 1.5f;
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_PLAYER);
			spriteRenderer.pivot.y = 0.25f;
			spriteRenderer.material = spriteMaterial;
			
			RigidbodyComponent rigidbody = new RigidbodyComponent();
			rigidbody.create(physicsSystem.getWorld(), BodyType.DynamicBody);
			rigidbody.body.setFixedRotation(true);
			
			BoxCollider collider = new BoxCollider(new Vector2(0.5f, 1), new Vector2(0, 0.125f));
			rigidbody.setCollider(collider);
			
			PlayerComponent player = new PlayerComponent();
			
			CameraComponent camera = new CameraComponent(renderingSystem.getCamera());
			camera.target = transform;
			camera.minBounds = tilemap.getWorldPos(0, tilemap.getHeight());
			camera.maxBounds = tilemap.getWorldPos(tilemap.getWidth(), 0);
			
			Entity entity = new Entity();
			entity.add(transform);
			entity.add(spriteRenderer);
			entity.add(rigidbody);
			entity.add(player);
			entity.add(camera);
			engine.addEntity(entity);
		}
		
		//tilemap
		{	
			TilemapRendererComponent tilemapRenderer = new TilemapRendererComponent(tilemap,
				assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TILE));
			tilemapRenderer.material = spriteMaterial;
			
			RigidbodyComponent rigidbody = new RigidbodyComponent();
			rigidbody.create(physicsSystem.getWorld(), BodyType.StaticBody);
			
			TilemapCollider collider = new TilemapCollider(tilemap);
			rigidbody.setCollider(collider);
			
			Entity entity = new Entity();
			entity.add(new TransformComponent());
			entity.add(tilemapRenderer);
			entity.add(rigidbody);
			engine.addEntity(entity);
		}
		
		//water
		{
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), -tilemap.getWorldPos(0, tilemap.getHeight()).y);
			transform.position = new Vector2(0, -transform.scale.y * 0.5f);
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_NULL);
			spriteRenderer.material = waterMaterial;
			
			Entity entity = new Entity();
			entity.add(transform);
			entity.add(spriteRenderer);
			engine.addEntity(entity);
		}
	}

	public void render () 
	{
		engine.update(Gdx.graphics.getDeltaTime());
	}
	
	public void dispose () 
	{
		assets.dispose();
		RenderingSystem renderingSystem = engine.getSystem(RenderingSystem.class);
		renderingSystem.dispose();
	}
}
