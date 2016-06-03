#version 410

#define MAX_CHARGES 128

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

out vec4 out_color;

struct SphereCharge { //needs to be 140 aligned
    vec3 loc;
    float charge;
};

struct PlaneCharge { //needs to be 140 aligned
    vec3 norm;
    float chargeDensity;
};

struct LineCharge { //needs to be 140 aligned
    vec3 dir;
    float chargeDensity;
};

layout (std140) uniform ChargeCounts {
    int chargeCounts_nSpheres;
    int chargeCounts_nPlanes;
};

layout (std140) uniform SphereCharges {
    SphereCharge sphereCharges_spheres[MAX_CHARGES];
};

layout (std140) uniform PlaneCharges {
    PlaneCharge planeCharges_planes[MAX_CHARGES];
};

layout (std140) uniform LineCharges {
    LineCharge lineCharges_lines[MAX_CHARGES];
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

float calcSphereE(float charge, float distance2) {
    return K * charge / distance2;
}

float calcPlaneE(float chargeDensity) {
    return chargeDensity / 2.0f / E0;
}

void main(void) {

    float eField = 0;
    for (int i = 0; i < chargeCounts_nSpheres; ++i) {
        eField += calcSphereE(sphereCharges_spheres[i].charge, dist2(v_to_f.vertCoords, sphereCharges_spheres[i].loc));
    }
    for (int i = 0; i < chargeCounts_nPlanes; ++i) {
        eField += calcPlaneE(planeCharges_planes[i].chargeDensity);
    }

    float eRel = sign(eField) * clamp((abs(eField) - eThreshold_minMagE) / (eThreshold_maxMagE - eThreshold_minMagE), 0.0f, 1.0f);

	out_color.rgb = vec3(max(sign(eRel), 0.0f), 0.0f, max(sign(-eRel), 0.0f));
	out_color.a = abs(eRel) * maxAlpha;
}