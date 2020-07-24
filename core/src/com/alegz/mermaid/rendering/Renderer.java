package com.alegz.mermaid.rendering;

import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.material.Material;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class Renderer 
{
	private MeshCreator mesh;
	private int activeSprites;
	private final int maxSprites;
	
	private Matrix4 modelMatrix;
	private Matrix4 projMatrix;
	
	private ShaderProgram shader;
	private Texture texture;
	private Material material;
	
	public Renderer(int spriteCount)
	{
		mesh = new MeshCreator(spriteCount * 4, spriteCount * 6);
		activeSprites = 0;
		maxSprites = spriteCount;
		
		modelMatrix = new Matrix4();
		projMatrix = new Matrix4();
	}
	
	public void begin()
	{
		mesh.begin();
		activeSprites = 0;
		shader = null;
		texture = null;
		material = null;
	}
	
	public void flush(boolean begin)
	{
		if (shader == null || activeSprites < 1)
			return;
		
		shader.setUniformMatrix("u_projTrans", projMatrix);
		shader.setUniformf("u_color", Color.WHITE);
		material.setAttributes();
		
		mesh.end();
		mesh.render(shader);
		
		if (begin)
		{
			mesh.begin();
			activeSprites = 0;
		}
	}
	
	public void end()
	{
		flush(false);
		if (shader != null)
			shader.end();
	}
	
	public void drawSprite(TransformComponent transform, SpriteRendererComponent spriteRenderer)
	{
		if (spriteRenderer.material == null || spriteRenderer.sprite == null)
			return;
		setMaterial(spriteRenderer.material);
		if (spriteRenderer.sprite.texture != null)
			setTexture(spriteRenderer.sprite.texture);
		
		modelMatrix.idt();
		modelMatrix.translate(transform.position.x, transform.position.y, 1+spriteRenderer.depth);
		modelMatrix.rotate(0.0f, 0.0f, 1.0f, transform.rotation);
		modelMatrix.scale(transform.scale.x * (1 + spriteRenderer.depth), transform.scale.y * (1 + spriteRenderer.depth), 1);
		modelMatrix.translate(0.5f - spriteRenderer.pivot.x, 0.5f - spriteRenderer.pivot.y, 0);
		mesh.addSprite(modelMatrix, spriteRenderer.sprite.rect);
		
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void drawTilemap(TransformComponent transform, TilemapRendererComponent tilemapRenderer)
	{
		setMaterial(tilemapRenderer.material);
		setTexture(tilemapRenderer.spriteAtlas.texture);
		tilemapRenderer.mesh.render(shader);
	}
	
	public void setProjectionMatrix(Matrix4 projMatrix)
	{
		flush(true);
		this.projMatrix = projMatrix.cpy();
	}
	
	public void setMaterial(Material material)
	{
		if (this.material != material)
		{
			flush(true);
			if (material != null)
				setShader(material.getShader());
			this.material = material;
		}
	}
	
	public void setShader(ShaderProgram shader)
	{
		if (this.shader != shader)
		{
			flush(true);
			if (this.shader != null)
				this.shader.end();
				
			if (shader != null)
				shader.begin();
			this.shader = shader;
		}
	}
	
	private void setTexture(Texture texture)
	{
		if (this.texture != texture)
		{
			flush(true);
			if (texture != null)
			{
				shader.setUniformi("u_texture", 0);
				texture.bind(0);
			}
			this.texture = texture;
		}
	}
	
	public void dispose()
	{
		mesh.dispose();
	}
}
