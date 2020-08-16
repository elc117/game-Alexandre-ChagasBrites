package com.alegz.mermaid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FishType 
{
	public float speed;
	public float dist;
	public TextureRegion sprite;
	
	public float separationForce;
	public float alignmentDamp;
	public float cohesionForce;
	
	public FishType(float speed, float dist, TextureRegion sprite, float separationForce, float alignmentDamp, float cohesionForce)
	{
		this.speed = speed;
		this.dist = dist;
		this.sprite = sprite;
		this.separationForce = separationForce;
		this.alignmentDamp = alignmentDamp;
		this.cohesionForce = cohesionForce;
	}
}
