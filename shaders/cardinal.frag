#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

layout (location = 0) out vec4 out_color;
layout (location = 1) out uint out_id;

layout (std140) uniform ID {
    uint id_id;
};

uniform vec3 u_camLoc;
uniform vec3 u_lightDir;
//uniform float u_lightStrength;
//uniform vec3 u_lightColor;
//uniform float u_lightAmbience;

void main(void) {
    vec3 norm = normalize(v_to_f.vertNorm);

	vec3 lightDir = normalize(-u_lightDir);
	vec3 viewDir = normalize(u_camLoc - v_to_f.vertCoords);
	vec3 halfway = normalize(lightDir + viewDir);

    vec3 ambientCol = v_to_f.vertColor.rgb;
    vec3 diffuseCol = v_to_f.vertColor.rgb;
    vec3 specularCol = v_to_f.vertColor.rgb;

	ambientCol *= 0.25f;

	diffuseCol *= max(dot(lightDir, norm), 0.0f);

	float specularIntensity = 0.5f;
	int shininess = 32;
	specularCol *= pow(max(dot(norm, halfway), 0.0f), shininess) * specularIntensity;

	out_color.rgb = (ambientCol + diffuseCol + specularCol);
	out_color.a = v_to_f.vertColor.a;

	out_id = id_id;
}