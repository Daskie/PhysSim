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
        this(width, height, true);
    }

    public QMatN(int width, int height, boolean diagInit) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Matrix cannot have dimensions less than 1!");
        }

        this.width = width;
        this.height = height;
        mat = new float[width][height];

        if (diagInit) {
            setDiag(1);
        }
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

    public QMatN(int width, int height, float... vs) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Matrix cannot have dimensions less than 1!");
        }

        this.width = width;
        this.height = height;
        mat = new float[width][height];

        int i;
        outerloop:
        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                i = ci * height + ri;
                if (i >= vs.length) {
                    break outerloop;
                }

                mat[ci][ri] = vs[i];
            }
        }
    }

    public QMatN add(float v) {
        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] + v;
            }
        }

        return temp;
    }

    public QMatN sub(float v) {
        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] - v;
            }
        }

        return temp;
    }

    public QMatN mult(float v) {
        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] * v;
            }
        }

        return temp;
    }

    public QMatN div(float v) {
        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] / v;
            }
        }

        return temp;
    }

    public QMatN add(QMatN m) {
        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] + m.mat[ci][ri];
            }
        }

        return temp;
    }

    public QMatN sub(QMatN m) {
        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] - m.mat[ci][ri];
            }
        }

        return temp;
    }

    public QMatN mult(QMatN m) {
        if (m.height != width) {
            throw new IllegalArgumentException("Cannot multiply two matrices w/ invalid width/height!");
        }

        QMatN temp = new QMatN(width, m.width, false);

        for (int ci = 0; ci < temp.width; ++ci) {
            for (int ri = 0; ri < temp.height; ++ri) {
                for (int i = 0; i < temp.width; ++i) {
                    temp.mat[ci][ri] += m.mat[ci][i] * mat[i][ri];
                }
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
        for (int i = 0; i < width && i < height; ++i) {
            mat[i][i] = v;
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

    //create a submatrix from the given rows and columns
    public QMatN subMatInc(int[] cols, int[] rows) {
        QMatN temp = new QMatN(cols.length, rows.length);

        for (int ci = 0; ci < temp.width; ++ci) {
            for (int ri = 0; ri < temp.height; ++ri) {
                temp.mat[ci][ri] = mat[cols[ci]][rows[ri]];
            }
        }

        return temp;
    }

    //create a submatrix from all but the given row and column
    public QMatN subMatExc(int col, int row) {
        QMatN temp = new QMatN(width - 1, height - 1);

        for (int sci = 0, dci = 0; sci < width; ++sci) {
            if (sci == col) continue;

            for (int sri = 0, dri = 0; sri < height; ++sri) {
                if (sri == row) continue;

                temp.mat[dci][dri] = mat[sci][sri];

                ++dri;
            }

            ++dci;
        }

        return temp;
    }

    public float det() {
        if (width != height) {
            throw new IllegalArgumentException("Cannot find determinant of non-square matrix!");
        }

        if (width == 1) {
            return mat[0][0];
        }

        if (width == 2) {
            return mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];
        }

        float det = 0;

        for (int ci = 0; ci < width; ++ci) {
                det += mat[ci][0] * subMatExc(ci, 0).det() * (ci % 2 == 0 ? 1 : -1);
        }

        return det;
    }

    public QMatN trans() {
        QMatN temp = new QMatN(height, width);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ri][ci] = mat[ci][ri];
            }
        }

        return temp;
    }

    public QMatN cof() {
        if (width != height) {
            throw new IllegalArgumentException("Cannot find cofactor of non-square matrix!");
        }

        QMatN temp = new QMatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = subMatExc(ci, ri).det();
                if ((ci + ri) % 2 != 0) {
                    temp.mat[ci][ri] *= -1;
                }
            }
        }

        return temp;
    }

    public QMatN adj() {
        return cof().trans();
    }

    public QMatN inv() {
        return adj().div(det());
    }

    public int byteSize() {
        return width * height * 4;
    }

    public float get(int ci, int ri) {
        return mat[ci][ri];
    }

    public void set(int ci, int ri, float v) {
        mat[ci][ri] = v;
    }
}
