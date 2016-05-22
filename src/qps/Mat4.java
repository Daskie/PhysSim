package qps;

/**
 * @since 5/17/2016
 */
public class Mat4 extends MatN {

    public Mat4() {
        super(4, 4);
    }

    public static Mat4 translation(Vec3 delta) {
        Mat4 mat = new Mat4();
        mat.mat[3][0] = delta.x;
        mat.mat[3][1] = delta.y;
        mat.mat[3][2] = delta.z;
        return mat;
    }

}
