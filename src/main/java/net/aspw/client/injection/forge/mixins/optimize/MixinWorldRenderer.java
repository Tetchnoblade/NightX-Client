package net.aspw.client.injection.forge.mixins.optimize;

import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.access.Cullable;
import dev.tr7zw.entityculling.access.EntityRendererInter;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public abstract class MixinWorldRenderer {
    @Shadow
    public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity p_getEntityRenderObject_1_);

    @Inject(at = @At("HEAD"), method = "doRenderEntity", cancellable = true)
    public void doRenderEntity(Entity entity, double p_doRenderEntity_2_, double d1, double d2,
                               float tickDelta, float p_doRenderEntity_9_, boolean p_doRenderEntity_10_, CallbackInfoReturnable<Boolean> info) {
        Cullable cullable = (Cullable) entity;
        if (!cullable.isForcedVisible() && cullable.isCulled()) {
            EntityRendererInter<Entity> entityRenderer = (EntityRendererInter) getEntityRenderObject(entity);
            if (entityRenderer.shadowShouldShowName(entity)) {
                entityRenderer.shadowRenderNameTag(entity, p_doRenderEntity_2_, d1, d2);
            }
            EntityCullingModBase.instance.skippedEntities++;
            info.cancel();
            return;
        }
        EntityCullingModBase.instance.renderedEntities++;
        cullable.setOutOfCamera(false);
    }
}