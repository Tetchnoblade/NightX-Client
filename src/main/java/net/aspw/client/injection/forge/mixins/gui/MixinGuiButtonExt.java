package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.util.render.RenderUtils;
import net.aspw.client.visual.client.GuiMainMenu;
import net.aspw.client.visual.font.semi.Fonts;
import net.aspw.client.visual.font.smooth.FontLoaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;

/**
 * The type Mixin gui button ext.
 */
@Mixin(GuiButtonExt.class)
public abstract class MixinGuiButtonExt extends GuiButton {

    private float alpha;

    /**
     * Instantiates a new Mixin gui button ext.
     *
     * @param p_i46323_1_ the p i 46323 1
     * @param p_i46323_2_ the p i 46323 2
     * @param p_i46323_3_ the p i 46323 3
     * @param p_i46323_4_ the p i 46323 4
     * @param p_i46323_5_ the p i 46323 5
     * @param p_i46323_6_ the p i 46323 6
     */
    public MixinGuiButtonExt(int p_i46323_1_, int p_i46323_2_, int p_i46323_3_, int p_i46323_4_,
                             int p_i46323_5_, String p_i46323_6_) {
        super(p_i46323_1_, p_i46323_2_, p_i46323_3_, p_i46323_4_, p_i46323_5_, p_i46323_6_);
    }

    /**
     * @author As_pw
     * @reason Button
     */
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            final FontRenderer fontRenderer =
                    mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.minecraftFont;
            hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition &&
                    mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

            final Interface anInterface = Client.moduleManager.getModule(Interface.class);

            if (anInterface == null) return;

            if (enabled && hovered) {
                alpha = 190;
            } else {
                alpha = 120;
            }

            if (mc.currentScreen instanceof GuiMainMenu)
                RenderUtils.originalRoundedRect(this.xPosition, this.yPosition,
                        this.xPosition + this.width, this.yPosition + this.height, 0F,
                        this.enabled ? new Color(0F, 0F, 0F, this.alpha / 255F).getRGB() :
                                new Color(120F, 120F, 120F, 100F).getRGB());
            else {
                RenderUtils.originalRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0F, new Color(0F, 0F, 0F, this.alpha / 255F).getRGB());
            }

            int j = 13816530;
            int b = 14737632;

            if (!this.enabled) {
                j = 10526880;
                b = 10526880;
            } else if (this.hovered) {
                j = 16777120;
                b = 5263615;
            }

            if (mc.currentScreen instanceof GuiMainMenu)
                FontLoaders.SF21.drawCenteredString(this.displayString, this.xPosition + this.width / 2F, this.yPosition + (this.height - 8) / 2F, j);
            else {
                mc.getTextureManager().bindTexture(buttonTextures);
                mouseDragged(mc, mouseX, mouseY);
                fontRenderer.drawStringWithShadow(displayString,
                        ((this.xPosition + this.width / 2F) -
                                fontRenderer.getStringWidth(displayString) / 2F),
                        (int) (this.yPosition + (this.height - 4) / 2F - 2), b);
                GlStateManager.resetColor();
            }
        }
    }
}
