package com.alegz.mermaid;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class Tilemap 
{
	private byte[] map;
	private int width, height;
	private Vector2 offset;
	private int trashCount;
	private int fishCount;
	
	public Tilemap()
	{
		map = null;
		width = height = 0;
		offset = new Vector2();
	}
	
	public Tilemap(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		map = new byte[width * height];
		for (int i = 0; i < width * height; i++)
			map[i] = 0;
		
		for (int i = 0; i < width; i++)
			map[i + (height - 1) * width] = 1;
		for (int i = 0; i < height; i++)
			map[i * width] = map[width - 1 + i * width] = 1;
		
		offset = new Vector2(width * 0.5f, 2.0f);
		trashCount = 0;
	}
	
	public byte getTile(int x, int y)
	{
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		return map[x + y * width];
	}
	
	public void setTile(Vector2 pos, byte value)
	{
		int x = MathUtils.floor(pos.x + offset.x);
		int y = MathUtils.floor(offset.y - pos.y);
		if (x < 1 || x >= width - 1 || y < 0 || y >= height - 1)
			return;
		map[x + y * width] = value;
	}

	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getTrashCount()
	{
		return trashCount;
	}
	
	public int getFishCount()
	{
		return fishCount;
	}
	
	public Vector2 getWorldPos(int x, int y)
	{
		return new Vector2(x - offset.x, offset.y - y);
	}
	
	public Vector2[][] getVertices()
	{
		class Line
		{
			int start, end, dir;
			public Line(int s, int e, int d)
			{
				start = s; end = e; dir = d;
			}
		}
		
		ArrayList<Line> lines = new ArrayList<Line>();
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (map[x + y * width] == 0)
					continue;
				
				int off = width + 1;
				int current = x + y * off;
				if (y == 0 || map[x + (y - 1) * width] == 0)
					lines.add(new Line(current, current + 1, 0));
				if (x == width - 1 || map[x + 1 + y * width] == 0)
					lines.add(new Line(current + 1, current + 1 + off, 1));
				if (y == height - 1 || map[x + (y + 1) * width] == 0)
					lines.add(new Line(current + 1 + off, current + off, 2));
				if (x == 0 || map[x - 1 + y * width] == 0)
					lines.add(new Line(current + off, current, 3));
			}
		}
		
		for (int i = 0; i < lines.size(); i++)
		{
			Line line = lines.get(i);
			Line other = null;
			for (int j = 0; j < lines.size(); j++)
			{
				Line test = lines.get(j);
				if (test == line)
					continue;
				if (test.start == line.end && test.dir == line.dir)
				{
					other = test;
					break;
				}
			}
			if (other != null)
			{	
				line.end = other.end;
				lines.remove(other);
				i--;
			}
		}
		
		ArrayList<ArrayList<Vector2>> layers = new ArrayList<>();
		ArrayList<Vector2> vertices = null;
		Line currentLine = null;
		while (lines.size() > 0)
		{
			if (currentLine == null)
			{
				vertices = new ArrayList<>();
				layers.add(vertices);
				currentLine = lines.get(0);
			}
			lines.remove(currentLine);
			
			int x = currentLine.start % (width + 1);
			int y = currentLine.start / (width + 1);
			vertices.add(getWorldPos(x, y));
			
			if (lines.size() == 0)
				break;
			
			int end = currentLine.end;
			currentLine = null;
			for (int i = 0; i < lines.size(); i++)
			{
				if (lines.get(i) == currentLine)
					continue;
				if (lines.get(i).start == end)
				{
					currentLine = lines.get(i);
					break;
				}
			}
		}
		
		vertices = new ArrayList<>();
		vertices.add(getWorldPos(0, -height));
		vertices.add(getWorldPos(0, height));
		vertices.add(getWorldPos(width, height));
		vertices.add(getWorldPos(width, -height));
		layers.add(vertices);
		
		Vector2[][] tileLayers = new Vector2[layers.size()][];
		for (int i = 0; i < layers.size(); i++)
		{	
			tileLayers[i] = new Vector2[layers.get(i).size()];
			layers.get(i).toArray(tileLayers[i]);
		}
		return tileLayers;
	}
	
	public static Tilemap load(String path)
	{
		Json json = new Json();
		return json.fromJson(Tilemap.class, Gdx.files.internal(path));
	}
	
	public void save(String path)
	{
		Json json = new Json();
		json.toJson(this, Tilemap.class, Gdx.files.local(path));
	}
}
