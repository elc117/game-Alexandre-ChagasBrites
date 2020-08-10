package com.alegz.mermaid.rendering;

import com.alegz.mermaid.PixelFont;
import com.alegz.mermaid.PixelFontGlyph;
import com.alegz.mermaid.components.ImageRendererComponent;
import com.alegz.mermaid.components.MeshRendererComponent;
import com.alegz.mermaid.components.SpriteRendererComponent;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.TilemapRendererComponent;
import com.alegz.mermaid.components.TransformComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.rendering.material.Material;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

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
	
	public void drawSprite(TransformComponent transform, SpriteRendererComponent spriteRenderer)
	{
		if (spriteRenderer.material == null)
			return;
		setMaterial(spriteRenderer.material);
		
		modelMatrix.idt();
		modelMatrix.translate(transform.position.x, transform.position.y, spriteRenderer.depth);
		modelMatrix.rotate(0.0f, 0.0f, 1.0f, transform.rotation);
		modelMatrix.scale(transform.scale.x, transform.scale.y, 1);
		modelMatrix.translate(0.5f - spriteRenderer.pivot.x, 0.5f - spriteRenderer.pivot.y, 0);
		if (spriteRenderer.sprite != null)
		{
			setTexture(spriteRenderer.sprite.getTexture());
			mesh.addSprite(modelMatrix, spriteRenderer.sprite);
		}
		else
			mesh.addSprite(modelMatrix, 0, 0, 1, 1);
		
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void drawMesh(TransformComponent transform, MeshRendererComponent meshRenderer)
	{
		setMaterial(meshRenderer.material);
		meshRenderer.mesh.render(activeShader.getProgram(), GL20.GL_TRIANGLES);
	}
	
	public void drawTilemap(TransformComponent transform, TilemapRendererComponent tilemapRenderer)
	{
		setMaterial(tilemapRenderer.material);
		setTexture(tilemapRenderer.spriteAtlas.getRegions().get(0).getTexture());
		tilemapRenderer.mesh.render(activeShader.getProgram());
	}
	
	public void drawImage(PlatformerCamera camera, UITransformComponent transform, ImageRendererComponent imageRenderer)
	{
		if (imageRenderer.material == null)
			return;
		setMaterial(imageRenderer.material);
		
		modelMatrix.idt();
		modelMatrix.translate(transform.position.x + transform.anchor.x * camera.getUIWidth(), 
							  transform.position.y + transform.anchor.y * camera.getUIHeight(), 0);
		modelMatrix.scale(transform.scale.x, transform.scale.y, 1);
		modelMatrix.translate(0.5f - imageRenderer.pivot.x, 0.5f - imageRenderer.pivot.y, 0);
		if (imageRenderer.sprite != null)
		{
			setTexture(imageRenderer.sprite.getTexture());
			mesh.addSprite(modelMatrix, imageRenderer.sprite);
		}
		else
			mesh.addSprite(modelMatrix, 0, 0, 1, 1);
		
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void drawText(PlatformerCamera camera, PixelFont pixelFont, UITransformComponent transform, TextRendererComponent textRenderer)
	{
		if (textRenderer.material == null)
			return;
		setMaterial(textRenderer.material);
		setTexture(pixelFont.getTexture());
		
		Vector2 position = transform.position.cpy();
		position.add(transform.anchor.x * camera.getUIWidth() + textRenderer.offset.x, 
					 transform.anchor.y * camera.getUIHeight() + textRenderer.offset.y);
		TextureRegion sprite = new TextureRegion(pixelFont.getTexture());
		
		final Vector2 pivot = new Vector2(0.0f, 0.0f);
		final int pixelKerning = -1;
		for (Character character : textRenderer.text.toCharArray())
		{
			if (character == '\n')
			{
				position.x = transform.position.x + transform.anchor.x * camera.getUIWidth() + textRenderer.offset.x;
				position.y -= 9;
			}
			
			PixelFontGlyph glyph = pixelFont.getGlyph(character);
			if (glyph == null)
				continue;
			
			modelMatrix.idt();
			modelMatrix.translate(position.x, position.y + glyph.offset, 0);
			modelMatrix.scale(glyph.width * textRenderer.fontSize, glyph.height * textRenderer.fontSize, 1);
			modelMatrix.translate(0.5f - pivot.x, 0.5f - pivot.y, 0);
			
			sprite.setRegion(glyph.x, glyph.y, glyph.width, glyph.height);
			mesh.addSprite(modelMatrix, sprite);
			position.x += (glyph.width + pixelKerning) * textRenderer.fontSize;
			
			activeSprites++;
			if (activeSprites == maxSprites)
				flush(true);
		}
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
			activeMaterial = material;
			if (activeMaterial != null)
			{
				setShader(material.getShader());
				activeMaterial.setMatrix("u_projTrans", projMatrix);
				activeMaterial.setAttributes();
			}
		}
	}
	
	public void setShader(Shader shader)
	{
		if (activeShader != shader)
		{
			flush(true);
			if (activeShader != null)
				activeShader.end();
				
			activeShader = shader;
			if (activeShader != null)
				activeShader.begin();
		}
	}
	
	private void setTexture(Texture texture)
	{
		if (activeTexture != texture)
		{
			flush(true);
			activeTexture = texture;
			if (texture != null)
			{
				activeShader.getProgram().setUniformi("u_texture", 0);
				activeTexture.bind(0);
			}
		}
	}
	
	public void dispose()
	{
		mesh.dispose();
	}
}
