#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float u_pollution;
uniform float u_width;
uniform float u_waterHeight;
uniform float u_totalHeight;

const float c_tileHeight = 16.0;
const vec4 c_pollutedWaterColor = vec4(176.0 / 255.0, 239.0 / 255.0, 73.0 / 255.0, 1.0);

void main()
{
    vec2 uv = vec2(floor(v_texCoords.x * u_width) / u_width, 0.5);
    vec2 uv2 = vec2(uv.x + 1.0 / u_width, 0.5);

    vec4 water0 = texture2D(u_texture, uv);
    vec4 water1 = texture2D(u_texture, uv2);

    float smoothwater = smoothstep(uv.x, uv2.x, v_texCoords.x);
    float height = mix(water0.x * 2.0 - 1.0, water1.x * 2.0 - 1.0, smoothwater);

    float smoothHeight = -height;
	height = -floor(height * c_tileHeight + 0.5) / c_tileHeight;

	float currentHeight = v_texCoords.y * u_totalHeight - u_waterHeight;
	if (currentHeight < height)
		discard;

	gl_FragColor = mix(vec4(1.0), c_pollutedWaterColor, u_pollution);

	gl_FragColor.r *= clamp(smoothstep(mix(1.0, 8.0, u_pollution), 0.0, currentHeight - smoothHeight), 0.0, 1.0);
	gl_FragColor.g *= mix(clamp(smoothstep(1.0, 0.0, v_texCoords.y), 0.0, 1.0), 1.0, 0.25 * u_pollution);
	gl_FragColor.b *= mix(clamp(smoothstep(1.0, 0.0, v_texCoords.y), 0.0, 1.0), 1.0, 0.25 * (1.0 - u_pollution));
	gl_FragColor.a = mix(0.25 + 0.5 * u_pollution, 0.5 + 0.5 * u_pollution, v_texCoords.y);

	gl_FragColor = mix(gl_FragColor, vec4(1.0), step(currentHeight, height + 1.0 / c_tileHeight));
	gl_FragColor.a += 0.25 * (1.0 - u_pollution) * gl_FragColor.r;
}