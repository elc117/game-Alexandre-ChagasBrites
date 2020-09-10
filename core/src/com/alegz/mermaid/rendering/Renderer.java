package com.alegz.mermaid.rendering;

import com.alegz.mermaid.PixelFont;
import com.alegz.mermaid.PixelFontGlyph;
import com.alegz.mermaid.components.TextRendererComponent;
import com.alegz.mermaid.components.UITransformComponent;
import com.alegz.mermaid.rendering.material.Material;
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
	}
	
	public void draw(TextureRegion sprite)
	{
		mesh.addSprite(modelMatrix, sprite);
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void draw(float u, float v, float u2, float v2)
	{
		mesh.addSprite(modelMatrix, u, v, u2, v2);
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void draw()
	{
		mesh.addSprite(modelMatrix, 0.0f, 0.0f, 1.0f, 1.0f);
		activeSprites++;
		if (activeSprites == maxSprites)
			flush(true);
	}
	
	public void draw(MeshCreator mesh)
	{
		mesh.render(activeShader.getProgram());
	}
	
	public void drawText(PlatformerCamera camera, PixelFont pixelFont, UITransformComponent transform, TextRendererComponent textRenderer)
	{
		if (textRenderer.material == null)
			return;
		setMaterial(textRenderer.material);
		setTexture(pixelFont.getTexture());
		
		Vector2 position = transform.uiPosition.cpy();
		position.add(textRenderer.offset.x, textRenderer.offset.y);
		TextureRegion sprite = new TextureRegion(pixelFont.getTexture());
		
		final Vector2 pivot = new Vector2(0.0f, 0.0f);
		final int pixelKerning = -1;
		for (Character character : textRenderer.text.toCharArray())
		{
			if (character == '\n')
			{
				position.x = transform.uiPosition.x + textRenderer.offset.x;
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
	
	private void setShader(Shader shader)
	{
		if (activeShader != shader)
		{
			flush(true);
			activeShader = shader;
			if (activeShader != null)
				activeShader.bind();
		}
	}
	
	public void setTexture(Texture texture)
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
	
	public Matrix4 getModelMatrix()
	{
		return modelMatrix;
	}
	
	public void dispose()
	{
		mesh.dispose();
	}
}
