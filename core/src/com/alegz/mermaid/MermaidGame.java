package com.alegz.mermaid;

import com.alegz.mermaid.components.CameraComponent;
import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.PlayerComponent;
import com.alegz.mermaid.components.RigidbodyComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.TrashComponent;
import com.alegz.mermaid.ecs.Engine;
import com.alegz.mermaid.ecs.Entity;
import com.alegz.mermaid.physics.BoxCollider;
import com.alegz.mermaid.physics.CircleCollider;
import com.alegz.mermaid.physics.TilemapCollider;
import com.alegz.mermaid.rendering.SpriteAtlas;
import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.systems.CameraSystem;
import com.alegz.mermaid.systems.PhysicsDebugSystem;
import com.alegz.mermaid.systems.PhysicsSystem;
import com.alegz.mermaid.systems.PlayerSystem;
import com.alegz.mermaid.systems.PollutionSystem;
import com.alegz.mermaid.systems.RenderingSystem;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MermaidGame extends ApplicationAdapter 
{
	private Assets assets;
	private Water water;
	
	private Engine engine;
	private float time;
	
	private Mesh screenMesh;
	
	public void create () 
	{
		assets = new Assets();
		assets.load();
		Tilemap tilemap = assets.getTilemap(Assets.TILEMAP_LEVEL0);
		water = new Water(assets.getMaterial(Assets.MATERIAL_WATER), tilemap.getWidth());
		
		{
			screenMesh = new Mesh(false, 4, 6,
							new VertexAttribute(VertexAttributes.Usage.Position, 3, GL20.GL_FLOAT, false, ShaderProgram.POSITION_ATTRIBUTE),
							new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, GL20.GL_FLOAT, false, ShaderProgram.TEXCOORD_ATTRIBUTE));
	
			float[] vertices = new float[] {
					-1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
					 1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
					-1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
					 1.0f,  1.0f, 0.0f, 1.0f, 0.0f
			};
			short[] indices = new short[] {
					0, 1, 2,
					2, 1, 3
			};
			
			screenMesh.setVertices(vertices);
			screenMesh.setIndices(indices);
		}
		
		engine = new Engine();
		engine.addComponentStorage(TransformComponent.class);
		engine.addComponentStorage(SpriteRendererComponent.class);
		engine.addComponentStorage(MeshRendererComponent.class);
		engine.addComponentStorage(TilemapRendererComponent.class);
		engine.addComponentStorage(RigidbodyComponent.class);
		engine.addComponentStorage(PlayerComponent.class);
		engine.addComponentStorage(TrashComponent.class);
		engine.addComponentStorage(CameraComponent.class);
		
		PhysicsSystem physicsSystem = new PhysicsSystem();
		RenderingSystem renderingSystem = new RenderingSystem();
		renderingSystem.getCamera().backgroundColor = new Color(204 / 255.0f, 232 / 255.0f, 255 / 255.0f, 1);
		
		engine.addSystem(new PlayerSystem(water));
		engine.addSystem(new PollutionSystem(assets.getMaterial(Assets.MATERIAL_WATER)));
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
			TransformComponent transform = new TransformComponent();
			transform.scale = new Vector2(tilemap.getWidth(), tilemap.getHeight());
			transform.position = new Vector2(0, tilemap.getWorldPos(0, tilemap.getHeight() / 2).y);
			
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
			spriteRenderer.depth = 4.0f - i;
			spriteRenderer.material = backgroundMaterial;
			
			Entity entity = new Entity();
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addEntity(entity);
		}
		
		//trash
		{
			SpriteAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_TRASH);
			for (int y = 0; y < tilemap.getHeight(); y++)
			{
				for (int x = 0; x < tilemap.getWidth(); x++)
				{
					if (MathUtils.random() > 1.0f / 4.0f || tilemap.getTile(x, y) != 0)
						continue;
					
					Entity entity = new Entity();
					TransformComponent transform = new TransformComponent();
					transform.position = tilemap.getWorldPos(x, y);
					transform.position.x += 0.5f;
					transform.position.y -= 0.5f;
					if (transform.position.y > 0)
						continue;
					transform.position.x += MathUtils.random(-0.25f, 0.25f);
					transform.position.y += MathUtils.random(-0.25f, 0.25f);
					transform.rotation = MathUtils.random(0, 360);
					
					SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
					spriteRenderer.sprite = spriteAtlas.getSprite(MathUtils.random(0,2), MathUtils.random(0,1));
					spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_SPRITE);
					
					RigidbodyComponent rigidbody = new RigidbodyComponent(PhysicsSystem.CATEGORY_TRASH, PhysicsSystem.MASK_TRASH);
					rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.KinematicBody);
					
					CircleCollider collider = new CircleCollider(0.5f*2);
					collider.isSensor = true;
					rigidbody.addCollider(collider);
					
					engine.addComponent(entity, transform);
					engine.addComponent(entity, spriteRenderer);
					engine.addComponent(entity, rigidbody);
					engine.addComponent(entity, new TrashComponent());
					engine.addEntity(entity);
				}
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
			
			RigidbodyComponent rigidbody = new RigidbodyComponent(PhysicsSystem.CATEGORY_PLAYER, PhysicsSystem.MASK_PLAYER);
			rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.DynamicBody);
			rigidbody.getBody().setFixedRotation(true);
			
			BoxCollider collider = new BoxCollider(new Vector2(0.5f, 0.5f), new Vector2(0, 0));
			rigidbody.addCollider(collider);
			
			PlayerComponent player = new PlayerComponent();
			
			CameraComponent camera = new CameraComponent(renderingSystem.getCamera());
			camera.target = transform;
			camera.minBounds = tilemap.getWorldPos(0, tilemap.getHeight());
			camera.maxBounds = tilemap.getWorldPos(tilemap.getWidth(), 0);
			
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addComponent(entity, rigidbody);
			engine.addComponent(entity, player);
			engine.addComponent(entity, camera);
			engine.addEntity(entity);
		}
		
		//plants
		{
			SpriteAtlas spriteAtlas = assets.getSpriteAtlas(Assets.SPRITE_ATLAS_PLANTS);
			for (int y = 0; y < tilemap.getHeight(); y++)
			{
				for (int x = 0; x < tilemap.getWidth(); x++)
				{
					if (tilemap.getTile(x, y) != 0 ||
						tilemap.getTile(x, y + 1) == 0 ||
						MathUtils.random() > 1.0f / 3.0f )
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
					spriteRenderer.sprite = spriteAtlas.getSprite(MathUtils.random(0, 3), MathUtils.random(0, 0));
					spriteRenderer.pivot.y = 0;
					spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_PLANT);
					
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
			
			RigidbodyComponent rigidbody = new RigidbodyComponent(PhysicsSystem.CATEGORY_WORLD, PhysicsSystem.MASK_WORLD);
			rigidbody.create(physicsSystem.getWorld(), entity, transform, BodyType.StaticBody);
			
			TilemapCollider collider = new TilemapCollider(tilemap);
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
			
			/*Mesh mesh;
			{
				int quadCount = tilemap.getWidth() * 16;
				int verticesCount = (quadCount + 1) * 2;
				int indicesCount = quadCount * 6;
				mesh = new Mesh(false, verticesCount, indicesCount,
								new VertexAttribute(VertexAttributes.Usage.Position, 3, GL20.GL_FLOAT, false, ShaderProgram.POSITION_ATTRIBUTE),
								new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, GL20.GL_FLOAT, false, ShaderProgram.TEXCOORD_ATTRIBUTE));
		
				float[] vertices = new float[verticesCount * 5];
				short[] indices = new short[indicesCount];
				
				int activeVertices = 0;
				int activeIndices = 0;
				for (int i = 0; i < quadCount; i++)
				{
					indices[activeIndices++] = (short)(i * 2);
					indices[activeIndices++] = (short)(i * 2 + 2);
					indices[activeIndices++] = (short)(i * 2 + 1);
					indices[activeIndices++] = (short)(i * 2 + 1);
					indices[activeIndices++] = (short)(i * 2 + 2);
					indices[activeIndices++] = (short)(i * 2 + 3);
				}
				
				for (int i = 0; i < quadCount + 1; i++)
				{
					float x = ((float)i / quadCount - 0.5f) * transform.scale.x;
					float y = -transform.scale.y;
					
					vertices[activeVertices++] = x;
					vertices[activeVertices++] = 0.0f;
					vertices[activeVertices++] = 0.0f;
					vertices[activeVertices++] = (float)i / quadCount;
					vertices[activeVertices++] = 0.0f;
					
					vertices[activeVertices++] = x;
					vertices[activeVertices++] = y;
					vertices[activeVertices++] = 0.0f;
					vertices[activeVertices++] = (float)i / quadCount;
					vertices[activeVertices++] = 1.0f;
				}
				
				mesh.setVertices(vertices);
				mesh.setIndices(indices);
			}
			
			MeshRendererComponent meshRenderer = new MeshRendererComponent();
			meshRenderer.mesh = mesh;
			meshRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);*/
			
			SpriteRendererComponent spriteRenderer = new SpriteRendererComponent();
			spriteRenderer.sprite = assets.getSprite(Assets.SPRITE_NULL);
			spriteRenderer.material = assets.getMaterial(Assets.MATERIAL_WATER);
			
			Entity entity = new Entity();
			engine.addComponent(entity, transform);
			engine.addComponent(entity, spriteRenderer);
			engine.addEntity(entity);
		}
		
		engine.start();
		time = 0;
	}

	public void render () 
	{
		assets.getMaterial(Assets.MATERIAL_SPRITE).setFloat("u_time", time);
		assets.getMaterial(Assets.MATERIAL_PLANT).setFloat("u_time", time);
		
		water.update(Gdx.graphics.getDeltaTime());
		engine.update(Gdx.graphics.getDeltaTime());
		
		time += Gdx.graphics.getDeltaTime();
	}
	
	public void dispose () 
	{
		assets.dispose();
		RenderingSystem renderingSystem = engine.getSystem(RenderingSystem.class);
		renderingSystem.dispose();
	}
}
