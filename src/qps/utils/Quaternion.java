package qps.utils;

/**
 * @since 5/23/2016
 */
public class Quaternion {

    private float w;
    private Vec3 v;

    public Quaternion() {
        w = 1.0f;
        v = new Vec3();
    }

    public Quaternion(float w, Vec3 v) {
        this.w = w;
        this.v = new Vec3(v);
    }

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        v = new Vec3(x, y, z);
    }

    public Quaternion(Quaternion o) {
        w = o.w;
        v = new Vec3(o.v);
    }

    public Quaternion(Vec3 v) {
        w = 0.0f;
        this.v = new Vec3(v);
    }

    public Quaternion add(Quaternion o) {
        return new Quaternion(
                w + o.w,
                v.add(o.v)
        );
    }

    public Quaternion sub(Quaternion o) {
        return new Quaternion(
                w - o.w,
                v.sub(o.v)
        );
    }

    public Quaternion mult(Quaternion o) {
        return new Quaternion(
                w * o.w - v.dot(o.v),
                o.v.mult(w).add(v.mult(o.w)).add(v.cross(o.v))
        );
    }

    public Vec3 rotate(Vec3 v) {
        Vec3 t = this.v.cross(v).mult(2.0f);
        return v.add(t.mult(w)).add(this.v.cross(t));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(this.getClass())) {
            return false;
        }
        if (o == this) {
            return true;
        }

        return w == ((Quaternion)o).w && v.equals(((Quaternion)o).v);
    }

    public float getW() {
        return w;
    }

    public Vec3 getV() {
        return new Vec3(v);
    }

    public float mag() {
        return (float)Math.sqrt(w * w + v.mag2());
    }

    public Quaternion norm() {
        float m = mag();
        return new Quaternion(
                w / m,
                v.div(m)
        );
    }

    public static Quaternion angleAxis(float theta, Vec3 axis) {
        return new Quaternion(
                (float)Math.cos(theta / 2.0f),
                axis.norm().mult((float)Math.sin(theta / 2.0f))
        );
    }

    public static Quaternion align(Vec3 v1, Vec3 v2) {
        v1 = v1.norm();
        v2 = v2.norm();

        Vec3 axis = v1.cross(v2);
        float theta = (float)Math.acos(v1.dot(v2));

        return angleAxis(theta, axis);
    }

    public static Quaternion align(Vec3 forward1, Vec3 up1, Vec3 forward2, Vec3 up2) {
        Quaternion q = align(forward1, forward2);
        return align(q.rotate(up1), up2).mult(q);
    }

    public Mat3 toRotMat() {
        float wx = w * v.x;
        float wy = w * v.y;
        float wz = w * v.z;
        float xx = v.x * v.x;
        float xy = v.x * v.y;
        float xz = v.x * v.z;
        float yy = v.y * v.y;
        float yz = v.y * v.z;
        float zz = v.z * v.z;

        return new Mat3(
                1.0f - 2.0f * (yy + zz), 2.0f * (xy + wz), 2.0f * (xz - wy),
                2.0f * (xy - wz), 1.0f - 2.0f * (xx + zz), 2.0f * (yz + wx),
                2.0f * (xz + wy), 2.0f * (yz - wx), 1.0f - 2.0f * (xx + yy)
        );
    }

}
