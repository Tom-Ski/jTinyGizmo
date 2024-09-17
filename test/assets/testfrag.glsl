#version 150

uniform sampler2D u_texture;

in vec2 v_texCoords; // provided by vertex shader

layout (std140) uniform VertexData {
    vec4 v_polygon;
};
uniform float anotherValue;

out vec4 fragColor;

void main()
{
    // we ignore the texture but libgdx asserts there's always u_texture: https://stackoverflow.com/q/27967788/39531
    vec4 sampledButIgnored = texture(u_texture, v_texCoords);

        // we have data in v_polygon, we don't get here
    fragColor = vec4(v_polygon.x, v_polygon.y, v_polygon.z, 1.0);

    fragColor += vec4(0, 0, 0, sampledButIgnored.a);
}
