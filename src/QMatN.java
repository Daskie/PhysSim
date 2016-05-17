/**
 * Column-major ordering
 * x1 x2 x3		00 03 06
 * y1 y2 y3		01 04 07
 * z1 z2 z3		02 05 08
 */

public class QMatN {

    private int width, height;
    public float[][] mat;

    public QMatN(int width, int height) {
        this.width = width;
        this.height = height;
        mat = new float[width][height];
        setDiag(1);
    }

    public QMatN(QMatN o) {
        this.width = o.width;
        this.height = o.height;
        mat = new float[width][height];
        for (int ci = 0; ci < width; ++ci) {
            for (int r = 0; r < height; ++r) {
                mat[ci][r] = o.mat[ci][r];
            }
        }
    }

    public QVecN col(int ci) {
        return new QVecN(mat[0]);
    }

    public QVecN row(int ri) {
        QVecN temp = new QVecN(width);

        for (int ci = 0; ci < ri; ++ci) {
            temp.set(ci, mat[ci][ri]);
        }

        return temp;
    }

    public QMatN subMat(int[] cols, int[] rows) {
        QMatN temp = new QMatN(cols.length, rows.length);

        for (int ci = 0; ci < temp.width; ++ci) {
            for (int ri = 0; ri < temp.height; ++ri) {
                temp.mat[ci][ri] = mat[cols[ci]][rows[ri]];
            }
        }

        return temp;
    }

    public void fill(float v) {
        for (int ci = 0; ci < width; ++ci) {
            for (int r = 0; r < height; ++r) {
                mat[ci][r] = v;
            }
        }
    }

    public void setDiag(float v) {
        for (int i = 0; i < (width < height ? width : height); ++i) {
            mat[i][i] = v;
        }
    }
}
