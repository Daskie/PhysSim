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

layout (std140) uniform Camera {
    mat4 camera_viewMat;
    mat4 camera_projMat;
};

layout (std140) uniform View {
    vec3 view_camLoc;
    float view_nearFrust;
    vec3 view_camForward;
    float view_farFrust;
    vec3 view_camUp;
    float view_fov;
};

uniform vec3 u_dir;
uniform vec3 u_long;
uniform float u_thickness;
uniform int u_size;
uniform float u_spacing;

void main(void) {

    int centeredID = gl_InstanceID - u_size / 2;
    vec3 pos = centeredID * u_dir * u_spacing;
    float adjustedThickness = max(u_thickness, u_thickness * distance(view_camLoc, pos) / 5.0f);
    float mult0 = (1 - sign(abs(centeredID)));
    float mult5 = (1 - sign(mod(abs(centeredID), 5)));
    float mult10 = (1 - sign(mod(abs(centeredID), 10)));
    float width = 0.25f;
    width = mix(width, 0.5f, mult5);
    width = mix(width, 1.0f, mult10);
    width = mix(width, u_size / 2, mult0);
    vec3 coords = in_vertCoords * max((u_long * width * u_spacing), adjustedThickness);
    coords += pos;

	gl_Position = camera_projMat * camera_viewMat * vec4(coords, 1.0f);
	v_to_f.vertCoords = coords;
	v_to_f.vertColor = in_vertColor;
	v_to_f.vertUV = in_vertUV;
	v_to_f.vertNorm = in_vertNorm;
}