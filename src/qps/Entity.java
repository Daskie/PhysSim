package qps;

/**
 * @since 5/21/2016
 */
public class Entity {

    public static final Vec3 DEFAULT_LOC = new Vec3();
    public static final Vec3 DEFAULT_FORWARD = Vec3.POSY;
    public static final Vec3 DEFAULT_UP = Vec3.POSZ;
    public static final float DEFAULT_SCALE = 1.0f;

    protected Vec3 loc;
    protected Vec3 baseForward;
    protected Vec3 baseUp;
    protected Vec3 forward;
    protected Vec3 up;
    protected float scale;

    public Entity() {
        this(DEFAULT_LOC, DEFAULT_FORWARD, DEFAULT_UP);
    }
    public Entity(Vec3 loc) {
        this(loc, DEFAULT_FORWARD, DEFAULT_UP);
    }
    public Entity(Vec3 loc, float scale) {
        this(loc, DEFAULT_FORWARD, DEFAULT_UP, scale);
    }
    public Entity(Vec3 loc, Vec3 baseForward, Vec3 baseUp) {
        this(loc, baseForward, baseUp, DEFAULT_SCALE);
    }
    public Entity(Vec3 loc, Vec3 baseForward, Vec3 baseUp, float scale) {
        this.loc = new Vec3(loc);
        this.baseForward = new Vec3(baseForward);
        this.baseUp = new Vec3(baseUp);
        forward = this.baseForward;
        up = this.baseUp;
        this.scale = scale;
    }

    public Vec3 getLoc() {
        return new Vec3(loc);
    }

    public void setLoc(Vec3 loc) {
        this.loc = new Vec3(loc);
    }

    public void translate(Vec3 delta) {
        loc = loc.add(delta);
    }

    public Vec3 getForward() {
        return new Vec3(forward);
    }

    public Vec3 getUp() {
        return new Vec3(up);
    }

    public Vec3 getRight() {
        return forward.cross(up);
    }

    public void orient(Entity o) {
        forward = new Vec3(o.forward);
        up = new Vec3(o.up);
    }

    public void orient(Vec3 forward, Vec3 up) {
        this.forward = forward;
        this.up = up;
    }

    public void rotate(Quaternion q) {
        forward = q.rotate(forward);
        up = q.rotate(up);
    }

    public void rotate(Mat3 mat) {
        forward = mat.mult(forward);
        up = mat.mult(up);
    }

    public void rotateAbs(Quaternion q) {
        forward = q.rotate(baseForward);
        up = q.rotate(baseUp);
    }

    public void rotateAbs(Mat3 mat) {
        forward = mat.mult(baseForward);
        up = mat.mult(baseUp);
    }

    public void resetRotation() {
        forward = new Vec3(baseForward);
        up = new Vec3(baseUp);
    }

    public void scale(float scale) {
        this.scale = scale;
    }

    public Mat3 alignToBaseMat() {
        return Mat3.align(forward, up, baseForward, baseUp);
    }

    public Quaternion alignToBaseQuat() {
        return Quaternion.align(forward, up, baseForward, baseUp);
    }

    public Mat3 alignFromBaseMat() {
        return Mat3.align(baseForward, baseUp, forward, up);
    }

    public Quaternion alignFromBaseQuat() {
        return Quaternion.align(baseForward, baseUp, forward, up);
    }

    public Mat4 modelMat() {
        return new Mat4(Mat4.translate(loc).mult(new Mat4(alignFromBaseMat())).mult(new Mat4(Mat3.scale(scale))));
    }

}
