package net.aspw.client.util.render.shader.shaders;

import net.aspw.client.util.render.shader.FramebufferShader;
import org.lwjgl.opengl.GL20;

/**
 * The type Outline shader.
 */
public final class OutlineShader extends FramebufferShader {

    /**
     * The constant OUTLINE_SHADER.
     */
    public static final OutlineShader OUTLINE_SHADER = new OutlineShader();

    /**
     * Instantiates a new Outline shader.
     */
    public OutlineShader() {
        super("outline.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1F / mc.displayWidth * (radius * quality), 1F / mc.displayHeight * (radius * quality));
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
        GL20.glUniform1f(getUniform("radius"), radius);
    }
}
