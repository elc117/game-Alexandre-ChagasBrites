package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class MaterialAttribute 
{
	public String key;
	public abstract void set(ShaderProgram shader);
}
