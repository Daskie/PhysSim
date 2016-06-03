package qps.charges;

import qps.Vec3;

/**
 * @since 6/2/2016
 */
public class ChargedLine extends ChargedObject {

    public ChargedLine(float charge, Vec3 loc) {
        super(charge, loc, new Vec3(0.1f, 0.1f, 1000.0f));
    }

}
