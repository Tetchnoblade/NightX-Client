package net.aspw.client.utils.render.shader.shaders;

import net.aspw.client.utils.render.shader.FramebufferShader;
import org.lwjgl.opengl.GL20;

/**
 * The type Glow shader.
 */
public final class GlowShader extends FramebufferShader {

    /**
     * The constant GLOW_SHADER.
     */
    public static final GlowShader GLOW_SHADER = new GlowShader();

    /**
     * Instantiates a new Glow shader.
     */
    public GlowShader() {
        super("glow.frag");
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
        GL20.glUniform3f(getUniform("color"), red, green, blue);
        GL20.glUniform1f(getUniform("divider"), 140F);
        GL20.glUniform1f(getUniform("radius"), radius);
        GL20.glUniform1f(getUniform("maxSample"), 10F);
    }
}
