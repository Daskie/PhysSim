package qps;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * @since 5/17/2016
 */
public class Vec4 {

    public float x, y, z, w;

    public Vec4() {

    }

    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(Vec4 o) {
        this(o.x, o.y, o.z, o.w);
    }

    public Vec4(Vec2 o) {
        this(o.x, o.y, 0, 0);
    }

    public Vec4(Vec3 o) {
        this(o.x, o.y, o.z, 0);
    }

    public Vec4 add(float v) {
        return new Vec4(x + v, y + v, z + v, w + v);
    }

    public Vec4 sub(float v) {
        return new Vec4(x - v, y - v, z - v, w - v);
    }

    public Vec4 mult(float v) {
        return new Vec4(x * v, y * v, z * v, w * v);
    }

    public Vec4 div(float v) {
        return new Vec4(x / v, y / v, z / v, w / v);
    }

    public Vec4 add(Vec4 v) {
        return new Vec4(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public Vec4 sub(Vec4 v) {
        return new Vec4(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public Vec4 mult(Vec4 v) {
        return new Vec4(x * v.x, y * v.y, z * v.z, w * v.w);
    }

    public Vec4 div(Vec4 v) {
        return new Vec4(x / v.x, y / v.y, z / v.z, w / v.w);
    }

    public float mag2() {
        return x * x + y * y + z * z + w * w;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public Vec4 norm() {
        return div(mag());
    }

    public void buffer(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
    }

    public void buffer(ByteBuffer buffer, int i) {
        buffer.putFloat(i, x);
        buffer.putFloat(i + 4, y);
        buffer.putFloat(i + 8, z);
        buffer.putFloat(i + 12, w);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        return x == ((Vec4)o).x && y == ((Vec4)o).y && z == ((Vec4)o).z && w == ((Vec4)o).w;
    }

}
