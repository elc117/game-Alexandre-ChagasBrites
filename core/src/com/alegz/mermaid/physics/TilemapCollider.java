package com.alegz.mermaid.physics;

import com.alegz.mermaid.Tilemap;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class TilemapCollider extends Collider 
{
	private Tilemap tilemap;
	
	public TilemapCollider(Tilemap tilemap)
	{
		this.tilemap = tilemap;
	}
	
	protected Shape getShape()
	{
	    ChainShape shape = new ChainShape();
	    shape.createLoop(tilemap.getVertices());
	    return shape;
	}
}
