package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.XRay;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * The type Mixin tile entity renderer dispatcher.
 */
@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Inject(method = "renderTileEntity", at = @At("HEAD"), cancellable = true)
    private void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage, final CallbackInfo callbackInfo) {
        final XRay xray = Objects.requireNonNull(Launch.moduleManager.getModule(XRay.class));

        if (xray.getState() && !xray.getXrayBlocks().contains(tileentityIn.getBlockType()))
            callbackInfo.cancel();
    }
}