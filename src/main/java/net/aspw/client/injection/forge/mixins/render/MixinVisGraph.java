package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.XRay;
import net.minecraft.client.renderer.chunk.VisGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * The type Mixin vis graph.
 */
@Mixin(VisGraph.class)
public class MixinVisGraph {

    @Inject(method = "func_178606_a", at = @At("HEAD"), cancellable = true)
    private void func_178606_a(final CallbackInfo callbackInfo) {
        final XRay xray = Objects.requireNonNull(Launch.moduleManager.getModule(XRay.class));

        if (xray.getState())
            callbackInfo.cancel();
    }
}