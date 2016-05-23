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

    public static Mat4 translation(Vec3 delta) {
        Mat4 mat = new Mat4();
        mat.mat[3][0] = delta.x;
        mat.mat[3][1] = delta.y;
        mat.mat[3][2] = delta.z;
        return mat;
    }

    public static Mat4 ortho(float w, float h, float n, float f) {
        return new Mat4(
                2.0f / w, 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / h, 0.0f, 0.0f,
                0.0f, 0.0f, 2.0f / (n - f), 0.0f,
                0.0f, 0.0f, (f + n) / (n - f), 1.0f
        );
    }

    public static Mat4 orthoAsym(float l, float r, float b, float t, float n, float f) {
        l *= -1.0f;
        b *= -1.0f;
        return new Mat4(
                2.0f / (r - l), 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / (t - b), 0.0f, 0.0f,
                0.0f, 0.0f, 2.0f / (n - f), 0.0f,
                (r + l) / (l - r), (t + b) / (b - t), (f + n) / (n - f), 1.0f
        );
    }

    public static Mat4 perspective(float fov, float a, float n, float f) {
        float t_n = (float)Math.tan(fov / 2.0f);

        return new Mat4(
                1.0f / (a * t_n), 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f / t_n, 0.0f, 0.0f,
                0.0f, 0.0f, (f + n) / (n - f), -1.0f,
                0.0f, 0.0f, 2.0f * f * n / (n - f), 0.0f
        );
    }

    public static Mat4 perspectiveAsym(float fovNX, float fovPX, float fovNY, float fovPY, float n, float f) {
        float l = n * (float)Math.tan(fovNX) * -1.0f;
        float r = n * (float)Math.tan(fovPX);
        float b = n * (float)Math.tan(fovNY) * -1.0f;
        float t = n * (float)Math.tan(fovPY);

        return new Mat4(
                2.0f * n / (r - l),	0.0f, 0.0f, 0.0f,
                0.0f, 2.0f * n / (t - b), 0.0f, 0.0f,
                (r + l) / (r - l), (t + b) / (t - b), (f + n) / (n - f), -1.0f,
                0.0f, 0.0f, 2.0f * f * n / (n - f), 0.0f
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
