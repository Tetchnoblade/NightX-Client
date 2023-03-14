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

@Mixin(Render.class)
public abstract class MixinRender {
    @Shadow
    protected abstract <T extends Entity> boolean bindEntityTexture(T entity);

    @Inject(method = "doRender", at = @At("HEAD"))
    private void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        Client.eventManager.callEvent(new RenderEntityEvent(entity, x, y, z, entityYaw, partialTicks));
    }

    public void doRenders(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Client.eventManager.callEvent(new RenderEntityEvent(entity, x, y, z, entityYaw, partialTicks));
    }
}