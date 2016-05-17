/**
 * @since 5/17/2016
 */
public class QVec3 {

    public float x, y, z;

    public QVec3() {

    }

    public QVec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public QVec3(QVec3 o) {
        this(o.x, o.y, o.z);
    }

    public QVec3(QVec2 o) {
        this(o.x, o.y, 0);
    }

    public QVec3(QVec4 o) {
        this(o.x, o.y, o.z);
    }

    public QVec3 add(float v) {
        return new QVec3(x + v, y + v, z + v);
    }

    public QVec3 sub(float v) {
        return new QVec3(x - v, y - v, z - v);
    }

    public QVec3 mult(float v) {
        return new QVec3(x * v, y * v, z * v);
    }

    public QVec3 div(float v) {
        return new QVec3(x / v, y / v, z / v);
    }

    public QVec3 add(QVec3 v) {
        return new QVec3(x + v.x, y + v.y, z + v.z);
    }

    public QVec3 sub(QVec3 v) {
        return new QVec3(x - v.x, y - v.y, z - v.z);
    }

    public QVec3 mult(QVec3 v) {
        return new QVec3(x * v.x, y * v.y, z * v.z);
    }

    public QVec3 div(QVec3 v) {
        return new QVec3(x / v.x, y / v.y, z / v.z);
    }

    public float mag2() {
        return x * x + y * y + z * z;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public QVec3 norm() {
        return div(mag());
    }

    public float dot(QVec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public QVec3 cross(QVec3 v) {
        return new QVec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public float angle(QVec3 v) {
        return (float)Math.acos(norm().dot(v.norm()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        return x == ((QVec3)o).x && y == ((QVec3)o).y && z == ((QVec3)o).z;
    }

}
