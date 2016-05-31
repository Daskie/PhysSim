package qps;

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

        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "Transform"), UniformGlobals.TransformGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "ChargeCounts"), UniformGlobals.ChargeCountsGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "SphereCharges"), UniformGlobals.SphereChargesGlobals.BINDING);
        glUniformBlockBinding(id, glGetUniformBlockIndex(id, "EThreshold"), UniformGlobals.EThresholdGlobals.BINDING);
        if (!Utils.checkGLErr()) {
            System.err.println("Failed to get uniform locations for main shader program!");
            return false;
        }

        return true;
    }
}
