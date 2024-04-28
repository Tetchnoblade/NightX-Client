package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.visual.client.altmanager.GuiAltManager;
import net.aspw.client.visual.font.semi.Fonts;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        buttonList.add(new GuiButton(998, width - 94, 5, 88, 20, "Alt Manager"));
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        Fonts.minecraftFont.drawStringWithShadow("§7Username: §d" + mc.getSession().getUsername(), 6f, 6f, 0xffffff);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if (button.id == 998) {
            mc.displayGuiScreen(new GuiAltManager((GuiScreen) (Object) this));
        }
    }
}