package qps;

/**
 * @since 5/17/2016
 */
public class Mat4 extends MatN {

    public Mat4() {
        super(4, 4);
    }

    public Mat4(float... vs) {
        super(4, 4, vs);
    }

    public Mat4(MatN o) {
        super(4, 4, o);
    }

    public Vec4 mult(Vec4 v) {
        return new Vec4(
                v.x * mat[0][0] + v.y * mat[1][0] + v.z * mat[2][0] + v.w * mat[3][0],
                v.x * mat[0][1] + v.y * mat[1][1] + v.z * mat[2][1] + v.w * mat[3][1],
                v.x * mat[0][2] + v.y * mat[1][2] + v.z * mat[2][2] + v.w * mat[3][2],
                v.x * mat[0][3] + v.y * mat[1][3] + v.z * mat[2][3] + v.w * mat[3][3]
        );
    }

    public static Mat4 translate(Vec3 delta) {
        Mat4 mat = new Mat4();
        mat.mat[3][0] = delta.x;
        mat.mat[3][1] = delta.y;
        mat.mat[3][2] = delta.z;
        return mat;
    }

    public static Mat4 ortho(float width, float height, float near, float far) {
        return new Mat4(
                2.0f / width, 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / height, 0.0f, 0.0f,
                0.0f, 0.0f, 2.0f / (near - far), 0.0f,
                0.0f, 0.0f, (far + near) / (near - far), 1.0f
        );
    }

    public static Mat4 orthoAsym(float nWidth, float pWidth, float nHeight, float pHeight, float near, float far) {
        nWidth *= -1.0f;
        nHeight *= -1.0f;
        return new Mat4(
                2.0f / (pWidth - nWidth), 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / (pHeight - nHeight), 0.0f, 0.0f,
                0.0f, 0.0f, 2.0f / (near - far), 0.0f,
                (pWidth + nWidth) / (nWidth - pWidth), (pHeight + nHeight) / (nHeight - pHeight), (far + near) / (near - far), 1.0f
        );
    }

    public static Mat4 perspective(float fov, float aspectRatio, float near, float far) {
        float t_n = (float)Math.tan(fov / 2.0f);

        return new Mat4(
                1.0f / (aspectRatio * t_n), 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f / t_n, 0.0f, 0.0f,
                0.0f, 0.0f, (far + near) / (near - far), -1.0f,
                0.0f, 0.0f, 2.0f * far * near / (near - far), 0.0f
        );
    }

    public static Mat4 perspectiveAsym(float fovNX, float fovPX, float fovNY, float fovPY, float near, float far) {
        float l = near * (float)Math.tan(fovNX) * -1.0f;
        float r = near * (float)Math.tan(fovPX);
        float b = near * (float)Math.tan(fovNY) * -1.0f;
        float t = near * (float)Math.tan(fovPY);

        return new Mat4(
                2.0f * near / (r - l),	0.0f, 0.0f, 0.0f,
                0.0f, 2.0f * near / (t - b), 0.0f, 0.0f,
                (r + l) / (r - l), (t + b) / (t - b), (far + near) / (near - far), -1.0f,
                0.0f, 0.0f, 2.0f * far * near / (near - far), 0.0f
        );
    }

    public static Mat4 view(Vec3 camPos, Vec3 camDir, Vec3 camUp) {
        Vec3 Z = camDir.norm().mult(-1.0f);
        Vec3 Y = camUp.norm();
        Vec3 X = Y.cross(Z);
        Vec3 P = camPos.mult(-1.0f);

        return new Mat4(
                X.x, Y.x, Z.x, 0.0f,
                X.y, Y.y, Z.y, 0.0f,
                X.z, Y.z, Z.z, 0.0f,
                P.dot(X), P.dot(Y), P.dot(Z), 1.0f
        );
    }

}
