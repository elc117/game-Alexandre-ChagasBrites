package com.alegz.mermaid.components;

import com.alegz.ecs.Component;
import com.alegz.mermaid.rendering.Renderer;
import com.alegz.mermaid.rendering.material.Material;

public abstract class RendererComponent extends Component
{
	public Material material = null;
	public int layer = 0;
	
	public abstract void draw(Renderer renderer, TransformComponent transform);
	public abstract void draw(Renderer renderer, UITransformComponent transform);
}
