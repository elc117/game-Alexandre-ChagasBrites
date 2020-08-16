attribute vec3 a_position;
attribute vec2 a_texCoord;

varying vec2 v_texCoords;

uniform mat4 u_projTrans;
uniform float u_time;

void main()
{
	v_texCoords = a_texCoord;
	vec3 position = a_position;
	float size = 1.0 / 32.0;
	float mask = clamp(smoothstep(0.0, -1.0, a_position.y), 0.0, 1.0);
	position.x += size * sin(a_position.x * 0.5 + a_position.y * 2.0 + u_time) * mask;
	position.y += size * sin(a_position.y * 0.5 + a_position.x * 2.0 + u_time) * mask;
	gl_Position = u_projTrans * vec4(position, 1.0);
}
