package qps;

import java.io.IOException;

/**
 * @since 5/20/2016
 */
public abstract class MeshManager {

    public static Mesh cubeMesh;
    public static Mesh squareMesh;

    public static boolean initMeshes() {
        try {
            cubeMesh = MeshLoader.fromFile("meshes/cube.qmesh");
        } catch (IOException e) {
            System.err.println("Failed to initialize cube mesh!");
            e.printStackTrace();
            return false;
        }

        squareMesh = MeshLoader.simpleSquare();

        return true;
    }

}
