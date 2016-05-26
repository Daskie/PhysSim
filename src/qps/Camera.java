package qps;

/**
 * @since 5/21/2016
 */
public class Camera {

    private Vec3 loc;
    private float theta, phi;

    public Camera() {
        loc = new Vec3(0.0f, 0.0f, 0.0f);
        theta = 0.0f;
        phi = 0.0f;
    }

    void translate(Vec3 delta) {
        loc = loc.add(delta);
    }

    void yaw(float theta) {
        this.theta += theta;
    }

    void pitch(float phi) {
        this.phi = Utils.clamp(this.phi + phi, -(float)Math.PI / 2.0f, (float)Math.PI / 2.0f);
    }

    public Vec3 loc() {
        return new Vec3(loc);
    }

    public Vec3 forward() {
        return Utils.sphericalToCartesian(1.0f, theta + (float)Math.PI / 2.0f, (float)Math.PI / 2.0f - phi);
    }

    public Vec3 up() {
        return Utils.sphericalToCartesian(1.0f, theta + (float)Math.PI / 2.0f, -phi);
    }

    public Vec3 right() {
        return Utils.sphericalToCartesian(1.0f, theta, (float)Math.PI / 2.0f);
    }


}
