package net.aspw.nightx.injection.forge.mixins.gui;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.Render2DEvent;
import net.aspw.nightx.ui.font.AWTFontRenderer;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSpectator.class)
public class MixinGuiSpectator {

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution p_175264_1_, float p_175264_2_, CallbackInfo callbackInfo) {
        NightX.eventManager.callEvent(new Render2DEvent(p_175264_2_));
        AWTFontRenderer.Companion.garbageCollectionTick();
    }
}