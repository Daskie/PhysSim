#version 410

layout (location = 0) in vec3 in_vertCoords;
layout (location = 1) in vec4 in_vertColor;
layout (location = 2) in vec2 in_vertUV;
layout (location = 3) in vec3 in_vertNorm;
layout (location = 4) in mat4 in_instanceMat; //5, 6, 7
layout (location = 8) in float in_instanceCharge;
layout (location = 9) in int in_instanceID;

out V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
	flat float instanceCharge;
	flat int instanceID;
} v_to_f;

layout (std140) uniform Transform {
    mat4 transform_modelMat;
    mat4 transform_normMat;
    mat4 transform_viewMat;
    mat4 transform_projMat;
};

void main(void)
{
    vec3 coords = vec3(in_instanceMat * transform_modelMat * vec4(in_vertCoords, 1.0f));
	gl_Position = transform_projMat * transform_viewMat * vec4(coords, 1.0f);
	v_to_f.vertCoords = coords;
	v_to_f.vertColor = vec4(max(sign(in_instanceCharge), 0.0f), 0.0f, max(sign(-in_instanceCharge), 0.0f), 1.0f);
	v_to_f.vertUV = in_vertUV;
	v_to_f.vertNorm = mat3(transform_normMat) * in_vertNorm;
	v_to_f.instanceCharge = in_instanceCharge;
	v_to_f.instanceID = in_instanceID;
}