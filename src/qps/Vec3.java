package qps;

/**
 * @since 5/17/2016
 */
public class Vec3 {

    public static final Vec3 POSX = new Vec3(1.0f, 0.0f, 0.0f);
    public static final Vec3 POSY = new Vec3(0.0f, 1.0f, 0.0f);
    public static final Vec3 POSZ = new Vec3(0.0f, 0.0f, 1.0f);
    public static final Vec3 NEGX = new Vec3(-1.0f, 0.0f, 0.0f);
    public static final Vec3 NEGY = new Vec3(0.0f, -1.0f, 0.0f);
    public static final Vec3 NEGZ = new Vec3(0.0f, 0.0f, -1.0f);

    public float x, y, z;

    public Vec3() {

    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 o) {
        this(o.x, o.y, o.z);
    }

    public Vec3(Vec2 o) {
        this(o.x, o.y, 0);
    }

    public Vec3(Vec4 o) {
        this(o.x, o.y, o.z);
    }

    public Vec3 add(float v) {
        return new Vec3(x + v, y + v, z + v);
    }

    public Vec3 sub(float v) {
        return new Vec3(x - v, y - v, z - v);
    }

    public Vec3 mult(float v) {
        return new Vec3(x * v, y * v, z * v);
    }

    public Vec3 div(float v) {
        return new Vec3(x / v, y / v, z / v);
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 sub(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 mult(Vec3 v) {
        return new Vec3(x * v.x, y * v.y, z * v.z);
    }

    public Vec3 div(Vec3 v) {
        return new Vec3(x / v.x, y / v.y, z / v.z);
    }

    public float mag2() {
        return x * x + y * y + z * z;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public Vec3 norm() {
        return div(mag());
    }

    public float dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public float angle(Vec3 v) {
        return (float)Math.acos(norm().dot(v.norm()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        return x == ((Vec3)o).x && y == ((Vec3)o).y && z == ((Vec3)o).z;
    }

}
