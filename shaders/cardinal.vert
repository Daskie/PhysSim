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

layout (std140) uniform Model {
    mat4 model_modelMat;
    mat4 model_normMat;
};

uniform vec3 u_camLoc;
uniform vec2 u_screenPos;

void main(void) {
    mat3 viewMat = mat3(camera_viewMat);
    mat3 normViewMat = transpose(inverse(viewMat));

    vec3 coords = viewMat * mat3(model_modelMat) * in_vertCoords - u_camLoc;
	gl_Position = camera_projMat * vec4(coords, 1.0f);
	gl_Position.xy += u_screenPos * gl_Position.w;
	v_to_f.vertCoords = coords;
	v_to_f.vertColor = in_vertColor;
	v_to_f.vertUV = in_vertUV;
	v_to_f.vertNorm = normViewMat * mat3(model_normMat) * in_vertNorm;
}