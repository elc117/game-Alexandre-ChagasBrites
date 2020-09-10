package com.alegz.mermaid.components;

import com.alegz.ecs.Component;
import com.badlogic.gdx.math.Vector2;

public class UITransformComponent extends Component
{
	public Vector2 position = new Vector2(0, 0);
	public Vector2 scale = new Vector2(1, 1);
	public Vector2 anchor = new Vector2(0.5f, 0.5f);
	public Vector2 uiPosition = new Vector2(0, 0);
	public boolean dirty = false;
	
	public void setPosition(Vector2 position)
	{
		this.position = position.cpy();
		dirty = true;
	}
}
