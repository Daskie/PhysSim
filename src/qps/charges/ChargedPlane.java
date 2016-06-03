package qps.charges;

import qps.utils.Vec3;
import qps.utils.Vec4;

/**
 * @since 6/2/2016
 */
public class ChargedPlane extends ChargedObject {

    public ChargedPlane(float charge, Vec3 loc) {
        super(charge, loc, Vec3.POSZ, Vec3.NEGY, new Vec3(1000.0f, 1000.0f, 1000.0f));
    }

    public Vec4 getVec() {
        Vec3 forward = getForward();
        Vec3 loc = getLoc();
        return new Vec4(forward.x, forward.y, forward.z, forward.dot(loc));
    }

}
