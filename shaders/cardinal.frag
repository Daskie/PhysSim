#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

layout (location = 0) out vec4 out_color;
layout (location = 1) out int out_id;

layout (std140) uniform Light {
    vec3 light_dir;
    float light_strength;
    vec3 light_color;
    float light_ambience;
    float light_specularIntensity;
    float light_shininess;
};

layout (std140) uniform ID {
    int id_hovored;
    int id_selected;
};

uniform vec3 u_camLoc;
uniform vec3 u_lightDir;
uniform int u_id;
uniform vec3 u_lightColor;
uniform float u_lightStrength;

void main(void) {
    vec3 norm = normalize(v_to_f.vertNorm);

	vec3 lightDir = normalize(-u_lightDir);
	vec3 viewDir = normalize(u_camLoc - v_to_f.vertCoords);
	vec3 halfway = normalize(lightDir + viewDir);

    vec3 ambientCol = v_to_f.vertColor.rgb;
    vec3 diffuseCol = v_to_f.vertColor.rgb;
    vec3 specularCol = v_to_f.vertColor.rgb;

	ambientCol *= light_ambience;

	diffuseCol *= max(dot(lightDir, norm), 0.0f);

	specularCol *= pow(max(dot(norm, halfway), 0.0f), light_shininess) * light_specularIntensity;

	out_color.rgb = u_lightColor * (ambientCol + diffuseCol + specularCol) * u_lightStrength;
	out_color.a = v_to_f.vertColor.a;

    float hovored = step(0, id_hovored) * (1.0f - abs(sign(u_id - id_hovored)));
    float selected = step(0, id_selected) * (1.0f - abs(sign(u_id - id_selected)));
    float highlight = mix(hovored * 0.25, selected * 0.5, selected);

    out_color.rgb += highlight;

	out_id = u_id;
}