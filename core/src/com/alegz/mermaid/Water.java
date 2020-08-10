package com.alegz.mermaid;

import com.alegz.mermaid.rendering.material.Material;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Water 
{
	private float[] heights;
	private float[] velocities;
	private float[] leftDeltas;
	private float[] rightDeltas;
	private int length;
	
	private final float k = 5.0f;
	private final float damp = 2.5f;
	private final float spread = 40.0f;
	
	private Material waterMaterial;
	private int width;
	
	private Pixmap pixmap;
	private Texture texture;
	
	public Water(Material waterMaterial, int width)
	{
		length = width + 1;
		heights = new float[length];
		velocities = new float[length];
		leftDeltas = new float[length];
		rightDeltas = new float[length];
		
		this.waterMaterial = waterMaterial;
		this.width = width;
		
		pixmap = new Pixmap(length, 1, Format.RGBA8888);
		texture = new Texture(pixmap);
	}
	
	public void update(float deltaTime)
	{
		for (int i = 0; i < length; i++)
		{
		    float accel = -k * heights[i];
		    velocities[i] += accel * deltaTime;
		    heights[i] = MathUtils.clamp(heights[i] + velocities[i] * deltaTime, -1, 1);
		    velocities[i] /= 1.0f + damp * deltaTime;
		}
		
	    for (int i = 0; i < length; i++)
	    {
	        if (i > 0)
	        {
	            leftDeltas[i] = spread * (heights[i] - heights[i - 1]);
	            velocities[i - 1] += leftDeltas[i] * deltaTime;
	        }
	        if (i < length - 1)
	        {
	            rightDeltas[i] = spread * (heights[i] - heights[i + 1]);
	            velocities[i + 1] += rightDeltas[i] * deltaTime;
	        }
	    }
	    
	    pixmap.setBlending(Blending.None);
	    Color color = new Color(0, 0, 0, 1);
	    for (int i = 0; i < length; i++)
	    {
	    	color.r = heights[i] * 0.5f + 0.5f;
	    	pixmap.setColor(color);
	    	pixmap.drawPixel(i, 0);
	    }
	    texture.draw(pixmap, 0, 0);
	    
		waterMaterial.setTexture("u_texture", texture, 0);
		waterMaterial.setFloat("u_width", width);
		waterMaterial.setFloat("u_waterHeight", 1.0f);
		waterMaterial.setFloat("u_totalHeight", 32.0f + 1.0f);
	}
	
	public void splash(float x, float speed)
	{
		int index = MathUtils.round((x / width + 0.5f) * width);
		if (index >= 0 && index < length)
		{
			heights[index] = speed;
			velocities[index] = -speed;
			SoundManager.play(Assets.SOUND_SPLASH, MathUtils.clamp(Math.abs(speed), 0, 1));
		}
	}
}
