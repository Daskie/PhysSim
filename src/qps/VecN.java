package qps;

/**
 * @since 5/17/2016
 */
public class VecN {

    private int n;
    private float vec[];

    public VecN(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Vector cannot have less than 1 element!");
        }

        this.n = n;
        vec = new float[n];
    }

    public VecN(VecN o) {
        this(o.n);
        for (int i = 0; i < n; ++i) {
            vec[i] = o.vec[i];
        }
    }

    public VecN(float... vs) {
        this(vs.length);
        for (int i = 0; i < n; ++i) {
            vec[i] = vs[i];
        }
    }

    public VecN add(float v) {
        VecN temp = new VecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] + v;
        }
        return temp;
    }

    public VecN sub(float v) {
        VecN temp = new VecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] - v;
        }
        return temp;
    }

    public VecN mult(float v) {
        VecN temp = new VecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] * v;
        }
        return temp;
    }

    public VecN div(float v) {
        VecN temp = new VecN(n);
        for (int i = 0; i < n; ++i) {
            temp.vec[i] = vec[i] / v;
        }
        return temp;
    }

    public VecN add(VecN v) {
        VecN temp = new VecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] + v.vec[i];
        }
        return temp;
    }

    public VecN sub(VecN v) {
        VecN temp = new VecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] - v.vec[i];
        }
        return temp;
    }

    public VecN mult(VecN v) {
        VecN temp = new VecN(this);
        for (int i = 0; i < n && i < v.n; ++i) {
            temp.vec[i] = vec[i] * v.vec[i];
        }
        return temp;
    }

    public VecN div(VecN v) {
        VecN temp = new VecN(this);
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

    public VecN norm() {
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
        if (o == null || !o.getClass().equals(getClass()) || n != ((VecN)o).n) {
            return false;
        }

        for (int i = 0; i < n; ++i) {
            if (vec[i] != ((VecN)o).vec[i]) {
                return false;
            }
        }

        return true;
    }

}
