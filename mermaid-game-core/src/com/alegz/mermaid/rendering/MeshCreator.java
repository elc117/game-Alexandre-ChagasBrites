package com.alegz.mermaid.rendering;

import com.alegz.mermaid.Rect;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MeshCreator 
{
	private Mesh mesh;
	
	private float[] vertices;
	private int activeVertices;
	
	private short[] indices;
	private int activeIndices;
	
	public MeshCreator(int verticesCount, int indicesCount)
	{
		mesh = new Mesh(false, verticesCount, indicesCount,
						new VertexAttribute(VertexAttributes.Usage.Position, 3, GL20.GL_FLOAT, false, ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, GL20.GL_FLOAT, false, ShaderProgram.TEXCOORD_ATTRIBUTE));
		
		vertices = new float[verticesCount * 5];
		indices = new short[indicesCount];
	}
	
	public void render(ShaderProgram shader)
	{
		mesh.render(shader, GL20.GL_TRIANGLES, 0, activeIndices);
	}
	
	public void addSprite(Matrix4 modelMatrix, Rect rect)
	{
		Vector3[] quad = new Vector3[] {
			new Vector3(-0.5f, -0.5f, 0).mul(modelMatrix),
			new Vector3( 0.5f, -0.5f, 0).mul(modelMatrix),
			new Vector3(-0.5f,  0.5f, 0).mul(modelMatrix),
			new Vector3( 0.5f,  0.5f, 0).mul(modelMatrix)
		};
		
		indices[activeIndices++] = (short)(activeVertices / 5 + 0);
		indices[activeIndices++] = (short)(activeVertices / 5 + 1);
		indices[activeIndices++] = (short)(activeVertices / 5 + 2);
		indices[activeIndices++] = (short)(activeVertices / 5 + 2);
		indices[activeIndices++] = (short)(activeVertices / 5 + 1);
		indices[activeIndices++] = (short)(activeVertices / 5 + 3);
		
		vertices[activeVertices++] = quad[0].x;
		vertices[activeVertices++] = quad[0].y;
		vertices[activeVertices++] = quad[0].z;
		vertices[activeVertices++] = rect.x;
		vertices[activeVertices++] = rect.y + rect.height;
		
		vertices[activeVertices++] = quad[1].x;
		vertices[activeVertices++] = quad[1].y;
		vertices[activeVertices++] = quad[1].z;
		vertices[activeVertices++] = rect.x + rect.width;
		vertices[activeVertices++] = rect.y + rect.height;
		
		vertices[activeVertices++] = quad[2].x;
		vertices[activeVertices++] = quad[2].y;
		vertices[activeVertices++] = quad[2].z;
		vertices[activeVertices++] = rect.x;
		vertices[activeVertices++] = rect.y;
		
		vertices[activeVertices++] = quad[3].x;
		vertices[activeVertices++] = quad[3].y;
		vertices[activeVertices++] = quad[3].z;
		vertices[activeVertices++] = rect.x + rect.width;
		vertices[activeVertices++] = rect.y;
	}
	
	public void begin()
	{
		activeVertices = activeIndices = 0;
	}
	
	public void end()
	{
		mesh.setVertices(vertices, 0, activeVertices);
		mesh.setIndices(indices, 0, activeIndices);
	}
	
	public void dispose()
	{
		mesh.dispose();
	}
}
