package net.aspw.nightx.injection.forge.mixins.cape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelPlayer;

@Mixin(value =  ModelPlayer.class)
public class PlayerModelMixin {

    @Inject(method = "renderCape", at = @At("HEAD"), cancellable = true)
    public void renderCloak(float p_renderCape_1_, CallbackInfo info) {
        info.cancel();
    }


}
