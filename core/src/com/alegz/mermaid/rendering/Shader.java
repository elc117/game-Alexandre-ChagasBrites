package com.alegz.mermaid.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shader 
{
	private ShaderProgram program;
	private boolean blend;
	
	public Shader(ShaderProgram program, boolean blend)
	{
		this.program = program;
		this.blend = blend;
	}
	
	public void begin()
	{
		program.begin();
		if (blend)
		{
			Gdx.gl20.glEnable(GL20.GL_BLEND);
			Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	public void end()
	{
		if (blend)
			Gdx.gl20.glDisable(GL20.GL_BLEND);
		program.end();
	}
	
	public ShaderProgram getProgram()
	{
		return program;
	}
	
	public void dispose()
	{
		program.dispose();
	}
}
