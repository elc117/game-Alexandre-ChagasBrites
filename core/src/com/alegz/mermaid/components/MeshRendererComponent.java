package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.badlogic.gdx.graphics.Mesh;

public class MeshRendererComponent extends RendererComponent
{
	public Mesh mesh = null;
	
	public Class<? extends Component> getComponentClass()
	{
		return MeshRendererComponent.class;
	}
}
