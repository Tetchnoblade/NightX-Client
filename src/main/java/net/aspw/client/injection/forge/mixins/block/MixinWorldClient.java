package net.aspw.client.injection.forge.mixins.block;

import net.aspw.client.utils.PlayerUtils;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Inject(method = "playSound", at = @At("HEAD"), cancellable = true)
    public void playSound(double p_playSound_1_, double p_playSound_2_, double p_playSound_3_, String p_playSound_4_, float p_playSound_5_, float p_playSound_6_, boolean p_playSound_7_, CallbackInfo ci) {
        if (PlayerUtils.INSTANCE.getPredicting()) ci.cancel();
    }
}