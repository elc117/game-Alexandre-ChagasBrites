package com.alegz.mermaid;

import java.util.HashMap;

import com.alegz.mermaid.rendering.Shader;
import com.alegz.mermaid.rendering.material.Material;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Assets 
{
	private HashMap<String, Texture> textures;
	private HashMap<String, Shader> shaders;
	private HashMap<String, TextureRegion> sprites;
	private HashMap<String, TextureAtlas> spritesAtlas;
	private HashMap<String, Material> materials;
	private HashMap<String, FishType> fishTypes;
	private HashMap<String, Sound> sounds;
	private HashMap<String, Tilemap> tilemaps;
	private Music music;
	private PixelFont font;
	
	private static final String TEXTURE_ENTITIES = "textures/entities.png";
	private static final String TEXTURE_TILE = "textures/tile.png";
	private static final String TEXTURE_PLANTS = "textures/plants.png";
	private static final String TEXTURE_BACKGROUND0 = "textures/background0.png";
	private static final String TEXTURE_BACKGROUND1 = "textures/background1.png";
	private static final String TEXTURE_BACKGROUND2 = "textures/background2.png";
	private static final String TEXTURE_BACKGROUND3 = "textures/background3.png";
	private static final String TEXTURE_UI = "textures/ui.png";
	
	public static final String SHADER_SPRITE = "shaders/sprite";
	public static final String SHADER_PLANT = "shaders/plant";
	public static final String SHADER_WATER = "shaders/water";
	public static final String SHADER_BACKGROUND = "shaders/background";
	public static final String SHADER_UI = "shaders/ui";
	public static final String SHADER_TRANSITION = "shaders/transition";
	
	public static final String SPRITE_PLAYER = "player";
	public static final String SPRITE_PLAYER_TAIL = "player_tail";
	public static final String SPRITE_PLAYER_STAMINA_BAR = "player_stamina_bar";
	public static final String SPRITE_PLAYER_STAMINA_BORDER = "player_stamina_border";
	public static final String SPRITE_BACKGROUND0 = "background0";
	public static final String SPRITE_BACKGROUND1 = "background1";
	public static final String SPRITE_BACKGROUND2 = "background2";
	public static final String SPRITE_BACKGROUND3 = "background3";
	
	public static final String SPRITE_ATLAS_FISH = "fish";
	public static final String SPRITE_ATLAS_TRASH = "trash";
	public static final String SPRITE_ATLAS_TILE = "tile";
	public static final String SPRITE_ATLAS_PLANTS = "seaweed";
	public static final String SPRITE_ATLAS_UI = "ui";
	
	public static final String MATERIAL_SPRITE = "sprite";
	public static final String MATERIAL_PLANT = "plant";
	public static final String MATERIAL_WATER = "water";
	public static final String MATERIAL_BACKGROUND = "background";
	public static final String MATERIAL_UI = "ui";
	public static final String MATERIAL_TRANSITION = "transition";
	
	public static final String[] FISH_TYPES = new String[] { "type0", "type1", "type2", "type3"};
	
	public static final String SOUND_TRASH = "sounds/trash.ogg";
	public static final String SOUND_DASH = "sounds/dash.ogg";
	public static final String SOUND_SPLASH = "sounds/splash.ogg";
	public static final String SOUND_IMPACT = "sounds/impact.ogg";
	public static final String SOUND_PAUSE_IN = "sounds/pauseIn.ogg";
	public static final String SOUND_PAUSE_OUT = "sounds/pauseOut.ogg";
	public static final String SOUND_LEVEL_COMPLETE = "sounds/levelComplete.ogg";
	
	public static final String TILEMAP_LEVEL0 = "levels/level0.json";
	public static final String TILEMAP_LEVEL1 = "levels/level1.json";
	public static final String TILEMAP_LEVEL2 = "levels/level2.json";
	
	public Assets()
	{
		textures = new HashMap<>();
		shaders = new HashMap<>();
		sprites = new HashMap<>();
		spritesAtlas = new HashMap<>();
		materials = new HashMap<>();
		fishTypes = new HashMap<>();
		sounds = new HashMap<>();
		tilemaps = new HashMap<>();
	}
	
	public void load()
	{
		loadTexture(TEXTURE_ENTITIES, TextureFilter.Nearest);
		loadTexture(TEXTURE_TILE, TextureFilter.Nearest);
		loadTexture(TEXTURE_PLANTS, TextureFilter.Nearest);
		loadTexture(TEXTURE_BACKGROUND0, TextureFilter.Linear);
		loadTexture(TEXTURE_BACKGROUND1, TextureFilter.Linear);
		loadTexture(TEXTURE_BACKGROUND2, TextureFilter.Linear);
		loadTexture(TEXTURE_BACKGROUND3, TextureFilter.Linear);
		loadTexture(TEXTURE_UI, TextureFilter.Nearest);
		
		loadShader(SHADER_SPRITE, false);
		loadShader(SHADER_PLANT, false);
		loadShader(SHADER_WATER, true);
		loadShader(SHADER_BACKGROUND, true);
		loadShader(SHADER_UI, false);
		loadShader(SHADER_TRANSITION, false);
		
		loadSprite(SPRITE_PLAYER, TEXTURE_ENTITIES, 0, 0, 24, 24);
		loadSprite(SPRITE_PLAYER_TAIL, TEXTURE_ENTITIES, 2, 34, 12, 12);
		loadSprite(SPRITE_PLAYER_STAMINA_BAR, TEXTURE_ENTITIES, 0, 48, 16, 4);
		loadSprite(SPRITE_PLAYER_STAMINA_BORDER, TEXTURE_ENTITIES, 0, 56, 16, 4);
		loadSprite(SPRITE_BACKGROUND0, TEXTURE_BACKGROUND0, 0, 0, 512, 512);
		loadSprite(SPRITE_BACKGROUND1, TEXTURE_BACKGROUND1, 0, 0, 512, 512);
		loadSprite(SPRITE_BACKGROUND2, TEXTURE_BACKGROUND2, 0, 0, 512, 512);
		loadSprite(SPRITE_BACKGROUND3, TEXTURE_BACKGROUND3, 0, 0, 512, 512);
		
		{
			TextureAtlas atlas = new TextureAtlas();
			Texture texture = textures.get(TEXTURE_ENTITIES);
			atlas.addRegion("fish0", new TextureRegion(texture, 32,  0, 16, 16));
			atlas.addRegion("fish1", new TextureRegion(texture, 32, 16, 16, 16));
			atlas.addRegion("fish2", new TextureRegion(texture, 48,  0, 16, 16));
			atlas.addRegion("fish3", new TextureRegion(texture, 48, 16, 16, 16));
			loadSpriteAtlas(SPRITE_ATLAS_FISH, atlas);
		}
		{
			TextureAtlas atlas = new TextureAtlas();
			Texture texture = textures.get(TEXTURE_ENTITIES);
			atlas.addRegion("trash0", new TextureRegion(texture,16,32, 16, 16));
			atlas.addRegion("trash1", new TextureRegion(texture,16,32, 16, 16));
			atlas.addRegion("trash2", new TextureRegion(texture,48,32, 16, 16));
			atlas.addRegion("trash3", new TextureRegion(texture,16,48, 16, 16));
			atlas.addRegion("trash4", new TextureRegion(texture,32,48, 16, 16));
			atlas.addRegion("trash5", new TextureRegion(texture,48,48, 16, 16));
			loadSpriteAtlas(SPRITE_ATLAS_TRASH, atlas);
		}
		{
			TextureAtlas atlas = new TextureAtlas();
			Texture texture = textures.get(TEXTURE_TILE);
			atlas.addRegion("tile0", new TextureRegion(texture, 0, 0, 16, 16));
			atlas.addRegion("tile1", new TextureRegion(texture,16, 0, 16, 16));
			atlas.addRegion("tile2", new TextureRegion(texture,32, 0, 16, 16));
			atlas.addRegion("tile3", new TextureRegion(texture,48, 0, 16, 16));
			atlas.addRegion("tile4", new TextureRegion(texture,64, 0, 16, 16));
			atlas.addRegion("tile5", new TextureRegion(texture, 0,16, 16, 16));
			atlas.addRegion("tile6", new TextureRegion(texture,16,16, 16, 16));
			atlas.addRegion("tile7", new TextureRegion(texture,32,16, 16, 16));
			atlas.addRegion("tile8", new TextureRegion(texture,48,16, 16, 16));
			atlas.addRegion("tile9", new TextureRegion(texture,64,16, 16, 16));
			atlas.addRegion("tile10", new TextureRegion(texture, 0,32, 16, 16));
			atlas.addRegion("tile11", new TextureRegion(texture,16,32, 16, 16));
			atlas.addRegion("tile12", new TextureRegion(texture,32,32, 16, 16));
			loadSpriteAtlas(SPRITE_ATLAS_TILE, atlas);
		}
		{
			TextureAtlas atlas = new TextureAtlas();
			Texture texture = textures.get(TEXTURE_PLANTS);
			atlas.addRegion("plant0", new TextureRegion(texture, 0, 0, 16, 32));
			atlas.addRegion("plant1", new TextureRegion(texture,16, 0, 16, 32));
			atlas.addRegion("plant2", new TextureRegion(texture,32, 0, 16, 32));
			atlas.addRegion("plant3", new TextureRegion(texture,48, 0, 16, 32));
			atlas.addRegion("plant4", new TextureRegion(texture,64, 0, 16, 32));
			atlas.addRegion("plant5", new TextureRegion(texture,80, 0, 16, 32));
			loadSpriteAtlas(SPRITE_ATLAS_PLANTS, atlas);
		}
		{
			TextureAtlas atlas = new TextureAtlas();
			Texture texture = textures.get(TEXTURE_UI);
			atlas.addRegion("buttonDefault", new TextureRegion(texture, 0, 80, 128, 16));
			atlas.addRegion("buttonHighlight", new TextureRegion(texture, 0, 96, 128, 16));
			atlas.addRegion("buttonPressed", new TextureRegion(texture, 0, 112, 128, 16));
			atlas.addRegion("musicButtonDefault", new TextureRegion(texture, 112, 0, 16, 16));
			atlas.addRegion("musicButtonHighlight", new TextureRegion(texture, 112, 16, 16, 16));
			atlas.addRegion("musicButtonPressed", new TextureRegion(texture, 112, 32, 16, 16));
			atlas.addRegion("checkmark", new TextureRegion(texture, 112, 64, 16, 16));
			loadSpriteAtlas(SPRITE_ATLAS_UI, atlas);
		}
		
		loadMaterial(MATERIAL_SPRITE, SHADER_SPRITE);
		loadMaterial(MATERIAL_PLANT, SHADER_PLANT);
		loadMaterial(MATERIAL_WATER, SHADER_WATER);
		loadMaterial(MATERIAL_BACKGROUND, SHADER_BACKGROUND);
		loadMaterial(MATERIAL_UI, SHADER_UI);
		loadMaterial(MATERIAL_TRANSITION, SHADER_TRANSITION);
		
		{
			TextureAtlas atlas = getSpriteAtlas(SPRITE_ATLAS_FISH);
			loadFishType(FISH_TYPES[0], new FishType(5, 2, atlas.getRegions().get(0), 20, 0.05f, 10));
			loadFishType(FISH_TYPES[1], new FishType(5, 2.5f, atlas.getRegions().get(1), 20, 0.075f, 10));
			loadFishType(FISH_TYPES[2], new FishType(5, 1.5f, atlas.getRegions().get(2), 20, 0.025f, 10));
			loadFishType(FISH_TYPES[3], new FishType(5, 2, atlas.getRegions().get(3), 20, 0.05f, 10));
		}
		
		loadSound(SOUND_TRASH);
		loadSound(SOUND_DASH);
		loadSound(SOUND_SPLASH);
		loadSound(SOUND_IMPACT);
		loadSound(SOUND_PAUSE_IN);
		loadSound(SOUND_PAUSE_OUT);
		loadSound(SOUND_LEVEL_COMPLETE);
		
		loadTilemap(TILEMAP_LEVEL0);
		loadTilemap(TILEMAP_LEVEL1);
		loadTilemap(TILEMAP_LEVEL2);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.ogg"));
		font = new PixelFont(textures.get(TEXTURE_UI), "other/glyphs.json");
	}
	
	public Shader getShader(String name)
	{
		return shaders.get(name);
	}
	
	public TextureRegion getSprite(String name)
	{
		return sprites.get(name);
	}
	
	public TextureAtlas getSpriteAtlas(String name)
	{
		return spritesAtlas.get(name);
	}
	
	public Material getMaterial(String name)
	{
		return materials.get(name);
	}
	
	public FishType getFishType(String name)
	{
		return fishTypes.get(name);
	}
	
	public Sound getSound(String name)
	{
		return sounds.get(name);
	}
	
	public Tilemap getTilemap(String name)
	{
		return tilemaps.get(name);
	}
	
	public Music getMusic()
	{
		return music;
	}
	
	public PixelFont getFont()
	{
		return font;
	}
	
	private void loadTexture(String path, TextureFilter filter)
	{
		Texture texture = new Texture(Gdx.files.internal(path));
		texture.setFilter(filter, filter);
		//texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		textures.put(path, texture);
	}
	
	private void loadShader(String path, boolean blend)
	{
		//ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(
			Gdx.files.internal(path + "Vert.glsl").readString(),
			Gdx.files.internal(path + "Frag.glsl").readString());
		if (!shader.isCompiled())
			throw new IllegalArgumentException("Error compiling shader " + path + ": " + shader.getLog());
		shaders.put(path, new Shader(shader, blend));
	}
	
	private void loadSprite(String key, String path, int x, int y, int width, int height)
	{
		sprites.put(key, new TextureRegion(textures.get(path), x, y, width, height));
	}
	
	private void loadSpriteAtlas(String key, TextureAtlas atlas)
	{
		spritesAtlas.put(key, atlas);
	}
	
	private void loadMaterial(String key, String path)
	{
		materials.put(key,  new Material(getShader(path)));
	}
	
	private void loadFishType(String key, FishType fishType)
	{
		fishTypes.put(key, fishType);
	}
	
	private void loadSound(String path)
	{
		sounds.put(path, Gdx.audio.newSound(Gdx.files.internal(path)));
	}
	
	private void loadTilemap(String path)
	{
		tilemaps.put(path, Tilemap.load(path));
	}
	
	public void dispose()
	{
		for (Texture texture : textures.values())
			texture.dispose();
		for (Shader shader : shaders.values())
			shader.dispose();
		for (Sound sound : sounds.values())
			sound.dispose();
		music.dispose();
	}
}
