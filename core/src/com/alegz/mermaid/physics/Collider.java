package com.alegz.mermaid.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public interface Collider 
{
	public Fixture getFixture(Body body);
}
