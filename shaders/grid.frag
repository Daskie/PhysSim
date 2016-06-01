#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

out vec4 out_color;

uniform int u_divisions;

void main(void) {

    vec2 loc = abs((v_to_f.vertUV - 0.5f)) * u_divisions;
    loc -= ivec2(loc);
    loc = min(loc, 1.0f - loc);
    float v = min(loc.x, loc.y);
    v /= 0.5f;
    v = 1.0f - v;
    v *= v;
    v *= v;
    v *= v;
    v *= v;
    v *= v;

    out_color = vec4(0.1f, 0.1f, 0.1f, v);

}