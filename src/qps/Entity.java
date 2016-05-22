package qps;

/**
 * @since 5/21/2016
 */
public class Entity {

    protected Vec3 loc;
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

    public void move(Vec3 delta) {
        loc = loc.add(delta);
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
