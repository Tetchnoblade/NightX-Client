package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.Interface;
import net.minecraft.client.gui.achievement.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GuiAchievement.class)
public class MixinGuiAchievement {
    @Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
    private void injectAchievements(CallbackInfo ci) {
        final Interface anInterface = Objects.requireNonNull(Launch.moduleManager.getModule(Interface.class));

        if (anInterface.getState() && anInterface.getNoAchievement().get())
            ci.cancel();
    }
}