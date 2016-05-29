package qps;

import java.io.IOException;

/**
 * @since 5/20/2016
 */
public abstract class MeshManager {

    public static Mesh squareMesh;
    public static Mesh cubeMesh;
    public static Mesh sphereMesh;
    public static Mesh arrowMesh;

    public static boolean initMeshes() {
        squareMesh = MeshLoader.square();

        try {
            cubeMesh = MeshLoader.fromFile("meshes/cube.qmesh");
            sphereMesh = MeshLoader.fromFile("meshes/sphere8.qmesh");
            arrowMesh = MeshLoader.fromFile("meshes/arrow16.qmesh");
        } catch (IOException e) {
            System.err.println("Failed to initialize meshes!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
