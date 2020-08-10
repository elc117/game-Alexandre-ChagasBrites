package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ImageRendererComponent extends RendererComponent
{
	public TextureRegion sprite = null;
	public Vector2 pivot = new Vector2(0.5f, 0.5f);
	
	public Class<? extends Component> getComponentClass()
	{
		return ImageRendererComponent.class;
	}
}
