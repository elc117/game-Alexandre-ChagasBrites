package com.alegz.mermaid.states;

import com.alegz.ecs.Engine;
import com.alegz.ecs.Entity;
import com.alegz.mermaid.Assets;
import com.alegz.mermaid.GameBuilder;
import com.alegz.mermaid.MermaidGame;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.Water;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.alegz.mermaid.systems.CameraSystem;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PlayerSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class LevelEditorState implements GameState
{
	private MermaidGame game;
	private Assets assets;
	
	private Tilemap tilemap;
	private String path;
	
	private PlatformerCamera platformerCamera;
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
	
	@Override
	public void create() 
	{
		platformerCamera = new PlatformerCamera();
		platformerCamera.backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), tilemap.getWidth());
		
		engine = new Engine(1024);
		
		PlayerSystem playerSystem = new PlayerSystem(engine, water, platformerCamera);
		PhysicsSystem physicsSystem = new PhysicsSystem(engine);
		CameraSystem cameraSystem = new CameraSystem(engine);
		RenderingSystem renderingSystem = new RenderingSystem(engine, platformerCamera, assets.getFont());
		
		engine.addSystem(playerSystem);
		engine.addSystem(physicsSystem);
		engine.addSystem(cameraSystem);
		engine.addSystem(renderingSystem);
		//engine.addSystem(new PhysicsDebugSystem(physicsSystem.getWorld(), renderingSystem.getCamera()));
		
		GameBuilder.createBackground(engine, assets, platformerCamera);
		GameBuilder.createTilemap(engine, assets, tilemap);
		
		Entity playerEntity = GameBuilder.createPlayer(engine, assets);
		TransformComponent playerTransform = engine.getComponent(playerEntity, TransformComponent.class);
		PlayerComponent player = engine.getComponent(playerEntity, PlayerComponent.class);
		
		GameBuilder.createCamera(engine, assets, tilemap, platformerCamera, playerTransform, player);
		GameBuilder.createWater(engine, assets, tilemap);
		
		time = 0;
	}

	@Override
	public void update() 
	{
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			game.setState(new MenuState(game, assets));
		
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
		
		assets.getMaterial(Assets.MATERIAL_SPRITE).setFloat("u_time", time);
		assets.getMaterial(Assets.MATERIAL_PLANT).setFloat("u_time", time);
		
		Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		mousePos = platformerCamera.getScreenToWorldPosition(mousePos.x, mousePos.y);
		
		if (Gdx.input.isButtonPressed(Buttons.LEFT))
			tilemap.setTile(mousePos, (byte)1);
		else if (Gdx.input.isButtonPressed(Buttons.RIGHT))
			tilemap.setTile(mousePos, (byte)0);
		
		tilemapRenderer.createMesh();
		
		water.update(deltaTime);
		engine.update(deltaTime);
		
		time += deltaTime;
	}

	@Override
	public void resize(int width, int height) 
	{
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
		tilemap.save(path);
	}
}
