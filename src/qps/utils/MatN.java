package qps.utils;

import java.nio.ByteBuffer;

/**
 * Column-major ordering
 * x1 x2 x3		00 03 06
 * y1 y2 y3		01 04 07
 * z1 z2 z3		02 05 08
 */

public class MatN {

    private int width, height;
    public float[][] mat;

    public MatN(int width, int height) {
        this(width, height, true);
    }

    public MatN(int width, int height, boolean initDiag) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Matrix cannot have dimensions less than 1!");
        }

        this.width = width;
        this.height = height;
        mat = new float[width][height];

        if (initDiag) {
            setDiag(1);
        }
    }

    public MatN(MatN o) {
        this.width = o.width;
        this.height = o.height;
        mat = new float[width][height];

        for (int ci = 0; ci < width; ++ci) {
            for (int r = 0; r < height; ++r) {
                mat[ci][r] = o.mat[ci][r];
            }
        }
    }

    public MatN(int width, int height, float... vs) {
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

    public MatN(int width, int height, MatN o) {
        this(width, height);

        for (int c = 0; c < width && c < o.width; ++c) {
            for (int r = 0; r < height && r < o.height; ++r) {
                mat[c][r] = o.mat[c][r];
            }
        }
    }

    public MatN add(float v) {
        MatN temp = new MatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] + v;
            }
        }

        return temp;
    }

    public MatN sub(float v) {
        MatN temp = new MatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] - v;
            }
        }

        return temp;
    }

    public MatN mult(float v) {
        MatN temp = new MatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] * v;
            }
        }

        return temp;
    }

    public MatN div(float v) {
        MatN temp = new MatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] / v;
            }
        }

        return temp;
    }

    public MatN add(MatN m) {
        MatN temp = new MatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] + m.mat[ci][ri];
            }
        }

        return temp;
    }

    public MatN sub(MatN m) {
        MatN temp = new MatN(width, height);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ci][ri] = mat[ci][ri] - m.mat[ci][ri];
            }
        }

        return temp;
    }

    public MatN mult(MatN m) {
        if (m.height != width) {
            throw new IllegalArgumentException("Cannot multiply two matrices w/ invalid width/height!");
        }

        MatN temp = new MatN(width, m.width, false);

        for (int ci = 0; ci < temp.width; ++ci) {
            for (int ri = 0; ri < temp.height; ++ri) {
                for (int i = 0; i < temp.width; ++i) {
                    temp.mat[ci][ri] += m.mat[ci][i] * mat[i][ri];
                }
            }
        }

        return temp;
    }

    public VecN mult(VecN v) {
        if (v.n() != width) {
            throw new IllegalArgumentException("Cannot multiply a vector and matrix with invalid length/width!");
        }

        VecN temp = new VecN(height);

        for (int ri = 0; ri < height; ++ri) {
            float dot = 0;
            for (int ci = 0; ci < width; ++ci) {
                dot += mat[ci][ri] * v.get(ci);
            }
            temp.set(ri, dot);
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

    public VecN col(int ci) {
        return new VecN(mat[0]);
    }

    public VecN row(int ri) {
        VecN temp = new VecN(width);

        for (int ci = 0; ci < ri; ++ci) {
            temp.set(ci, mat[ci][ri]);
        }

        return temp;
    }

    //create a submatrix from the given rows and columns
    public MatN subMatInc(int[] cols, int[] rows) {
        MatN temp = new MatN(cols.length, rows.length);

        for (int ci = 0; ci < temp.width; ++ci) {
            for (int ri = 0; ri < temp.height; ++ri) {
                temp.mat[ci][ri] = mat[cols[ci]][rows[ri]];
            }
        }

        return temp;
    }

    //create a submatrix from all but the given row and column
    public MatN subMatExc(int col, int row) {
        MatN temp = new MatN(width - 1, height - 1);

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

    public MatN trans() {
        MatN temp = new MatN(height, width);

        for (int ci = 0; ci < width; ++ci) {
            for (int ri = 0; ri < height; ++ri) {
                temp.mat[ri][ci] = mat[ci][ri];
            }
        }

        return temp;
    }

    public MatN cof() {
        if (width != height) {
            throw new IllegalArgumentException("Cannot find cofactor of non-square matrix!");
        }

        MatN temp = new MatN(width, height);

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

    public MatN adj() {
        return cof().trans();
    }

    public MatN inv() {
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

    public void buffer(ByteBuffer buffer) {
        for (int c = 0; c < width; ++c) {
            for (int r = 0; r < height; ++r) {
                buffer.putFloat(mat[c][r]);
            }
        }
    }

    public void buffer(ByteBuffer buffer, int i) {
        for (int c = 0; c < width; ++c) {
            for (int r = 0; r < height; ++r) {
                buffer.putFloat(i, mat[c][r]);
                i += 4;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(this.getClass())) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (((MatN)o).width != width || ((MatN)o).height != height) {
            return false;
        }

        for (int c = 0; c < width; ++c) {
            for (int r = 0; r < height; ++r) {
                if (((MatN)o).mat[c][r] != mat[c][r]) {
                    return false;
                }
            }
        }

        return true;
    }
}
