package net.ccbluex.liquidbounce.injection.forge.mixins.entity;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.Cape;
import net.ccbluex.liquidbounce.features.module.modules.render.EnchantColor;
import net.ccbluex.liquidbounce.features.module.modules.render.Fov;
import net.ccbluex.liquidbounce.features.module.modules.render.Nick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {

    //private CapeInfo capeInfo;

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final Cape capeMod = LiquidBounce.moduleManager.getModule(Cape.class);
        if (capeMod.getState() && Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName())) {
            callbackInfoReturnable.setReturnValue(capeMod.getCapeLocation(capeMod.getStyleValue().get()));
        }
    }

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void getFovModifier(CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final Fov fovModule = LiquidBounce.moduleManager.getModule(Fov.class);

        if (fovModule.getState()) {
            float newFOV = fovModule.getFovValue().get();

            if (!this.isUsingItem()) {
                callbackInfoReturnable.setReturnValue(newFOV);
                return;
            }

            if (this.getItemInUse().getItem() != Items.bow) {
                callbackInfoReturnable.setReturnValue(newFOV);
                return;
            }

            int i = this.getItemInUseDuration();
            float f1 = (float) i / 20.0f;
            f1 = f1 > 1.0f ? 1.0f : f1 * f1;
            newFOV *= 1.0f - f1 * 0.15f;
            callbackInfoReturnable.setReturnValue(newFOV);
        }
    }

    @Inject(method = "getLocationSkin()Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getSkin(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final Nick nameProtect = LiquidBounce.moduleManager.getModule(Nick.class);

        if (nameProtect.getState() && nameProtect.skinProtectValue.get()) {
            if (!nameProtect.allPlayersValue.get() && !Objects.equals(getGameProfile().getName(), Minecraft.getMinecraft().thePlayer.getGameProfile().getName()))
                return;

            callbackInfoReturnable.setReturnValue((nameProtect.customSkinValue.get() && nameProtect.skinImage != null) ? nameProtect.skinImage : DefaultPlayerSkin.getDefaultSkin(getUniqueID()));
        }
    }
}
