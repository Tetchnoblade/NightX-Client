package net.aspw.client.utils.render;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

/**
 * Like {@link VertexBuffer}, but it deletes it's contents when it is deleted
 * by the garbage collector
 */
public class SafeVertexBuffer extends VertexBuffer {

    public SafeVertexBuffer(VertexFormat vertexFormatIn) {
        super(vertexFormatIn);
    }

    @Override
    protected void finalize() throws Throwable {
        this.deleteGlBuffers();
    }
}
