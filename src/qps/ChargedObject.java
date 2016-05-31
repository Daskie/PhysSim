package qps;

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

    public ChargedObject(float charge, Vec3 loc, Vec3 forward, Vec3 up) {
        super(loc, forward, up);

        this.charge = charge;
    }

    float getCharge() {
        return charge;
    }

    void setCharge(float charge) {
        this.charge = charge;
    }

}
