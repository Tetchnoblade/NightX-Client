package net.aspw.nightx.injection.forge.mixins.gui;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.render.NoAchievements;
import net.minecraft.client.gui.achievement.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiAchievement.class)
public class MixinGuiAchievement {
    @Inject(method = "updateAchievementWindow", at = @At("HEAD"), cancellable = true)
    private void injectAchievements(CallbackInfo ci) {
        if (NightX.moduleManager != null
                && NightX.moduleManager.getModule(NoAchievements.class) != null
                && NightX.moduleManager.getModule(NoAchievements.class).getState())
            ci.cancel();
    }
}
