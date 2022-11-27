package net.aspw.nightx.injection.forge.mixins.gui;

import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.protocol.ProtocolCollection;
import net.aspw.nightx.ui.font.Fonts;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {
    private GuiSlider viaSlider;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        buttonList.add(viaSlider = new GuiSlider(1337, width - 116, 7, 110, 20, "Protocol: ", "", 0, ProtocolCollection.values().length - 1, ProtocolCollection.values().length - 1 - getProtocolIndex(ViaForge.getInstance().getVersion()), false, true,
                guiSlider -> {
                    ViaForge.getInstance().setVersion(ProtocolCollection.values()[ProtocolCollection.values().length - 1 - guiSlider.getValueInt()].getVersion().getVersion());
                    this.updatePortalText();
                }));
        this.updatePortalText();
    }

    private void updatePortalText() {
        if (this.viaSlider == null)
            return;

        this.viaSlider.displayString = "Protocol: " + ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName();
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        Fonts.fontSFUI40.drawStringWithShadow(
                "§7Username: §a" + mc.getSession().getUsername(),
                6f,
                6f,
                0xffffff);
    }

    private int getProtocolIndex(int id) {
        for (int i = 0; i < ProtocolCollection.values().length; i++)
            if (ProtocolCollection.values()[i].getVersion().getVersion() == id)
                return i;
        return -1;
    }
}