package com.alegz.mermaid;

import java.util.HashMap;
import com.alegz.mermaid.rendering.Sprite;
import com.alegz.mermaid.rendering.SpriteAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Assets 
{
	private HashMap<String, Texture> textures;
	private HashMap<String, ShaderProgram> shaders;
	private HashMap<String, Sprite> sprites;
	private HashMap<String, SpriteAtlas> spriteAtlas;
	private HashMap<String, Tilemap> tilemaps;
	
	private static final String TEXTURE_BLANK = "textures/blank.png";
	private static final String TEXTURE_PLAYER = "textures/player.png";
	private static final String TEXTURE_TILE = "textures/tile.png";
	private static final String TEXTURE_BACKGROUND0 = "textures/background0.png";
	private static final String TEXTURE_BACKGROUND1 = "textures/background1.png";
	private static final String TEXTURE_BACKGROUND2 = "textures/background2.png";
	private static final String TEXTURE_WATERGRADIENT = "textures/waterGradient.png";
	
	public static final String SHADER_SPRITE = "shaders/sprite";
	public static final String SHADER_WATER = "shaders/water";
	public static final String SHADER_BACKGROUND = "shaders/background";
	
	public static final String SPRITE_NULL = "null";
	public static final String SPRITE_PLAYER = "player";
	public static final String SPRITE_BACKGROUND0 = "background0";
	public static final String SPRITE_BACKGROUND1 = "background1";
	public static final String SPRITE_BACKGROUND2 = "background2";
	public static final String SPRITE_WATERGRADIENT = "watergradient";
	
	public static final String SPRITE_ATLAS_TILE = "tile";
	
	public static final String TILEMAP_LEVEL0 = "levels/level0.mgl";
	
	public Assets()
	{
		textures = new HashMap<String, Texture>();
		shaders = new HashMap<String, ShaderProgram>();
		sprites = new HashMap<String, Sprite>();
		spriteAtlas = new HashMap<String, SpriteAtlas>();
		tilemaps = new HashMap<String, Tilemap>();
	}
	
	public void load()
	{
		loadTexture(TEXTURE_BLANK, TextureFilter.Nearest);
		loadTexture(TEXTURE_PLAYER, TextureFilter.Nearest);
		loadTexture(TEXTURE_TILE, TextureFilter.Nearest);
		loadTexture(TEXTURE_BACKGROUND0, TextureFilter.Linear);
		loadTexture(TEXTURE_BACKGROUND1, TextureFilter.Linear);
		loadTexture(TEXTURE_BACKGROUND2, TextureFilter.Linear);
		loadTexture(TEXTURE_WATERGRADIENT, TextureFilter.Linear);
		
		loadShader(SHADER_SPRITE);
		loadShader(SHADER_WATER);
		loadShader(SHADER_BACKGROUND);
		
		sprites.put(SPRITE_NULL, new Sprite(null, new Rect(0, 0, 1, 1)));
		loadSprite(SPRITE_PLAYER, TEXTURE_PLAYER, new Rect(0, 0, 24, 24));
		loadSprite(SPRITE_BACKGROUND0, TEXTURE_BACKGROUND0, new Rect(0, 0, 512, 512));
		loadSprite(SPRITE_BACKGROUND1, TEXTURE_BACKGROUND1, new Rect(0, 0, 512, 512));
		loadSprite(SPRITE_BACKGROUND2, TEXTURE_BACKGROUND2, new Rect(0, 0, 512, 512));
		loadSprite(SPRITE_WATERGRADIENT, TEXTURE_WATERGRADIENT, new Rect(0, 0, 512, 512));
		
		loadSpriteAtlas(SPRITE_ATLAS_TILE, TEXTURE_TILE);
		
		loadTilemap(TILEMAP_LEVEL0);
	}
	
	public ShaderProgram getShader(String path)
	{
		return shaders.get(path);
	}
	
	public Sprite getSprite(String name)
	{
		return sprites.get(name);
	}
	
	public SpriteAtlas getSpriteAtlas(String name)
	{
		return spriteAtlas.get(name);
	}
	
	public Tilemap getTilemap(String name)
	{
		return tilemaps.get(name);
	}
	
	private void loadTexture(String path, TextureFilter filter)
	{
		Texture texture = new Texture(Gdx.files.internal(path));
		texture.setFilter(filter, filter);
		textures.put(path, texture);
	}
	
	private void loadShader(String path)
	{
		//ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(
			Gdx.files.internal(path + "Vert.glsl").readString(),
			Gdx.files.internal(path + "Frag.glsl").readString());
		if (!shader.isCompiled())
			throw new IllegalArgumentException("Error compiling shader " + path + ": " + shader.getLog());
		shaders.put(path, shader);
	}
	
	private void loadSprite(String key, String path, Rect rect)
	{
		Texture texture = textures.get(path);
		rect.x /= (float)texture.getWidth();
		rect.y /= (float)texture.getHeight();
		rect.width /= (float)texture.getWidth();
		rect.height /= (float)texture.getHeight();
		sprites.put(key, new Sprite(texture, rect));
	}
	
	private void loadSpriteAtlas(String key, String path)
	{
		Texture texture = textures.get(path);
		spriteAtlas.put(key, new SpriteAtlas(texture));
	}
	
	private void loadTilemap(String path)
	{
		tilemaps.put(path, new Tilemap(path));
	}
	
	public void dispose()
	{
		for (Texture texture : textures.values())
			texture.dispose();
		for (ShaderProgram shader : shaders.values())
			shader.dispose();
	}
}
