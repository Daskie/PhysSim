package qps;

import java.io.IOException;

/**
 * @since 5/20/2016
 */
public abstract class MeshManager {

    public static Mesh squareMesh;
    public static Mesh cubeMesh;
    public static Mesh sphereMesh;

    public static boolean initMeshes() {
        squareMesh = MeshLoader.simpleSquare();

        try {
            cubeMesh = MeshLoader.fromFile("meshes/cube.qmesh");
        } catch (IOException e) {
            System.err.println("Failed to initialize cube mesh!");
            e.printStackTrace();
            return false;
        }

        try {
            sphereMesh = MeshLoader.fromFile("meshes/sphere8.qmesh");
        } catch (IOException e) {
            System.err.println("Failed to initialize sphere mesh!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
