package com.alegz.mermaid.components;

import com.alegz.mermaid.rendering.Renderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class ImageRendererComponent extends RendererComponent
{
	public TextureRegion sprite = null;
	public Vector2 pivot = new Vector2(0.5f, 0.5f);
	
	@Override
	public void draw(Renderer renderer, TransformComponent transform) 
	{
		
	}
	
	@Override
	public void draw(Renderer renderer, UITransformComponent transform) 
	{
		if (material == null)
			return;
		renderer.setMaterial(material);
		
		Matrix4 modelMatrix = renderer.getModelMatrix();
		modelMatrix.idt();
		modelMatrix.translate(transform.uiPosition.x, transform.uiPosition.y, 0);
		modelMatrix.scale(transform.scale.x, transform.scale.y, 1);
		modelMatrix.translate(0.5f - pivot.x, 0.5f - pivot.y, 0);
		if (sprite != null)
		{
			renderer.setTexture(sprite.getTexture());
			renderer.draw(sprite);
		}
		else
			renderer.draw(0, 0, 1, 1);
	}
}
