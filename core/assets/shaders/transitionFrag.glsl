#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;

uniform float u_cutout;
uniform float u_flip;
uniform vec2 u_size;

void main()
{
	float cutout = 0.0;

	vec2 world = (v_texCoords - 0.5) * u_size;
	vec2 local = fract(world - 0.5);

	//float i = max(abs(local.x - 0.5), abs(local.y - 0.5)) * 2.0;
	float i = length(local - 0.5) / sqrt(0.5);

	//float j = length(v_texCoords - 0.5) / sqrt(0.5);
	//float j = dot(abs(v_texCoords - 0.5) * 2.0, vec2(0.5, 0.5) * 2.0) / 2.0;
	float j = abs(dot(v_texCoords - 0.5, vec2(0.5, -0.5) * 2.0)) / 2.0;
	//float j = max(abs(v_texCoords.x - 0.5), abs(v_texCoords.y - 0.5)) * 2.0;
	//float j = dot(v_texCoords, vec2(1.0, 1.0)) / 2.0;
	j = mix(j, 1.0 - j, u_flip);

	cutout = mix(j, i, 0.25);
	if (cutout > 1.0 - u_cutout)
		discard;
	gl_FragColor = vec4(17.0 / 255.0, 21.0 / 255.0, 26.0 / 255.0, 1.0);
}