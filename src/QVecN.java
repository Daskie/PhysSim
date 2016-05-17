/**
 * @since 5/17/2016
 */
public class QVecN {

    private int n;
    private float vec[];

    public QVecN(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Vector cannot have less than 1 element!");
        }

        this.n = n;
        vec = new float[n];
    }

    public QVecN(QVecN o) {
        this(o.n);
        for (int i = 0; i < n; ++i) {
            vec[i] = o.vec[i];
        }
    }

    public QVecN(float... vs) {
        this(vs.length);
        for (int i = 0; i < n; ++i) {
            vec[i] = vs[i];
        }
    }

    public QVecN add(float v) {
        QVecN temp = new QVecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] + v;
        }
        return temp;
    }

    public QVecN sub(float v) {
        QVecN temp = new QVecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] - v;
        }
        return temp;
    }

    public QVecN mult(float v) {
        QVecN temp = new QVecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] * v;
        }
        return temp;
    }

    public QVecN div(float v) {
        QVecN temp = new QVecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] / v;
        }
        return temp;
    }

    public QVecN add(QVecN v) {
        QVecN temp = new QVecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] + v.vec[i];
        }
        return temp;
    }

    public QVecN sub(QVecN v) {
        QVecN temp = new QVecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] - v.vec[i];
        }
        return temp;
    }

    public QVecN mult(QVecN v) {
        QVecN temp = new QVecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] * v.vec[i];
        }
        return temp;
    }

    public QVecN div(QVecN v) {
        QVecN temp = new QVecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] / v.vec[i];
        }
        return temp;
    }

    public float mag2() {
        float mag = 0;
        for (int i = 0; i < n; ++i) {
            mag += vec[i] * vec[i];
        }
        return mag;
    }

    public float mag() {
        return (float)Math.sqrt(mag2());
    }

    public QVecN norm() {
        return div(mag());
    }

    public int n() {
        return n;
    }

    public float get(int i) {
        return vec[i];
    }

    public void set(int i, float v) {
        vec[i] = v;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(getClass()) || n != ((QVecN)o).n) {
            return false;
        }

        for (int i = 0; i < n; ++i) {
            if (vec[i] != ((QVecN)o).vec[i]) {
                return false;
            }
        }

        return true;
    }

}
