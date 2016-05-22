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
        for (Entity entity : entities) {
            if (entity.isCorporeal()) {
                glBindVertexArray(entity.vao().vao());
                Utils.checkGLErr();
                glDrawElements(GL_TRIANGLES, entity.mesh().nIndices(), GL_UNSIGNED_INT, 0);
                Utils.checkGLErr();
            }
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public Camera camera() {
        return camera;
    }

}
