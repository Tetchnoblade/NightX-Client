package net.aspw.client.injection.forge.mixins.render;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityParticleEmitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * The type Mixin effect renderer.
 */
@Mixin(EffectRenderer.class)
public abstract class MixinEffectRenderer {

    @Shadow
    private List<EntityParticleEmitter> particleEmitters;

    /**
     * Update effect layer.
     *
     * @param layer the layer
     */
    @Shadow
    protected abstract void updateEffectLayer(int layer);

    /**
     * Update effects.
     *
     * @author As_pw
     * @reason Effects
     */
    @Overwrite
    public void updateEffects() {
        try {
            for (int i = 0; i < 4; ++i)
                this.updateEffectLayer(i);

            for (final Iterator<EntityParticleEmitter> it = this.particleEmitters.iterator(); it.hasNext(); ) {
                final EntityParticleEmitter entityParticleEmitter = it.next();

                entityParticleEmitter.onUpdate();

                if (entityParticleEmitter.isDead)
                    it.remove();
            }
        } catch (final ConcurrentModificationException ignored) {
        }
    }
}