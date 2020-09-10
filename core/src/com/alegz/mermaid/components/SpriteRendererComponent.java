package com.alegz.mermaid.components;

import com.alegz.mermaid.rendering.Renderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class SpriteRendererComponent extends RendererComponent
{
	public TextureRegion sprite = null;
	public Vector2 pivot = new Vector2(0.5f, 0.5f);
	public float depth = 0;
	
	@Override
	public void draw(Renderer renderer, TransformComponent transform)
	{
		if (material == null)
			return;
		renderer.setMaterial(material);
		
		Matrix4 modelMatrix = renderer.getModelMatrix();
		modelMatrix.idt();
		modelMatrix.translate(transform.position.x, transform.position.y, depth);
		modelMatrix.rotate(0.0f, 0.0f, 1.0f, transform.rotation);
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
	
	@Override
	public void draw(Renderer renderer, UITransformComponent transform) 
	{
		
	}
}
