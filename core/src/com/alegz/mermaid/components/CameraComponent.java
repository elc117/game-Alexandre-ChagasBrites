package com.alegz.mermaid.components;

import com.alegz.ecs.Component;
import com.alegz.mermaid.rendering.PlatformerCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraComponent extends Component
{
	public PlatformerCamera camera;
	public TransformComponent target;
	public PlayerComponent targetPlayer;
	public Vector2 minBounds;
	public Vector2 maxBounds;
	
	public CameraComponent(PlatformerCamera camera)
	{
		this.camera = camera;
	}
}
