#version 410

in V_TO_F {
	vec2 vertUV;
} v_to_f;

out vec4 out_color;

uniform sampler2D u_tex;

void main(void) {
	out_color = texture(u_tex, v_to_f.vertUV);
}