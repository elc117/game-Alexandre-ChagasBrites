#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform LOWP vec4 u_color;

uniform LOWP vec4 u_topColor;
uniform LOWP vec4 u_bottomColor;
uniform float u_nearPlane;
uniform float u_farPlane;

void main()
{
	gl_FragColor = u_color * texture2D(u_texture, v_texCoords);
	if (gl_FragColor.a <= 0.0)
		discard;

	float n = u_nearPlane;
	float f = u_farPlane;
	float depth = 2.0 * n * f / (f + n - (gl_FragCoord.z * 2.0 - 1.0) * (f - n));
	depth = clamp(smoothstep(1.0, 5.0, depth), 0.0, 1.0);

	vec3 background = mix(u_topColor.rgb, u_bottomColor.rgb, v_texCoords.y);
	gl_FragColor.rgb = mix(gl_FragColor.rgb, background, v_texCoords.y * depth);
}