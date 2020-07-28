package com.alegz.mermaid.rendering;

import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.rendering.material.Material;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class Renderer 
{
	private MeshCreator mesh;
	private int activeSprites;
	private final int maxSprites;
	
	private Shader activeShader;
	private Texture activeTexture;
	private Material activeMaterial;
	
	private Matrix4 modelMatrix;
	private Matrix4 projMatrix;
	
	public Renderer(int spriteCount)
	{
		mesh = new MeshCreator(spriteCount * 4, spriteCount * 6);
		maxSprites = spriteCount;
		
		modelMatrix = new Matrix4();
		projMatrix = new Matrix4();
	}
	
	public void begin()
	{
		mesh.begin();
		activeSprites = 0;
		activeShader = null;
		activeTexture = null;
		activeMaterial = null;
	}
	
	public void flush(boolean begin)
	{
		if (activeShader == null || activeSprites < 1)
			return;
		
		updateRenderer();
		mesh.end();
		mesh.render(activeShader.getProgram());
		
		if (begin)
		{
			mesh.begin();
			activeSprites = 0;
		}
	}
	
	public void end()
	{
		flush(false);
		if (activeShader != null)
			activeShader.end();
	}
	
	private void updateRenderer()
	{
		ShaderProgram program = activeShader.getProgram();
		program.setUniformMatrix("u_projTrans", projMatrix);
		program.setUniformf("u_color", Color.WHITE);
		activeMaterial.setAttributes();
	}
	
	public void drawSprite(TransformComponent transform, SpriteRendererComponent spriteRenderer)
	{
		if (spriteRenderer.material == null || spriteRenderer.sprite == null)
			return;
		setMaterial(spriteRenderer.material);
		if (spriteRenderer.sprite.getTexture() != null)
			setTexture(spriteRenderer.sprite.getTexture());
		
		modelMatrix.idt();
		modelMatrix.translate(transform.position.x, transform.position.y, spriteRenderer.depth);
		modelMatrix.rotate(0.0f, 0.0f, 1.0f, transform.rotation);
		modelMatrix.scale(transform.scale.x * (1 + spriteRenderer.depth), transform.scale.y * (1 + spriteRenderer.depth), 1);
		modelMatrix.translate(0.5f - spriteRenderer.pivot.x, 0.5f - spriteRenderer.pivot.y, 0);
		mesh.addSprite(modelMatrix, spriteRenderer.sprite.getRect());
		
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void drawMesh(TransformComponent transform, MeshRendererComponent meshRenderer)
	{
		setMaterial(meshRenderer.material);
		updateRenderer();
		meshRenderer.mesh.render(activeShader.getProgram(), GL20.GL_TRIANGLES);
	}
	
	public void drawTilemap(TransformComponent transform, TilemapRendererComponent tilemapRenderer)
	{
		setMaterial(tilemapRenderer.material);
		setTexture(tilemapRenderer.spriteAtlas.getTexture());
		updateRenderer();
		tilemapRenderer.mesh.render(activeShader.getProgram());
	}
	
	public void setProjectionMatrix(Matrix4 projMatrix)
	{
		flush(true);
		this.projMatrix = projMatrix.cpy();
	}
	
	public void setMaterial(Material material)
	{
		if (activeMaterial != material)
		{
			flush(true);
			if (material != null)
				setShader(material.getShader());
			activeMaterial = material;
		}
	}
	
	public void setShader(Shader shader)
	{
		if (activeShader != shader)
		{
			flush(true);
			if (activeShader != null)
				activeShader.end();
				
			if (shader != null)
				shader.begin();
			activeShader = shader;
		}
	}
	
	private void setTexture(Texture texture)
	{
		if (activeTexture != texture)
		{
			flush(true);
			if (texture != null)
			{
				activeShader.getProgram().setUniformi("u_texture", 0);
				texture.bind(0);
			}
			activeTexture = texture;
		}
	}
	
	public void dispose()
	{
		mesh.dispose();
	}
}
