package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Client;
import net.aspw.client.event.RenderEntityEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The type Mixin render.
 */
@Mixin(Render.class)
public abstract class MixinRender {
    /**
     * Bind entity texture boolean.
     *
     * @param <T>    the type parameter
     * @param entity the entity
     * @return the boolean
     */
    @Shadow
    protected abstract <T extends Entity> boolean bindEntityTexture(T entity);

    @Shadow
    public <T extends Entity> void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    private void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        Client.eventManager.callEvent(new RenderEntityEvent(entity, x, y, z, entityYaw, partialTicks));
    }

    public void doRenders(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Client.eventManager.callEvent(new RenderEntityEvent(entity, x, y, z, entityYaw, partialTicks));
    }
}