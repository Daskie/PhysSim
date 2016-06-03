package qps.utils;

import java.nio.ByteBuffer;

/**
 * @since 5/17/2016
 */
public class Vec2 {

    public float x, y;

    public Vec2() {

    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 o) {
        this(o.x, o.y);
    }

    public Vec2(Vec3 o) {
        this(o.x, o.y);
    }

    public Vec2(Vec4 o) {
        this(o.x, o.y);
    }

    public Vec2 add(float v) {
        return new Vec2(x + v, y + v);
    }

    public Vec2 sub(float v) {
        return new Vec2(x - v, y - v);
    }

    public Vec2 mult(float v) {
        return new Vec2(x * v, y * v);
    }

    public Vec2 div(float v) {
        return new Vec2(x / v, y / v);
    }

    public Vec2 add(Vec2 v) {
        return new Vec2(x + v.x, y + v.y);
    }

    public Vec2 sub(Vec2 v) {
        return new Vec2(x - v.x, y - v.y);
    }

    public Vec2 mult(Vec2 v) {
        return new Vec2(x * v.x, y * v.y);
    }

    public Vec2 div(Vec2 v) {
        return new Vec2(x / v.x, y / v.y);
    }

    public float mag2() {
        return x * x + y * y;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public Vec2 norm() {
        return div(mag());
    }

    public float dot(Vec2 v) {
        return x * v.x + y * v.y;
    }

    public float angle(Vec2 v) {
        return (float)Math.acos(norm().dot(v.norm()));
    }

    public void buffer(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
    }

    public void buffer(ByteBuffer buffer, int i) {
        buffer.putFloat(i, x);
        buffer.putFloat(i + 4, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        return x == ((Vec2)o).x && y == ((Vec2)o).y;
    }

}
