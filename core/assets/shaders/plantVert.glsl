#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

attribute vec3 a_position;
attribute vec2 a_texCoord;

varying vec2 v_texCoords;
varying vec2 v_worldPos;

uniform mat4 u_projTrans;
uniform float u_time;

void main()
{
	v_texCoords = a_texCoord;
	v_worldPos = a_position.xy;
	vec3 position = a_position;
	float size = 1.0 / 32.0;
	float mask = clamp(smoothstep(0.0, -1.0, a_position.y), 0.0, 1.0);
	position.x += size * sin(a_position.x * 0.5 + a_position.y * 2.0 + u_time) * mask;
	position.y += size * sin(a_position.y * 0.5 + a_position.x * 2.0 + u_time) * mask;
	gl_Position = u_projTrans * vec4(position, 1.0);
}
