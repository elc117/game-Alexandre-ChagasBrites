package com.alegz.mermaid.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ButtonComponent extends ImageRendererComponent
{
	public interface Action
	{
		public void onClick();
	}
	
	public TextureRegion defaultSprite = null;
	public TextureRegion highlightSprite = null;
	public TextureRegion pressedSprite = null;
	
	public int state = 0;
	public Action action = null;
}
