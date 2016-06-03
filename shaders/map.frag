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
    vec4 vec;
};

struct LineCharge { //needs to be 140 aligned
    vec3 loc;
    float chargeDensity;
    vec3 dir;
    float filler0;
};

layout (std140) uniform ChargeCounts {
    int chargeCounts_nSpheres;
    int chargeCounts_nPlanes;
    int chargeCount_nLines;
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

layout (std140) uniform Threshold {
    float threshold_minMagE;
    float threshold_maxMagE;
    float threshold_minMagV;
    float threshold_maxMagV;
    float threshold_vStep;
};

const float PI = 3.14159265f;
const float E0 = 8.854e-12f;
const float K = 8.98774244e9f;   // 1/4/pi/e0

const float maxAlpha = 0.9f;

float dist2(vec3 v1, vec3 v2) {
    v1 = v1 - v2;
    return dot(v1, v1);
}

float distToLine(vec3 lineLoc, vec3 lineDir, vec3 p) {
    return length(cross(p - lineLoc, normalize(lineDir)));
}

float distToPlane(vec4 planeVec, vec3 p) {
    return abs((dot(planeVec.xyz, p) + planeVec.w) / length(planeVec.xyz));
}

float calcSphereE(float charge, vec3 sphereLoc, vec3 p) {
    return K * charge / dist2(sphereLoc, p);
}

float calcSphereV(float charge, vec3 sphereLoc, vec3 p) {
    return K * charge / sqrt(dist2(sphereLoc, p));
}

float calcPlaneE(float chargeDensity) {
    return chargeDensity / 2.0f / E0;
}

float calcPlaneV(float chargeDensity, vec4 planeVec, vec3 p) {
    return chargeDensity / 2.0f / E0 * distToPlane(planeVec, p);
}

float calcLineE(float chargeDensity, vec3 lineLoc, vec3 lineDir, vec3 p) {
    return 2.0f * K * chargeDensity / distToLine(lineLoc, lineDir, p);
}

float calcLineV(float chargeDensity, vec3 lineLoc, vec3 lineDir, vec3 p) {
    return 2.0f * K * chargeDensity * log(distToLine(lineLoc, lineDir, p));
}

void main(void) {

    float E = 0;
    float V = 0;
    for (int i = 0; i < chargeCounts_nSpheres; ++i) {
        E += calcSphereE(sphereCharges_spheres[i].charge, sphereCharges_spheres[i].loc, v_to_f.vertCoords);
        V += calcSphereV(sphereCharges_spheres[i].charge, sphereCharges_spheres[i].loc, v_to_f.vertCoords);
    }
    for (int i = 0; i < chargeCounts_nPlanes; ++i) {
        E += calcPlaneE(planeCharges_planes[i].chargeDensity);
        V += calcPlaneV(planeCharges_planes[i].chargeDensity, planeCharges_planes[i].vec, v_to_f.vertCoords);
    }
    for (int i = 0; i < chargeCount_nLines; ++i) {
        E += calcLineE(lineCharges_lines[i].chargeDensity, lineCharges_lines[i].loc, lineCharges_lines[i].dir, v_to_f.vertCoords);
        V += calcLineV(lineCharges_lines[i].chargeDensity, lineCharges_lines[i].loc, lineCharges_lines[i].dir, v_to_f.vertCoords);
    }

    float eRel = sign(E) * clamp((abs(E) - threshold_minMagE) / (threshold_maxMagE - threshold_minMagE), 0.0f, 1.0f);
    float vRel = sign(V) * clamp((abs(V) - threshold_minMagV) / (threshold_maxMagV - threshold_minMagV), 0.0f, 1.0f);
    float vLine = V / threshold_vStep;
    vLine = abs(vLine - round(vLine)) * 2.0f;

	out_color.rgb = vec3(max(sign(eRel), 0.0f), 0.0f, max(sign(-eRel), 0.0f));
	out_color.rgb = mix(vec3(0.1f), out_color.rgb, step(0.1f, vLine));
	out_color.a = abs(eRel) * maxAlpha;
}