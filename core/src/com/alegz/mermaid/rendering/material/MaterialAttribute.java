package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class MaterialAttribute 
{
	public abstract void set(String key, ShaderProgram shader);
}
