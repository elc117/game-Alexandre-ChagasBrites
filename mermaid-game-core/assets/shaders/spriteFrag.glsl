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
	gl_FragColor = u_color * texture2D(u_texture, v_texCoords);
	//if (gl_FragColor.a < 0.5)
	//	discard;
}