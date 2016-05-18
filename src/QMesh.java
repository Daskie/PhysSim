import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

/**
 * @since 5/17/2016
 */
public class QMesh {

    static final int COORDS_BYTES = 3 * 4;
    static final int COLOR_BYTES = 4 * 4;
    static final int UV_BYTES = 2 * 4;
    static final int NORM_BYTES = 3 * 4;
    static final int VERT_BYTES = COORDS_BYTES + COLOR_BYTES + UV_BYTES + NORM_BYTES;

    class Coords {
        public float x, y, z;
    }

    class Color {
        public float r, g, b, a;
    }

    class UV {
        public float u, v;
    }

    class Norm {
        public float x, y, z;
    }

    private int nVerts;
    private Coords[] coords;
    private Color[] colors;
    private UV[] uvs;
    private Norm[] norms;
    private int nIndices;
    private int[] indices;

    public int nVerts() {
        return nVerts;
    }

    public int nIndices() {
        return nIndices;
    }

    QVAO buffer(int nInstances, QMat4[] instanceMats) {

        FloatBuffer vertBuffer = createFloatBuffer(nVerts * 12 + nInstances * 16);
        for (int i = 0; i < nVerts; ++i) {
            vertBuffer.put(coords[i].x);
            vertBuffer.put(coords[i].y);
            vertBuffer.put(coords[i].z);

            vertBuffer.put(colors[i].r);
            vertBuffer.put(colors[i].g);
            vertBuffer.put(colors[i].b);
            vertBuffer.put(colors[i].a);

            vertBuffer.put(uvs[i].u);
            vertBuffer.put(uvs[i].v);

            vertBuffer.put(norms[i].x);
            vertBuffer.put(norms[i].y);
            vertBuffer.put(norms[i].z);
        }
        for (int i = 0; i < nInstances; ++i) {
            for (int ci = 0; ci < 4; ++ci) {
                for (int ri = 0; ri < 4; ++ri) {
                    vertBuffer.put(instanceMats[i].get(ci, ri));
                }
            }
        }
        vertBuffer.flip();

        IntBuffer indexBuffer = createIntBuffer(nIndices);
        indexBuffer.put(indices);
        indexBuffer.flip();

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int offset = 0;
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(
                0,										// attribute 0. Match the layout in the shader.
                3,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                       				// array buffer offset
        );
        offset += nVerts * COORDS_BYTES;
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(
                1,										// attribute 0. Match the layout in the shader.
                4,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                                  // array buffer offset
        );
        offset += nVerts * COLOR_BYTES;
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(
                2,										// attribute 0. Match the layout in the shader.
                2,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                       				// array buffer offset
        );
        offset += nVerts * UV_BYTES;
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(
                3,										// attribute 0. Match the layout in the shader.
                3,				                        // size
                GL_FLOAT,					            // type
                false,								    // normalized?
                0,										// stride
                offset                       			// array buffer offset
        );
        offset += nVerts * NORM_BYTES;
        if (nInstances > 0) {
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(
                    4,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glEnableVertexAttribArray(5);
            glVertexAttribPointer(
                    5,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glEnableVertexAttribArray(6);
            glVertexAttribPointer(
                    6,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glEnableVertexAttribArray(7);
            glVertexAttribPointer(
                    7,
                    4,
                    GL_FLOAT,
                    false,
                    16,
                    offset
            );
            offset += 4;
            glVertexAttribDivisor(4, 1);
            glVertexAttribDivisor(5, 1);
            glVertexAttribDivisor(6, 1);
            glVertexAttribDivisor(7, 1);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);

        return new QVAO(vao, vbo, ebo);
    }

}
