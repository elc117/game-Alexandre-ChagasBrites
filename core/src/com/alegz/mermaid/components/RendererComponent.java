package com.alegz.mermaid.components;

import com.alegz.mermaid.rendering.material.Material;
import com.alegz.mermaid.ecs.Component;

public abstract class RendererComponent implements Component
{
	public Material material = null;
	public int layer = 0;
	
	public Class<? extends Component> getComponentClass()
	{
		return RendererComponent.class;
	}
}
