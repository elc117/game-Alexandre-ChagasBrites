package com.alegz.mermaid;

import com.alegz.mermaid.rendering.material.Material;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Water 
{
	private float[] heights;
	private float[] velocities;
	private float[] leftDeltas;
	private float[] rightDeltas;
	
	private final float k = 5.0f;
	private final float damp = 2.5f;
	private final float spread = 40.0f;
	
	private Material waterMaterial;
	private int length;
	
	public Water(Material waterMaterial, int length)
	{
		heights = new float[length];
		velocities = new float[length];
		leftDeltas = new float[length];
		rightDeltas = new float[length];
		
		this.waterMaterial = waterMaterial;
		this.length = length;
	}
	
	public void update(float deltaTime)
	{
		for (int i = 0; i < length; i++)
		{
		    float accel = -k * heights[i];
		    velocities[i] += accel * deltaTime;
		    heights[i] += velocities[i] * deltaTime;
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
		
		waterMaterial.setFloatArray("u_heights", heights, length);
		waterMaterial.setFloat("u_width", length);
		waterMaterial.setFloat("u_waterHeight", 1.0f);
		waterMaterial.setFloat("u_totalHeight", 32.0f + 1.0f);
	}
	
	public void splash(float x, float speed)
	{
		int index = MathUtils.round((x / length + 0.5f) * length);
		if (index >= 0 && index < length)
		{
			heights[index] = speed;
			velocities[index] = -speed;
		}
	}
}
