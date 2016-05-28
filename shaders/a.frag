#version 410

in V_TO_F {
	vec3 vertCoords;
	vec4 vertColor;
	vec2 vertUV;
	vec3 vertNorm;
} v_to_f;

out vec4 out_color;

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

/*uniform vec3 u_camLoc;
uniform vec3 u_lightDir;
uniform vec3 u_ambientCol;
uniform vec3 u_diffuseCol;
uniform vec3 u_specularCol;*/

void main(void) {
    vec3 norm = normalize(v_to_f.vertNorm);

	vec3 lightDir = normalize(-light_dir);
	vec3 viewDir = normalize(view_camLoc - v_to_f.vertCoords);
	vec3 halfway = normalize(lightDir + viewDir);

	vec3 ambientCol = light_color * light_ambience;

	vec3 diffuseCol = light_color * max(dot(lightDir, norm), 0.0f);

	float specularIntensity = 0.5f;
	int shininess = 32;
	vec3 specularCol = light_color * pow(max(dot(norm, halfway), 0.0f), shininess) * specularIntensity;

	out_color = v_to_f.vertColor;
	out_color.rgb *= (ambientCol + diffuseCol + specularCol) * light_strength;
}