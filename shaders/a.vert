#version 410

layout (location = 0) in vec3 in_vertCoords;
layout (location = 1) in vec4 in_vertColor;
layout (location = 2) in vec2 in_vertUV;
layout (location = 3) in vec3 in_vertNorm;

out V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

void main(void)
{

    gl_Position = vec4(in_vertCoords, 1.0f);

	v_to_f.vertCoords = in_vertCoords;
	v_to_f.vertColor = in_vertColor;
	v_to_f.vertUV = in_vertUV;
	v_to_f.vertNorm = in_vertNorm;
}