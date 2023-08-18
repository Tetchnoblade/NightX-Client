package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.features.api.PacketManager;
import net.aspw.client.features.module.impl.visual.Cape;
import net.aspw.client.features.module.impl.visual.CustomModel;
import net.aspw.client.features.module.impl.visual.Hud;
import net.aspw.client.features.module.impl.visual.SilentView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * The type Mixin abstract client player.
 */
@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

    @Shadow
    public abstract ResourceLocation getLocationCape();

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final Cape cape = Objects.requireNonNull(Client.moduleManager.getModule(Cape.class));
        final SilentView silentView = Objects.requireNonNull(Client.moduleManager.getModule(SilentView.class));
        final CustomModel customModel = Objects.requireNonNull(Client.moduleManager.getModule(CustomModel.class));
        if ((silentView.getState() && silentView.getSilentValue().get() && silentView.shouldRotate() || customModel.getState() && customModel.getHideCape().get()) && Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName())) {
            callbackInfoReturnable.cancel();
            return;
        }
        if (cape.getState() && Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName())) {
            if (!cape.getStyleValue().get().equals("Rise5") && !cape.getStyleValue().get().equals("NightX"))
                callbackInfoReturnable.setReturnValue(cape.getCapeLocation(cape.getStyleValue().get()));
            if (cape.getStyleValue().get().equals("Rise5") || cape.getStyleValue().get().equals("NightX"))
                callbackInfoReturnable.setReturnValue(new ResourceLocation("client/cape/animation/" + PacketManager.selectedCape + "/" + PacketManager.ticks + ".png"));
        }
    }

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void getFovModifier(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final Hud hud = Objects.requireNonNull(Client.moduleManager.getModule(Hud.class));
        float newFov = hud.getCustomFovModifier().getValue();
        newFov *= 1.0f;
        if (hud.getCustomFov().get() && hud.getState()) {
            callbackInfoReturnable.setReturnValue(newFov);
        }
    }
}