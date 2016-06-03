package qps;

/**
 * @since 5/21/2016
 */
public abstract class Scene {

    public abstract boolean init();

    public abstract void update(int t, int dt);

    public abstract void draw();

}
