package qps;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * @since 5/21/2016
 */
public class Scene {

    private ArrayList<Entity> entities;
    private Camera camera;

    public Scene() {
        entities = new ArrayList<Entity>();
        camera = new Camera();
    }

    public void draw() {

    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Camera camera() {
        return camera;
    }

}
