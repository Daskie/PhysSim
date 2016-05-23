package qps;

/**
 * @since 5/21/2016
 */
public class Inerital {

    private double pos;
    private double vel;
    private double acc;

    private double minPos = Double.NEGATIVE_INFINITY, maxPos = Double.POSITIVE_INFINITY;
    private double minVel = Double.NEGATIVE_INFINITY, maxVel = Double.POSITIVE_INFINITY;
    private double minAcc = Double.NEGATIVE_INFINITY, maxAcc = Double.POSITIVE_INFINITY;

    private double mass = 1.0;

    double update(double dt) {
        vel = Utils.clamp(vel + acc * dt, minVel, maxVel);
        pos = Utils.clamp(pos + vel * dt, minPos, maxPos);

        return pos;
    }

    private double getMass() {
        return mass;
    }

    private void setMass(double mass) {
        this.mass = mass;
    }

    public double getPos() {
        return pos;
    }

    public double getVel() {
        return vel;
    }

    public double getAcc() {
        return acc;
    }

    public void setPos(double pos) {
        this.pos = Utils.clamp(pos, minPos, maxPos);
    }

    public void setVel(double vel) {
        this.vel = Utils.clamp(vel, minVel, maxVel);
    }

    public void setAcc(double acc) {
        this.acc = Utils.clamp(acc, minAcc, maxAcc);
    }

    public void setMinPos(double minPos) {
        this.minPos = minPos;
    }

    public void setMaxPos(double maxPos) {
        this.maxPos = maxPos;
    }

    public void setMinVel(double minVel) {
        this.minVel = minVel;
    }

    public void setMaxVel(double maxVel) {
        this.maxVel = maxVel;
    }

    public void setMinAcc(double minAcc) {
        this.minAcc = minAcc;
    }

    public void setMaxAcc(double maxAcc) {
        this.maxAcc = maxAcc;
    }

}
