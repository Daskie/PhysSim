#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

out vec4 out_color;

struct ChargeObject { //needs to be 140 aligned
    vec3 loc;
    float charge;
};

layout (std140) uniform ChargeCounts {
    int chargeCounts_nSpheres;
    int chargeCounts_nPlanes;
};

layout (std140) uniform SphereCharges {
    ChargeObject sphereCharges_spheres[128];
};

layout (std140) uniform EThreshold {
    float eThreshold_minMagE;
    float eThreshold_maxMagE;
};

const float PI = 3.14159265f;
const float E0 = 8.854e-12f;
const float K = 8.98774244e9f;

const float maxAlpha = 0.9f;

float dist2(vec3 v1, vec3 v2) {
    v1 = v1 - v2;
    return dot(v1, v1);
}

void main(void) {

    float eField = 0;
    for (int i = 0; i < chargeCounts_nSpheres; ++i) {
        eField += sphereCharges_spheres[i].charge / dist2(v_to_f.vertCoords, sphereCharges_spheres[i].loc);
    }
    eField *= K;

    float eRel = sign(eField) * clamp((abs(eField) - eThreshold_minMagE) / (eThreshold_maxMagE - eThreshold_minMagE), 0.0f, 1.0f);

	out_color.rgb = vec3(max(sign(eRel), 0.0f), 0.0f, max(sign(-eRel), 0.0f));
	out_color.a = abs(eRel) * maxAlpha;
}