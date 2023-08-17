package net.aspw.client.util.render.shader;

import net.aspw.client.util.ClientUtils;
import net.aspw.client.util.MinecraftInstance;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Shader.
 */
public abstract class Shader extends MinecraftInstance {
    private int program;

    private Map<String, Integer> uniformsMap;

    /**
     * Instantiates a new Shader.
     *
     * @param fragmentShader the fragment shader
     */
    public Shader(final String fragmentShader) {
        int vertexShaderID, fragmentShaderID;

        try {
            final InputStream vertexStream = getClass().getResourceAsStream("/assets/minecraft/client/shader/vertex.vert");
            vertexShaderID = createShader(IOUtils.toString(vertexStream), ARBVertexShader.GL_VERTEX_SHADER_ARB);
            IOUtils.closeQuietly(vertexStream);

            final InputStream fragmentStream = getClass().getResourceAsStream("/assets/minecraft/client/shader/fragment/" + fragmentShader);
            fragmentShaderID = createShader(IOUtils.toString(fragmentStream), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
            IOUtils.closeQuietly(fragmentStream);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }

        if (vertexShaderID == 0 || fragmentShaderID == 0)
            return;

        program = ARBShaderObjects.glCreateProgramObjectARB();

        if (program == 0)
            return;

        ARBShaderObjects.glAttachObjectARB(program, vertexShaderID);
        ARBShaderObjects.glAttachObjectARB(program, fragmentShaderID);

        ARBShaderObjects.glLinkProgramARB(program);
        ARBShaderObjects.glValidateProgramARB(program);

        ClientUtils.getLogger().info("[Shader] Successfully loaded: " + fragmentShader);
    }

    /**
     * Start shader.
     */
    public void startShader() {
        GL11.glPushMatrix();
        GL20.glUseProgram(program);

        if (uniformsMap == null) {
            uniformsMap = new HashMap<>();
            setupUniforms();
        }

        updateUniforms();
    }

    /**
     * Stop shader.
     */
    public void stopShader() {
        GL20.glUseProgram(0);
        GL11.glPopMatrix();
    }

    /**
     * Sets uniforms.
     */
    public abstract void setupUniforms();

    /**
     * Update uniforms.
     */
    public abstract void updateUniforms();

    private int createShader(String shaderSource, int shaderType) {
        int shader = 0;

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

            return shader;
        } catch (final Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;

        }
    }

    private String getLogInfo(int i) {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    /**
     * Sets uniform.
     *
     * @param uniformName the uniform name
     * @param location    the location
     */
    public void setUniform(final String uniformName, final int location) {
        uniformsMap.put(uniformName, location);
    }

    /**
     * Sets uniform.
     *
     * @param uniformName the uniform name
     */
    public void setupUniform(final String uniformName) {
        setUniform(uniformName, GL20.glGetUniformLocation(program, uniformName));
    }

    /**
     * Gets uniform.
     *
     * @param uniformName the uniform name
     * @return the uniform
     */
    public int getUniform(final String uniformName) {
        return uniformsMap.get(uniformName);
    }
}
