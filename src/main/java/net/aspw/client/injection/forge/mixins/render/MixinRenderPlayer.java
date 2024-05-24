package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.aspw.client.utils.APIConnecter;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(method = {"getEntityTexture"}, at = {@At("HEAD")}, cancellable = true)
    public void getEntityTexture(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> ci) {
        final CustomModel customModel = Objects.requireNonNull(Launch.moduleManager.getModule(CustomModel.class));
        final ResourceLocation rabbit = APIConnecter.INSTANCE.callImage("rabbit", "models");
        final ResourceLocation fred = APIConnecter.INSTANCE.callImage("freddy", "models");
        final ResourceLocation imposter = APIConnecter.INSTANCE.callImage("imposter", "models");

        if (customModel.getState()) {
            if (customModel.getMode().get().contains("Rabbit")) {
                ci.setReturnValue(rabbit);
            }
            if (customModel.getMode().get().contains("Freddy")) {
                ci.setReturnValue(fred);
            }
            if (customModel.getMode().get().contains("Imposter")) {
                ci.setReturnValue(imposter);
            }
        }
    }
}