package com.alegz.mermaid.components;

import com.alegz.mermaid.rendering.PlatformerCamera;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class CameraComponent implements Component
{
	public PlatformerCamera camera;
	public TransformComponent target;
	public Vector2 minBounds;
	public Vector2 maxBounds;
	
	public CameraComponent(PlatformerCamera camera)
	{
		this.camera = camera;
	}
}
