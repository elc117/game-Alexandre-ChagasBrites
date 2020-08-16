#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;
varying vec2 v_worldPos;

uniform sampler2D u_texture;
uniform float u_time;

void main()
{
	vec2 uv = v_texCoords;
	vec2 seed = floor(v_worldPos * 16.0) / 16.0;

	float wave = sin(seed.x * 1.0 + seed.y * 4.0 + u_time * 2.0);
	float power = 1.0 - v_texCoords.y * v_texCoords.y * v_texCoords.y;
	uv.x += floor(wave * power + 0.5) / 128.0;

	gl_FragColor = texture2D(u_texture, uv);
	if (gl_FragColor.a < 0.5)
		discard;
}