package qps.charges;

import qps.utils.Vec3;

/**
 * @since 6/2/2016
 */
public class ChargedLine extends ChargedObject {

    public ChargedLine(float charge, Vec3 loc) {
        super(charge, loc, Vec3.POSZ, Vec3.NEGY, new Vec3(0.1f, 0.1f, 1000.0f));
    }

}
