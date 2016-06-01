#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

layout (location = 0) out vec4 out_color;

void main(void) {
	out_color = vec4(0.1f, 0.1f, 0.1f, 1.0f);
}