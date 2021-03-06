package qps.utils;

/**
 * @since 5/17/2016
 */
public class Mat3 extends MatN {

    public Mat3() {
        super(3, 3);
    }

    public Mat3(Mat3 o) {
        super(o);
    }

    public Mat3(boolean initDiag) {
        super(3, 3, initDiag);
    }

    public Mat3(float... vs) {
        super(3, 3, vs);
    }

    public Mat3(MatN o) {
        super(3, 3, o);
    }

    public Vec3 mult(Vec3 v) {
        return new Vec3(
                v.x * mat[0][0] + v.y * mat[1][0] + v.z * mat[2][0],
                v.x * mat[0][1] + v.y * mat[1][1] + v.z * mat[2][1],
                v.x * mat[0][2] + v.y * mat[1][2] + v.z * mat[2][2]
        );
    }

    public static Mat3 rotate(float theta, Vec3 axis) {
        if (axis.mag2() == 0) { //can't rotate around 0 length vector
            return new Mat3();
        }

        Vec3 a = axis.norm();

        float s = (float)Math.sin(theta);
        float c = (float)Math.cos(theta);
        float cm = 1.0f - c;

        return new Mat3(
                c + a.x * a.x * cm, a.y * a.x * cm + a.z * s, a.z * a.x * cm - a.y * s,
                a.x * a.y * cm - a.z * s, c + a.y * a.y * cm, a.z * a.y * cm + a.x * s,
                a.x * a.z * cm + a.y * s, a.y * a.z * cm - a.x * s, c + a.z * a.z * cm
        );
    }

    public static Mat3 align(Vec3 v1, Vec3 v2) {
        v1 = v1.norm();
        v2 = v2.norm();

        Vec3 c = v1.cross(v2);
        float d = Utils.clamp(v1.dot(v2), -1.0f, 1.0f);

        return rotate((float)Math.acos(d), c);
    }

    public static Mat3 align(Vec3 forward1, Vec3 up1, Vec3 forward2, Vec3 up2) {
        Mat3 m = align(forward1, forward2);
        return new Mat3(align(m.mult(up1), up2).mult(m));
    }

    public static Mat3 scale(float v) {
        return scale(v, v, v);
    }

    public static Mat3 scale(Vec3 v) {
        return scale(v.x, v.y, v.z);
    }

    public static Mat3 scale(float x, float y, float z) {
        return new Mat3(
                x, 0.0f, 0.0f,
                0.0f, y, 0.0f,
                0.0f, 0.0f, z
        );
    }

    public static Mat3 map(Vec3 x1, Vec3 y1, Vec3 z1, Vec3 x2, Vec3 y2, Vec3 z2) {
        Mat3 A = new Mat3(
                x1.x, x1.y, x1.z,
                y1.x, y1.y, y1.z,
                z1.z, z1.y, z1.z
        );
        Mat3 B = new Mat3(
                x2.x, x2.y, x2.z,
                y2.x, y2.y, y2.z,
                z2.x, z2.y, z2.z
        );

        return new Mat3(B.trans().mult(A));
    }

    public static Mat3 mapTo(Vec3 x, Vec3 y, Vec3 z) {
        return new Mat3(new Mat3(
                x.x, x.y, x.z,
                y.x, y.y, y.z,
                z.x, z.y, z.z
        ).trans());
    }

    public static Mat3 mapFrom(Vec3 x, Vec3 y, Vec3 z) {
        return new Mat3(x.x, x.y, x.z,
                y.x, y.y, y.z,
                z.x, z.y, z.z
        );
    }

}
