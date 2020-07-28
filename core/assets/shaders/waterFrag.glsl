#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

#define WATER_VERTICES 32

varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform LOWP vec4 u_color;

uniform float u_pollution;
uniform float u_heights[WATER_VERTICES];
uniform float u_width;
uniform float u_waterHeight;
uniform float u_totalHeight;

const float c_tileHeight = 16.0;
const vec4 c_pollutedWaterColor = vec4(176.0 / 255.0, 239.0 / 255.0, 73.0 / 255.0, 1.0);

void main()
{
	float t = floor(v_texCoords.x * u_width);
	//float height = mix(u_heights[int(t)], u_heights[(int(t) + 1)], smoothstep(t, t + 1.0, v_texCoords.x * u_width));
	//height = -floor(height * c_tileHeight + 0.5) /c_tileHeight;

	float height = 0.0;
	float smoothHeight = 0.0;
	int index = int(t);
	for (int i = 0; i < WATER_VERTICES; i++) 
	{
		if (i == index)
		{
			height = mix(u_heights[i], u_heights[i + 1], smoothstep(t, t + 1.0, v_texCoords.x * u_width));
			smoothHeight = -height;
			height = -floor(height * c_tileHeight + 0.5) /c_tileHeight;
		}
    }

	float currentHeight = v_texCoords.y * u_totalHeight - u_waterHeight;
	if (currentHeight < height)
		discard;

	gl_FragColor = u_color;
	gl_FragColor = mix(gl_FragColor, c_pollutedWaterColor, u_pollution);

	gl_FragColor.r *= clamp(smoothstep(mix(1.0, 8.0, u_pollution), 0.0, currentHeight - smoothHeight), 0.0, 1.0);
	gl_FragColor.g *= mix(clamp(smoothstep(1.0, 0.0, v_texCoords.y), 0.0, 1.0), 1.0, 0.25 * u_pollution);
	gl_FragColor.b *= mix(clamp(smoothstep(1.0, 0.0, v_texCoords.y), 0.0, 1.0), 1.0, 0.25 * (1.0 - u_pollution));
	gl_FragColor.a = mix(0.25 + 0.5 * u_pollution, 0.5 + 0.5 * u_pollution, v_texCoords.y);

	gl_FragColor = mix(gl_FragColor, vec4(1.0), step(currentHeight, height + 1.0 / c_tileHeight));
	gl_FragColor.a += 0.25 * (1.0 - u_pollution) * gl_FragColor.r;
}