package qps;

/**
 * @since 6/2/2016
 */
public class ChargedSphere extends ChargedObject {
    public ChargedSphere(float charge) {
        super(charge);
    }

    public ChargedSphere(float charge, Vec3 loc) {
        super(charge, loc);
    }
}
