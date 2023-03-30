package net.aspw.client.injection.forge.mixins.optimize;

import net.minecraft.client.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public class MixinLoadingScreenSkipProgress {
    @Inject(method = "setLoadingProgress", at = @At("HEAD"), cancellable = true)
    private void patcher$skipProgress(int progress, CallbackInfo ci) {
        if (progress < 0) {
            ci.cancel();
        }
    }
}