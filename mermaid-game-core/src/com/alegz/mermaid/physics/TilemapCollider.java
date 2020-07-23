package com.alegz.mermaid.physics;

import com.alegz.mermaid.Tilemap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class TilemapCollider implements Collider 
{
	private Tilemap tilemap;
	
	public TilemapCollider(Tilemap tilemap)
	{
		this.tilemap = tilemap;
	}
	
	public Fixture getFixture(Body body)
	{
	    ChainShape shape = new ChainShape();
	    shape.createLoop(tilemap.getVertices());

	    FixtureDef fixtureDef = new FixtureDef();
	    fixtureDef.shape = shape;
	    fixtureDef.density = 1f;

	    Fixture fixture = body.createFixture(fixtureDef);
	    shape.dispose();
	    
	    return fixture;
	}
}
