package com.alegz.mermaid.components;

import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.ecs.Component;
import com.alegz.mermaid.rendering.MeshCreator;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class TilemapRendererComponent extends RendererComponent
{
	public MeshCreator mesh;
	public TextureAtlas spriteAtlas;
	private Tilemap tilemap;
	
	public TilemapRendererComponent(Tilemap tilemap, TextureAtlas spriteAtlas)
	{
		this.tilemap = tilemap;
		this.spriteAtlas = spriteAtlas;
		createMesh();
	}
	
	public void createMesh()
	{
		int spriteCount = 0;
		for (int y = 0; y < tilemap.getHeight(); y++)
		{
			for (int x = 0; x < tilemap.getWidth(); x++)
			{
				switch(tilemap.getTile(x, y))
				{
				case 1:
					spriteCount++;
					break;
				}
			}
		}
		
		if (mesh != null)
			mesh.dispose();
		mesh = new MeshCreator(spriteCount * 4, spriteCount * 6);
		mesh.begin();
		
		Vector2 pivot = new Vector2(0.0f, 1.0f);
		Matrix4 modelMatrix = new Matrix4();
		
		for (int y = 0; y < tilemap.getHeight(); y++)
		{
			for (int x = 0; x < tilemap.getWidth(); x++)
			{
				Vector2 position = tilemap.getWorldPos(x, y);
				modelMatrix.idt();
				modelMatrix.translate(position.x, position.y, 0);
				modelMatrix.translate(0.5f - pivot.x, 0.5f - pivot.y, 0);
				
				TextureRegion region = spriteAtlas.findRegion("tile0");
				switch(tilemap.getTile(x, y))
				{
				case 1:
					region = spriteAtlas.findRegion("tile6");
					if (tilemap.getTile(x + 1, y) == 1 && 
						tilemap.getTile(x, y + 1) == 1 &&
						tilemap.getTile(x + 1, y + 1) != 1)
						region = spriteAtlas.findRegion("tile9");
					else if (tilemap.getTile(x + 1, y) == 1 && 
							 tilemap.getTile(x, y - 1) == 1 &&
							 tilemap.getTile(x + 1, y - 1) != 1)
						region = spriteAtlas.findRegion("tile4");
					else if (tilemap.getTile(x - 1, y) == 1 && 
							 tilemap.getTile(x, y + 1) == 1 &&
							 tilemap.getTile(x - 1, y + 1) != 1)
						region = spriteAtlas.findRegion("tile8");
					else if (tilemap.getTile(x - 1, y) == 1 && 
							 tilemap.getTile(x, y - 1) == 1 &&
							 tilemap.getTile(x - 1, y - 1) != 1)
						region = spriteAtlas.findRegion("tile3");
					else if (tilemap.getTile(x + 1, y) != 1 && x != tilemap.getWidth() - 1 && 
						tilemap.getTile(x, y + 1) != 1)
						region = spriteAtlas.findRegion("tile12");
					else if (tilemap.getTile(x + 1, y) != 1 && x != tilemap.getWidth() - 1 && 
							 tilemap.getTile(x, y - 1) != 1)
						region = spriteAtlas.findRegion("tile2");
					else if (tilemap.getTile(x + 1, y) != 1 && x != tilemap.getWidth() - 1)
						region = spriteAtlas.findRegion("tile7");
					else if (tilemap.getTile(x - 1, y) != 1 && x != 0 && 
							 tilemap.getTile(x, y + 1) != 1)
						region = spriteAtlas.findRegion("tile10");
					else if (tilemap.getTile(x - 1, y) != 1 && x != 0 &&
							 tilemap.getTile(x, y - 1) != 1)
						region = spriteAtlas.findRegion("tile0");
					else if (tilemap.getTile(x - 1, y) != 1 && x != 0)
						region = spriteAtlas.findRegion("tile5");
					else if (tilemap.getTile(x, y + 1) != 1 && y != tilemap.getHeight() - 1)
						region = spriteAtlas.findRegion("tile11");
					else if (tilemap.getTile(x, y - 1) != 1)
						region = spriteAtlas.findRegion("tile1");
					mesh.addSprite(modelMatrix, region);
					break;
				}
			}
		}
		mesh.end();
	}
}
