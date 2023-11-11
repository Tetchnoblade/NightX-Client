package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.protocol.Protocol;
import net.aspw.client.visual.client.GuiProxyManager;
import net.aspw.client.visual.client.altmanager.GuiAltManager;
import net.aspw.client.visual.font.semi.Fonts;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The type Mixin gui multiplayer.
 */
@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        buttonList.add(new GuiButton(998, width - 94, 5, 88, 20, "Alt Manager"));
        buttonList.add(new GuiButton(1000, 4, height - 24, 68, 20, "Proxy"));
        buttonList.add(Protocol.getAsyncVersionSlider());
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        Fonts.minecraftFont.drawStringWithShadow(
                "§7Username: §d" + mc.getSession().getUsername(),
                117f,
                11.5f,
                0xffffff);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if (button.id == 1000) {
            mc.displayGuiScreen(new GuiProxyManager((GuiScreen) (Object) this));
        }
        if (button.id == 998) {
            mc.displayGuiScreen(new GuiAltManager((GuiScreen) (Object) this));
        }
    }
}