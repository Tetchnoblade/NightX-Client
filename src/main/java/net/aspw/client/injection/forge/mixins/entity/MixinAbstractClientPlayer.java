package net.aspw.client.injection.forge.mixins.entity;

import kotlin.Pair;
import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.Cape;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.utils.APIConnecter;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.image.BufferedImage;
import java.util.*;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

    private final Map<String, BufferedImage> donorCapeLocations = new HashMap<>();
    private boolean capeInjected = false;

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final Cape cape = Objects.requireNonNull(Launch.moduleManager.getModule(Cape.class));
        String playerName = getGameProfile().getName();
        if (!this.capeInjected) {
            for (Pair<String, BufferedImage> pair : APIConnecter.INSTANCE.getDonorCapeLocations()) {
                donorCapeLocations.put(pair.getFirst(), pair.getSecond());
            }
            this.capeInjected = true;
        }

        if (donorCapeLocations.containsKey(playerName)) {
            BufferedImage image = donorCapeLocations.get(playerName);
            callbackInfoReturnable.setReturnValue(MinecraftInstance.mc.getTextureManager().getDynamicTextureLocation(Launch.CLIENT_FOLDER, new DynamicTexture(image)));
            return;
        }

        if (cape.getCustomCape().get() && playerName.equalsIgnoreCase(MinecraftInstance.mc.thePlayer.getGameProfile().getName()))
            callbackInfoReturnable.setReturnValue(cape.getCapeLocation(cape.getStyleValue().get()));
    }

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void getFovModifier(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final Interface anInterface = Objects.requireNonNull(Launch.moduleManager.getModule(Interface.class));
        float newFov = anInterface.getCustomFovModifier().getValue();
        newFov *= 1.0f;
        if (anInterface.getCustomFov().get() && anInterface.getState()) {
            callbackInfoReturnable.setReturnValue(newFov);
        }
    }
}