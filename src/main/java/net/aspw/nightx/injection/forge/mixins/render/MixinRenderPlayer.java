package net.aspw.nightx.injection.forge.mixins.render;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.render.PlayerEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "renderLivingAt", at = @At("HEAD"))
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z, CallbackInfo callbackInfo) {
        if (NightX.moduleManager.get(PlayerEdit.class).getState() & entityLivingBaseIn.equals(Minecraft.getMinecraft().thePlayer) && PlayerEdit.editPlayerSizeValue.get()) {
            GlStateManager.scale(PlayerEdit.playerSizeValue.get(), PlayerEdit.playerSizeValue.get(), PlayerEdit.playerSizeValue.get());
        }
    }
}