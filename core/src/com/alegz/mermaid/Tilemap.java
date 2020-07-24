package com.alegz.mermaid;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Tilemap 
{
	private byte[] map;
	private int width, height;
	private Vector2 offset;
	
	public Tilemap(String path)
	{
		FileHandle file = Gdx.files.internal(path);
		//System.out.println(file.exists());
		
		byte[] info = new byte[(int)file.length()];
		file.readBytes(info, 0, (int)file.length());
		
		width = (info[0] << 0) | (info[1] << 8) | (info[2] << 16) | (info[3] << 25);
		height = (info[4] << 0) | (info[5] << 8) | (info[6] << 16) | (info[7] << 25);
		
		map = new byte[width * height];
		for (int i = 0; i < width * height; i++)
			map[i] = info[8 + i];
		
		offset = new Vector2(width * 0.5f, 2.0f);
	}
	
	public Tilemap(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		map = new byte[width * height];
		for (int i = 0; i < width * height; i++)
			map[i] = 0;
		
		offset = new Vector2(width * 0.5f, 2.0f);
		
		for (int i = 0; i < width; i++)
			map[i + (height - 1) * width] = 1;
		for (int i = 0; i < height; i++)
			map[i * width] = map[width - 1 + i * width] = 1;
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
	
	public Vector2 getWorldPos(int x, int y)
	{
		return new Vector2(x - offset.x, offset.y - y);
	}
	
	public void save(String path)
	{
		FileHandle file = Gdx.files.local(path);
		System.out.println(file.path());
		byte[] info = new byte[] {
			(byte)(width >> 0), (byte)(width >> 8), (byte)(width >> 16), (byte)(width >> 24),
			(byte)(height >> 0), (byte)(height >> 8), (byte)(height >> 16), (byte)(height >> 24)
		};
		file.writeBytes(info, false);
		file.writeBytes(map, true);
	}
	
	public Vector2[] getVertices()
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
		
		ArrayList<Vector2> vertices = new ArrayList<Vector2>();
		Line currentLine = null;
		int teste = 0;
		while (lines.size() > 0 && teste < 1000)
		{
			if (currentLine == null)
				currentLine = lines.get(0);
			lines.remove(currentLine);
			
			int x = currentLine.start % (width + 1);
			int y = currentLine.start / (width + 1);
			vertices.add(getWorldPos(x, y));
			
			//System.out.println(currentLine.start + " - " + currentLine.end + " : " + x + ", " + y);
			
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
			teste++;
		}
		
		Vector2[] tileVertices = new Vector2[vertices.size()];
		vertices.toArray(tileVertices);
		return tileVertices;
	}
}
