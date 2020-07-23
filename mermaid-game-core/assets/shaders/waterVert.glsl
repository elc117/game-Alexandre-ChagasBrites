attribute vec3 a_position;
attribute vec2 a_texCoord;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;

void main()
{
	v_texCoords = a_texCoord;
	gl_Position = u_projTrans * vec4(a_position, 1);
}
