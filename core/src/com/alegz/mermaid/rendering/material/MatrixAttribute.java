package com.alegz.mermaid.rendering.material;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class MatrixAttribute  extends MaterialAttribute
{
	public Matrix4 matrix;
	
	public MatrixAttribute(Matrix4 matrix)
	{
		this.matrix = matrix;
	}
	
	@Override
	public void set(String key, ShaderProgram shader) 
	{
		shader.setUniformMatrix(key, matrix);
	}
}
