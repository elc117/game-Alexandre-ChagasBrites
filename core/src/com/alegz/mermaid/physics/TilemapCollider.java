package com.alegz.mermaid.physics;

import java.util.ArrayList;

import com.alegz.mermaid.Tilemap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class TilemapCollider extends Collider 
{
	private Tilemap tilemap;
	
	public TilemapCollider(Tilemap tilemap, short categoryBits, short maskBits)
	{
		super(categoryBits, maskBits);
		this.tilemap = tilemap;
	}
	
	protected Shape[] getShapes()
	{
		ArrayList<Shape> shapes = new ArrayList<>();
		Vector2[][] tileLayers = tilemap.getVertices();
		for (Vector2[] tileVertices : tileLayers)
		{
			ChainShape shape = new ChainShape();
		    shape.createLoop(tileVertices);
		    shapes.add(shape);
		}
		Shape[] shapeArray = new Shape[shapes.size()];
		shapes.toArray(shapeArray);
	    return shapeArray;
	}
}
