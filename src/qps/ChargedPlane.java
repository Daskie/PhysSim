package qps;

/**
 * @since 6/2/2016
 */
public class ChargedPlane extends ChargedObject {
    public ChargedPlane(float charge) {
        super(charge);
    }

    public ChargedPlane(float charge, Vec3 loc) {
        super(charge, loc);
    }

    public ChargedPlane(float charge, Vec3 loc, Vec3 forward, Vec3 up) {
        super(charge, loc, forward, up);
    }
}
