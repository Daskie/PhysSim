package qps;

/**
 * @since 5/20/2016
 */
public abstract class MeshManager {

    public static Mesh cubeMesh;
    public static Mesh squareMesh;

    public static boolean initMeshes() {
        cubeMesh = MeshLoader.fromFile("meshes/cube.qmesh");
        squareMesh = MeshLoader.simpleSquare();

        if (cubeMesh == null || squareMesh == null) {
            System.err.println("Failed to initialize meshes!");
            return false;
        }

        return true;
    }

}
