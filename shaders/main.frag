#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
	flat float instanceCharge;
	flat int instanceID;
} v_to_f;

layout (location = 0) out vec4 out_color;
layout (location = 1) out int out_id;

layout (std140) uniform View {
    vec3 view_camLoc;
    float view_nearFrust;
    vec3 view_camForward;
    float view_farFrust;
    vec3 view_camUp;
    float view_fov;
};

layout (std140) uniform Light {
    vec3 light_dir;
    float light_strength;
    vec3 light_color;
    float light_ambience;
};

layout (std140) uniform ID {
    int id_hovored;
    int id_selected;
};

void main(void) {
    vec3 norm = normalize(v_to_f.vertNorm);

	vec3 lightDir = normalize(-light_dir);
	vec3 viewDir = normalize(view_camLoc - v_to_f.vertCoords);
	vec3 halfway = normalize(lightDir + viewDir);

    vec3 ambientCol = v_to_f.vertColor.rgb;
    vec3 diffuseCol = v_to_f.vertColor.rgb;
    vec3 specularCol = v_to_f.vertColor.rgb;

	ambientCol *= light_color * light_ambience;

	diffuseCol *= light_color * max(dot(lightDir, norm), 0.0f);

	float specularIntensity = 0.5f;
	int shininess = 32;
	specularCol *= light_color * pow(max(dot(norm, halfway), 0.0f), shininess) * specularIntensity;

	out_color.rgb = (ambientCol + diffuseCol + specularCol) * light_strength;

    float hovored = step(0, id_hovored) * (1.0f - abs(sign(v_to_f.instanceID - id_hovored)));
    float selected = step(0, id_selected) * (1.0f - abs(sign(v_to_f.instanceID - id_selected)));
    float highlight = mix(hovored * 0.25, selected * -0.25, selected);

    out_color.rgb += highlight;

	out_color.a = v_to_f.vertColor.a;

	out_id = v_to_f.instanceID;
}