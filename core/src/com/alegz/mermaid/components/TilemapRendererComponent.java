package com.alegz.mermaid.components;

import com.alegz.mermaid.Rect;
import com.alegz.mermaid.Tilemap;
import com.alegz.mermaid.rendering.MeshCreator;
import com.alegz.mermaid.rendering.SpriteAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class TilemapRendererComponent extends RendererComponent
{
	public MeshCreator mesh;
	public SpriteAtlas spriteAtlas;
	
	private Tilemap tilemap;
	
	public TilemapRendererComponent(Tilemap tilemap, SpriteAtlas spriteAtlas)
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
				
				Rect rect = spriteAtlas.getRect(0, 0);
				switch(tilemap.getTile(x, y))
				{
				case 1:
					rect = spriteAtlas.getRect(1, 1);
					if (tilemap.getTile(x + 1, y) == 1 && 
						tilemap.getTile(x, y + 1) == 1 &&
						tilemap.getTile(x + 1, y + 1) != 1)
						rect = spriteAtlas.getRect(4, 1);
					else if (tilemap.getTile(x + 1, y) == 1 && 
							 tilemap.getTile(x, y - 1) == 1 &&
							 tilemap.getTile(x + 1, y - 1) != 1)
						rect = spriteAtlas.getRect(4, 0);
					else if (tilemap.getTile(x - 1, y) == 1 && 
							 tilemap.getTile(x, y + 1) == 1 &&
							 tilemap.getTile(x - 1, y + 1) != 1)
						rect = spriteAtlas.getRect(3, 1);
					else if (tilemap.getTile(x - 1, y) == 1 && 
							 tilemap.getTile(x, y - 1) == 1 &&
							 tilemap.getTile(x - 1, y - 1) != 1)
						rect = spriteAtlas.getRect(3, 0);
					else if (tilemap.getTile(x + 1, y) != 1 && x != tilemap.getWidth() - 1 && 
						tilemap.getTile(x, y + 1) != 1)
						rect = spriteAtlas.getRect(2, 2);
					else if (tilemap.getTile(x + 1, y) != 1 && x != tilemap.getWidth() - 1 && 
							 tilemap.getTile(x, y - 1) != 1)
						rect = spriteAtlas.getRect(2, 0);
					else if (tilemap.getTile(x + 1, y) != 1 && x != tilemap.getWidth() - 1)
						rect = spriteAtlas.getRect(2, 1);
					else if (tilemap.getTile(x - 1, y) != 1 && x != 0 && 
							 tilemap.getTile(x, y + 1) != 1)
						rect = spriteAtlas.getRect(0, 2);
					else if (tilemap.getTile(x - 1, y) != 1 && x != 0 &&
							 tilemap.getTile(x, y - 1) != 1)
						rect = spriteAtlas.getRect(0, 0);
					else if (tilemap.getTile(x - 1, y) != 1 && x != 0)
						rect = spriteAtlas.getRect(0, 1);
					else if (tilemap.getTile(x, y + 1) != 1 && y != tilemap.getHeight() - 1)
						rect = spriteAtlas.getRect(1, 2);
					else if (tilemap.getTile(x, y - 1) != 1)
						rect = spriteAtlas.getRect(1, 0);
					mesh.addSprite(modelMatrix, rect);
					break;
				}
			}
		}
		mesh.end();
	}
}
