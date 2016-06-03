package qps;

import qps.utils.Vec3;

/**
 * @since 6/1/2016
 */
public interface CardinalListener {

    void move(int id, Vec3 delta);

    void round(int id);

    void rotate(int id, Vec3 axis, float theta);

}
