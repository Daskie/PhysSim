package qps;

/**
 * @since 5/21/2016
 */
public class Entity {

    protected Vec3 loc;
    protected Vec3 forward;
    protected Vec3 up;
    private Mesh mesh;
    private VAO vao;

    public Entity(Mesh mesh, VAO vao) {
        this.mesh = mesh;
        this.vao = vao;

        this.loc = new Vec3();
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

    public void rotate(Quaternion q) {
        forward = q.rotate(forward);
        up = q.rotate(up);
    }

    public void rotate(Mat3 mat) {
        forward = mat.mult(forward);
        up = mat.mult(up);
    }

    public boolean isCorporeal() {
        return mesh != null;
    }

    public boolean isBuffered() {
        return vao != null;
    }

    public Mesh mesh() {
        return mesh;
    }

    public VAO vao() {
        return vao;
    }

}
