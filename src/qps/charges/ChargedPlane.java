package qps.charges;

import qps.Vec3;

/**
 * @since 6/2/2016
 */
public class ChargedPlane extends ChargedObject {

    public ChargedPlane(float charge, Vec3 loc) {
        super(charge, loc, new Vec3(1000.0f, 1000.0f, 1000.0f));
    }

}
