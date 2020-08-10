package com.alegz.mermaid.utils;

import com.badlogic.gdx.math.MathUtils;

public class GameUtils 
{
	public static float damp(float damping, float deltaTime)
	{
		return 1.0f - (float)Math.pow(damping, deltaTime);
	}
	
	public static float smoothstep(float a, float b, float t)
	{
		t = MathUtils.clamp((t - a) / (b - a), 0, 1);
		return t * t * (3.0f - 2.0f * t);
	}
	
	public static float triangleWave(float t)
	{
		return Math.abs((float)Math.floor(t) + 0.5f - t) * 2.0f;
	}
}
