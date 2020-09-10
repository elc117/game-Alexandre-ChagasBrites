package com.alegz.mermaid;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public class QuadTree<T>
{
	private ArrayList<T> objects;
	private ArrayList<Vector2> coords;
	private Rectangle rect;
	private float minSize;
	
	private boolean divided;
	private boolean canDivide;
	private QuadTree<T> sw;
	private QuadTree<T> se;
	private QuadTree<T> nw;
	private QuadTree<T> ne;
	
	public QuadTree(Rectangle rect, float minSize)
	{
		objects = new ArrayList<>();
		coords = new ArrayList<>();
		this.rect = rect;
		this.minSize = minSize;
		
		divided = false;
		canDivide = rect.width * 0.5f > minSize && rect.height * 0.5f > minSize;
		sw = se = nw = ne = null;
	}
	
	public void put(T object, float x, float y)
	{
		if (!divided)
		{
			objects.add(object);
			coords.add(new Vector2(x, y));
			
			if (canDivide && objects.size() >= 4)
				subDivide();
		}
		else
			getRegion(x, y).put(object, x, y);
	}
	
	private void subDivide()
	{
		sw = new QuadTree<T>(new Rectangle(rect.x					, rect.y					 , rect.width * 0.5f, rect.height * 0.5f), minSize);
		se = new QuadTree<T>(new Rectangle(rect.x + rect.width * 0.5f, rect.y					 , rect.width * 0.5f, rect.height * 0.5f), minSize);
		nw = new QuadTree<T>(new Rectangle(rect.x					, rect.y + rect.height * 0.5f, rect.width * 0.5f, rect.height * 0.5f), minSize);
		ne = new QuadTree<T>(new Rectangle(rect.x + rect.width * 0.5f, rect.y + rect.height * 0.5f, rect.width * 0.5f, rect.height * 0.5f), minSize);
		
		for (int i = 0; i < objects.size(); i++)
		{
			T object = objects.get(i);
			Vector2 coord = coords.get(i);
			QuadTree<T> tree = getRegion(coord.x, coord.y);
			tree.put(object, coord.x, coord.y);
		}
		objects.clear();
		coords.clear();
		divided = true;
	}
	
	public ArrayList<T> get(float x, float y)
	{
		if (!divided)
			return objects;
		return getRegion(x, y).get(x, y);
	}
	
	public void get(Rectangle rect, ArrayList<T> list)
	{
		if (!this.rect.overlaps(rect))
			return;
		
		if (getInternal(rect, list))
			return;
		
		sw.get(rect, list);
		se.get(rect, list);
		nw.get(rect, list);
		ne.get(rect, list);
	}
	
	public void get(Circle circle, ArrayList<T> list)
	{
		if (!overlapCircle(circle))
			return;
		
		if (getInternal(circle, list))
			return;
		
		sw.get(circle, list);
		se.get(circle, list);
		nw.get(circle, list);
		ne.get(circle, list);
	}
	
	private boolean getInternal(Shape2D shape, ArrayList<T> list)
	{
		if (divided)
			return false;
		
		for (int i = 0; i < objects.size(); i++)
		{
			T object = objects.get(i);
			Vector2 coord = coords.get(i);
			if (shape.contains(coord))
				list.add(object);
		}
		return true;
	}
	
	private boolean overlapCircle(Circle circle)
	{
		float closestX = MathUtils.clamp(circle.x, rect.x, rect.x + rect.width);
		float closestY = MathUtils.clamp(circle.y, rect.y, rect.y + rect.height);

		float distanceX = circle.x - closestX;
		float distanceY = circle.y - closestY;

		float distanceSquared = distanceX * distanceX + distanceY * distanceY;
		return distanceSquared < circle.radius * circle.radius;
	}
	
	private QuadTree<T> getRegion(float x, float y)
	{
		x -= rect.x + rect.width * 0.5f;
		y -= rect.y + rect.height * 0.5f;
		
		if (y < 0)
		{
			if (x < 0)
				return sw;
			return se;
		}
		if (x < 0)
			return nw;
		return ne;
	}
	
	public void clear()
	{
		objects.clear();
		coords.clear();
		if (divided)
		{
			sw.clear();
			se.clear();
			nw.clear();
			ne.clear();
			sw = se = nw = ne = null;
		}
		divided = false;
	}
}
