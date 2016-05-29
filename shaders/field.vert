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

struct ChargeObject { //needs to be 140 aligned
    vec3 loc;
    float charge;
};

layout (std140) uniform Transform {
    mat4 transform_modelMat;
    mat4 transform_normMat;
    mat4 transform_viewMat;
    mat4 transform_projMat;
};

layout (std140) uniform ChargeCounts {
    int chargeCounts_nSpheres;
};

layout (std140) uniform SphereCharges {
    ChargeObject sphereCharges_spheres[128];
};

uniform vec3 u_fieldLoc;
uniform vec3 u_fieldSize;
uniform ivec3 u_fieldCount;

float dist2(vec3 v1, vec3 v2) {
    v1 = v1 - v2;
    return dot(v1, v1);
}

void main(void)
{
    vec3 loc = vec3(gl_InstanceID % u_fieldCount.x, (gl_InstanceID / u_fieldCount.x) % u_fieldCount.y, gl_InstanceID / (u_fieldCount.x * u_fieldCount.y));
    loc /= u_fieldCount - 1;
    loc -= 0.5f;
    loc *= u_fieldSize;
    loc += u_fieldLoc;

    vec3 coords = in_vertCoords + loc;

    float eField = 0;
    for (int i = 0; i < chargeCounts_nSpheres; ++i) {
        eField += sphereCharges_spheres[i].charge / dist2(coords, sphereCharges_spheres[i].loc);
    }

	gl_Position = transform_projMat * transform_viewMat * vec4(coords, 1.0f);
	v_to_f.vertColor = vec4(clamp(eField, 0.0f, 1.0f), 0.0f, clamp(-eField, 0.0f, 1.0f), 1.0f);
	v_to_f.vertUV = in_vertUV;
	v_to_f.vertNorm = mat3(transform_normMat) * in_vertNorm;
	v_to_f.vertCoords = coords;
}