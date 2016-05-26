package qps;

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

    public static Mat3 rotation(float theta, Vec3 axis) {
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

}
