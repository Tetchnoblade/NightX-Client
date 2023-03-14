package net.aspw.client.injection.forge.mixins.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkPlayerInfo.class)
public class MixinNetworkPlayerInfo {
    @Shadow
    @Final
    private GameProfile gameProfile;

    @Inject(method = "getLocationSkin", cancellable = true, at = @At("HEAD"))
    private void injectSkinProtect(CallbackInfoReturnable<ResourceLocation> cir) {
    }
}