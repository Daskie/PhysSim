package qps.scenes.map;

import qps.ShaderProgram;
import qps.UniformGlobals;
import qps.utils.Utils;

import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @since 5/28/2016
 */
public class MapProgram extends ShaderProgram {

    public MapProgram() {
        super("shaders/a.vert", null, "shaders/map.frag");
    }

    @Override
    public boolean init() {
        if (!super.init()) return false;

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Camera"), UniformGlobals.CameraGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Model"), UniformGlobals.ModelGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ChargeCounts"), UniformGlobals.ChargeCountsGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "SphereCharges"), UniformGlobals.SphereChargesGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "PlaneCharges"), UniformGlobals.PlaneChargesGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "LineCharges"), UniformGlobals.LineChargesGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Threshold"), UniformGlobals.ThresholdGlobals.BINDING);

        if (!Utils.checkGLErr()) {
            System.err.println("Failed to initialize map program!");
            return false;
        }

        return true;
    }
}
