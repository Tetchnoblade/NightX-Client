package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.visual.Cape;
import net.aspw.client.features.module.impl.visual.Hud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final Cape cape = Client.moduleManager.getModule(Cape.class);
        if (cape.getState() && Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName())) {
            callbackInfoReturnable.setReturnValue(cape.getCapeLocation(cape.getStyleValue().get()));
        }
    }

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void getFovModifier(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        float f5Fov = 1.3f;
        f5Fov *= 1.0f;
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 && Client.moduleManager.getModule(Hud.class).getF5Animation().get()) {
            callbackInfoReturnable.setReturnValue(f5Fov);
        }
    }
}