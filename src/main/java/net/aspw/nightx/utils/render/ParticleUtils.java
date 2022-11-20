package net.aspw.nightx.utils.render;

import net.vitox.ParticleGenerator;

public final class ParticleUtils {

    private static final ParticleGenerator particleGenerator = new ParticleGenerator(0);

    public static void drawParticles(int mouseX, int mouseY) {
        particleGenerator.draw(mouseX, mouseY);
    }
}