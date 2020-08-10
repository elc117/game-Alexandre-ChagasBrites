package com.alegz.mermaid.components;

import com.alegz.mermaid.ecs.Component;
import com.badlogic.gdx.math.Vector2;

public class UITransformComponent extends TransformComponent
{
	public Vector2 anchor = new Vector2(0.5f, 0.5f);
	
	public Class<? extends Component> getComponentClass()
	{
		return UITransformComponent.class;
	}
}
