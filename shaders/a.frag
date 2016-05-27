#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

out vec4 out_color;

uniform vec3 u_camLoc;
uniform vec3 u_lightDir;
uniform vec3 u_ambientCol;
uniform vec3 u_diffuseCol;
uniform vec3 u_specularCol;

void main(void) {
	vec3 lightDir = normalize(-u_lightDir);
	vec3 viewDir = normalize(u_camLoc - v_to_f.vertCoords);
	vec3 halfway = normalize(lightDir + viewDir);

	vec3 ambientCol = u_ambientCol;

	vec3 diffuseCol = u_diffuseCol * max(dot(lightDir, v_to_f.vertNorm), 0.0f);

	float specularIntensity = 0.5f;
	int shininess = 32;
	vec3 specularCol = u_specularCol * pow(max(dot(v_to_f.vertNorm, halfway), 0.0f), shininess) * specularIntensity;

	out_color = v_to_f.vertColor;
	out_color.rgb *= ambientCol + diffuseCol + specularCol;
}