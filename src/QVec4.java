/**
 * @since 5/17/2016
 */
public class QVec4 extends QVec {

    public float x, y, z, w;

    public QVec4() {
        this(0, 0, 0, 0);
    }

    public QVec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public QVec4(QVec4 o) {
        this(o.x, o.y, o.z, o.w);
    }

    public QVec4(QVec2 o) {
        this(o.x, o.y, 0, 0);
    }

    public QVec4(QVec3 o) {
        this(o.x, o.y, o.z, 0);
    }

    public QVec4 add(float v) {
        return new QVec4(x + v, y + v, z + v, w + v);
    }

    public QVec4 sub(float v) {
        return new QVec4(x - v, y - v, z - v, w - v);
    }

    public QVec4 mult(float v) {
        return new QVec4(x * v, y * v, z * v, w * v);
    }

    public QVec4 div(float v) {
        return new QVec4(x / v, y / v, z / v, w / v);
    }

    public QVec4 add(QVec4 v) {
        return new QVec4(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public QVec4 sub(QVec4 v) {
        return new QVec4(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public QVec4 mult(QVec4 v) {
        return new QVec4(x * v.x, y * v.y, z * v.z, w * v.w);
    }

    public QVec4 div(QVec4 v) {
        return new QVec4(x / v.x, y / v.y, z / v.z, w / v.w);
    }

    public float mag2() {
        return x * x + y * y + z * z + w * w;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public QVec4 norm() {
        return div(mag());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        return x == ((QVec4)o).x && y == ((QVec4)o).y && z == ((QVec4)o).z && w == ((QVec4)o).w;
    }

}
