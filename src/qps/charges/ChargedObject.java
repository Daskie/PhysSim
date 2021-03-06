package qps.charges;

import qps.Entity;
import qps.utils.Vec3;

/**
 * @since 5/28/2016
 */
public class ChargedObject extends Entity {

    protected float charge;

    public ChargedObject(float charge) {
        this.charge = charge;
    }

    public ChargedObject(float charge, Vec3 loc) {
        super(loc);

        this.charge = charge;
    }

    public ChargedObject(float charge, Vec3 loc, Vec3 scale) {
        super(loc, scale);

        this.charge = charge;
    }

    public ChargedObject(float charge, Vec3 loc, Vec3 forward, Vec3 up) {
        super(loc, forward, up);

        this.charge = charge;
    }

    public ChargedObject(float charge, Vec3 loc, Vec3 forward, Vec3 up, Vec3 scale) {
        super(loc, forward, up, scale);

        this.charge = charge;
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

}
