/**
 * @since 5/17/2016
 */
public class QVec2 extends QVec {

    public float x, y;

    public QVec2() {
        this(0, 0);
    }

    public QVec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public QVec2(QVec2 o) {
        this(o.x, o.y);
    }

    public QVec2(QVec3 o) {
        this(o.x, o.y);
    }

    public QVec2(QVec4 o) {
        this(o.x, o.y);
    }

    public QVec2 add(float v) {
        return new QVec2(x + v, y + v);
    }

    public QVec2 sub(float v) {
        return new QVec2(x - v, y - v);
    }

    public QVec2 mult(float v) {
        return new QVec2(x * v, y * v);
    }

    public QVec2 div(float v) {
        return new QVec2(x / v, y / v);
    }

    public QVec2 add(QVec2 v) {
        return new QVec2(x + v.x, y + v.y);
    }

    public QVec2 sub(QVec2 v) {
        return new QVec2(x - v.x, y - v.y);
    }

    public QVec2 mult(QVec2 v) {
        return new QVec2(x * v.x, y * v.y);
    }

    public QVec2 div(QVec2 v) {
        return new QVec2(x / v.x, y / v.y);
    }

    public float mag2() {
        return x * x + y * y;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public QVec2 norm() {
        return div(mag());
    }

    public float dot(QVec2 v) {
        return x * v.x + y * v.y;
    }

    public float angle(QVec2 v) {
        return (float)Math.acos(norm().dot(v.norm()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass())) {
            return false;
        }

        return x == ((QVec2)o).x && y == ((QVec2)o).y;
    }

}
