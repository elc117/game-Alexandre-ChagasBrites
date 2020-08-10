package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.badlogic.gdx.math.Vector2;

public class TextRendererComponent extends RendererComponent
{
	public String text = "";
	public float fontSize = 1.0f;
	public Vector2 offset = new Vector2(0.0f, 0.0f);
	
	public Class<? extends Component> getComponentClass()
	{
		return TextRendererComponent.class;
	}
}
