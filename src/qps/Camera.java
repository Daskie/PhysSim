package qps;

import qps.utils.Utils;
import qps.utils.Vec3;

/**
 * @since 5/21/2016
 */
public class Camera {

    private static final float MIN_CAM_DISTANCE = 1.0f;

    private Vec3 tetherLoc;
    private float camDistance;
    private float camRange;
    private float tetherRange;
    private float theta;
    private float phi;

    public Camera() {
        tetherLoc = new Vec3();
        camDistance = 10.0f;
        camRange = Float.POSITIVE_INFINITY;
        tetherRange = Float.POSITIVE_INFINITY;
        theta = -(float)Math.PI / 2.0f;
        phi = (float)Math.PI / 4.0f;
    }

    public Camera(Vec3 tetherLoc, float camDistance, float camRange, float tetherRange) {
        this.tetherLoc = tetherLoc;
        this.camDistance = Utils.max(camDistance, MIN_CAM_DISTANCE);
        this.camRange = camRange;
        this.tetherRange = tetherRange;
        theta = (float)Math.PI / 4.0f;
        phi = (float)Math.PI / 4.0f;
    }

    void translate(Vec3 delta) {
        Vec3 newLoc = tetherLoc.add(delta);
        if (newLoc.mag2() > tetherRange * tetherRange) {
            newLoc = newLoc.norm().mult(tetherRange);
        }
        tetherLoc = newLoc;
    }

    void yaw(float theta) {
        this.theta += theta;
    }

    void pitch(float phi) {
        this.phi = Utils.clamp(this.phi + phi, -(float)Math.PI / 2.0f, (float)Math.PI / 2.0f);
    }

    void move(float delta) {
        camDistance = Utils.max(Utils.min(camDistance + delta, camRange), MIN_CAM_DISTANCE);
    }

    public Vec3 loc() {
        return forward().mult(-camDistance).add(tetherLoc);
    }

    public Vec3 forward() {
        return Utils.sphericalToCartesian(1.0f, theta + (float)Math.PI, (float)Math.PI / 2.0f + phi);
    }

    public Vec3 up() {
        return Utils.sphericalToCartesian(1.0f, theta + (float)Math.PI, phi);
    }

    public Vec3 right() {
        return Utils.sphericalToCartesian(1.0f, theta + (float)Math.PI / 2, (float)Math.PI / 2.0f);
    }

    public float distance() {
        return camDistance;
    }

    public void lookAt(Vec3 p) {
        translate(p.sub(tetherLoc));
        Vec3 sCoords = Utils.cartesianToSpherical(loc().sub(p));
        theta = sCoords.y;
        phi = (float)Math.PI / 2.0f - sCoords.z;
    }

}
