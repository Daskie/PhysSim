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
    public static Mesh coneMesh;
    public static Mesh axesMesh;
    public static Mesh cylinderMesh;

    public static boolean initMeshes() {
        squareMesh = MeshLoader.square();

        try {
            cubeMesh = MeshLoader.fromFile("meshes/cube.qmesh");
            sphereMesh = MeshLoader.fromFile("meshes/sphere8.qmesh");
            arrowMesh = MeshLoader.fromFile("meshes/arrow16.qmesh");
            coneMesh = MeshLoader.fromFile("meshes/cone16.qmesh");
            axesMesh = MeshLoader.fromFile("meshes/cardinal16.qmesh");
            cylinderMesh = MeshLoader.fromFile("meshes/cylinder16.qmesh");
        } catch (IOException e) {
            System.err.println("Failed to initialize meshes!");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
