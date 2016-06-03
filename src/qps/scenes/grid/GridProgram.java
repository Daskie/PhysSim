package qps.scenes.grid;

import qps.*;
import qps.utils.Utils;
import qps.utils.Vec3;
import qps.utils.Vec4;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/31/2016
 */
public class GridProgram extends ShaderProgram {

    //private int u_divisions;
    private int u_dir;
    private int u_long;
    private int u_thickness;
    private int u_size;
    private int u_spacing;
    private int u_color;

    public GridProgram() {
        super("shaders/grid.vert", null, "shaders/color.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "View"), UniformGlobals.ViewGlobals.BINDING);
        //glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        //u_divisions = glGetUniformLocation(id, "u_divisions");
        u_dir = glGetUniformLocation(id, "u_dir");
        u_long = glGetUniformLocation(id, "u_long");
        u_thickness = glGetUniformLocation(id, "u_thickness");
        u_size = glGetUniformLocation(id, "u_size");
        u_spacing = glGetUniformLocation(id, "u_spacing");
        u_color = glGetUniformLocation(id, "u_color");

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize grid program!");
            return false;
        }

        return true;
    }

    //public void setDivisions(int divisions) {
    //    setUniform(u_divisions, divisions);
    //}

    public void setDir(Vec3 dir) {
        setUniform(u_dir, dir);
    }

    public void setLong(Vec3 longV) {
        setUniform(u_long, longV);
    }

    public void setThickness(float thickness) {
        setUniform(u_thickness, thickness);
    }

    public void setSize(int size) {
        setUniform(u_size, size);
    }

    public void setSpacing(float spacing) {
        setUniform(u_spacing, spacing);
    }

    public void setColor(Vec4 color) {
        setUniform(u_color, color);
    }

}
