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
};

layout (std140) uniform SphereCharges {
    ChargeObject sphereCharges_spheres[128];
};

float dist2(vec3 v1, vec3 v2) {
    v1 = v1 - v2;
    return dot(v1, v1);
}

void main(void) {

    float eField = 0;
    for (int i = 0; i < chargeCounts_nSpheres; ++i) {
        eField += sphereCharges_spheres[i].charge / dist2(v_to_f.vertCoords, sphereCharges_spheres[i].loc);
    }

	out_color = vec4(clamp(eField, 0.0f, 1.0f), 0.0f, clamp(-eField, 0.0f, 1.0f), 1.0f);
	out_color = vec4(1, 1, 1, 1);
}