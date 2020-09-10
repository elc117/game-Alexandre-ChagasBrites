package com.alegz.mermaid.utils;

import com.badlogic.gdx.math.MathUtils;

public class GameUtils 
{
	public static float damp(float damping, float deltaTime)
	{
		return 1.0f - (float)Math.pow(damping, deltaTime);
	}
	
	public static float easeIn(float t)
	{
		return t * t;
	}
	
	public static float easeOut(float t)
	{
		return t * (2.0f - t);
	}
	
	public static float easeOutBounce(float t)
	{
		final float n1 = 7.5625f;
		final float d1 = 2.75f;
		if (t < 1.0f / d1)
		    return n1 * t * t;
		else if (t < 2.0f / d1)
			return n1 * (t -= 1.5f / d1) * t + 0.75f;
		else if (t < 2.5f / d1)
			return n1 * (t -= 2.25f / d1) * t + 0.9375f;
		else
			return n1 * (t -= 2.625f / d1) * t + 0.984375f;
	}
	
	public static float easeStep(float t)
	{
		return t * t * (3.0f - 2.0f * t);
	}
	
	public static float smoothstep(float a, float b, float t)
	{
		t = MathUtils.clamp((t - a) / (b - a), 0, 1);
		return easeStep(t);
	}
	
	public static float triangleWave(float t)
	{
		return Math.abs((float)Math.floor(t) + 0.5f - t) * 2.0f;
	}
}
