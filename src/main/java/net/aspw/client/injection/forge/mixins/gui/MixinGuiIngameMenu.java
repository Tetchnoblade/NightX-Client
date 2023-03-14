package net.aspw.client.injection.forge.mixins.gui;

import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.protocol.ProtocolCollection;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.PacketUtils;
import net.aspw.client.visual.font.Fonts;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends MixinGuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        Fonts.minecraftFont.drawStringWithShadow(
                "§7Username: §a" + mc.getSession().getUsername(),
                6f,
                6f,
                0xffffff);
        if (!mc.isIntegratedServerRunning()) {
            Fonts.minecraftFont.drawStringWithShadow(
                    "§7IP: §a" + mc.getCurrentServerData().serverIP,
                    6f,
                    16f,
                    0xffffff);
            Fonts.minecraftFont.drawStringWithShadow(
                    "§7Protocol: §a" + ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName(),
                    6f,
                    26f,
                    0xffffff);
            Fonts.minecraftFont.drawStringWithShadow(
                    "§7Ping: §a" + EntityUtils.getPing(mc.thePlayer),
                    6f,
                    36f,
                    0xffffff);
            Fonts.minecraftFont.drawStringWithShadow(
                    "§7inBound: §a" + PacketUtils.avgInBound,
                    6f,
                    46f,
                    0xffffff);
            Fonts.minecraftFont.drawStringWithShadow(
                    "§7outBound: §a" + PacketUtils.avgOutBound,
                    6f,
                    56f,
                    0xffffff);
        }
    }
}