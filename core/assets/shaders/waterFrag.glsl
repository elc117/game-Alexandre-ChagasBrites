#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform LOWP vec4 u_color;

void main()
{
	gl_FragColor = u_color * vec4(117.0 / 255.0, 206.0 / 255.0, 251.0 / 255.0, 0.25);
	gl_FragColor = mix(gl_FragColor, vec4(76.0 / 255.0, 106.0 / 255.0, 200.0 / 255.0, 0.5), smoothstep(0.0, 1.0, v_texCoords.y));
	gl_FragColor = mix(gl_FragColor, vec4(1.0), step(v_texCoords.y, 1.0 / (20.0 * 16.0)));
}