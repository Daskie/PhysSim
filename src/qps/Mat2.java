package qps;

/**
 * @since 5/23/2016
 */
public class Mat2 extends MatN {

    public Mat2() {
        super(2, 2);
    }

    public Mat2(Mat2 o) {
        super(o);
    }

    public Mat2(boolean initDiag) {
        super(3, 3, initDiag);
    }

    public Mat2(float... vs) {
        super(2, 2, vs);
    }

    public Mat2(MatN o) {
        super(2, 2, o);
    }

    public static Mat2 rotation(float theta) {
        float s = (float)Math.sin(theta);
        float c = (float)Math.cos(theta);

        return new Mat2(
                c, s,
                -s, c
        );
    }
}
