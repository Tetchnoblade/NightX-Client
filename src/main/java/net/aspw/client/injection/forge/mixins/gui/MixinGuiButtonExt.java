package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.utils.render.RenderUtils;
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
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

@Mixin(GuiButtonExt.class)
public abstract class MixinGuiButtonExt extends GuiButton {

    @Unique
    private float alpha;

    public MixinGuiButtonExt(final int p_i46323_1_, final int p_i46323_2_, final int p_i46323_3_, final int p_i46323_4_,
                             final int p_i46323_5_, final String p_i46323_6_) {
        super(p_i46323_1_, p_i46323_2_, p_i46323_3_, p_i46323_4_, p_i46323_5_, p_i46323_6_);
    }

    /**
     * @author As_pw
     * @reason Button Renderer
     */
    @Override
    @Overwrite
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (visible) {
            final FontRenderer fontRenderer =
                    mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.minecraftFont;
            hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition &&
                    mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

            final Interface anInterface = Launch.moduleManager.getModule(Interface.class);

            if (anInterface == null) return;

            if (mc.currentScreen instanceof GuiMainMenu && enabled && hovered) {
                alpha = 160;
            } else if (enabled && hovered) {
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
                if (this.enabled && this.hovered) {
                    RenderUtils.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, new Color(0, 0, 255, 80).getRGB(), new Color(0, 0, 255, 80).getRGB());
                    RenderUtils.drawGradientRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, new Color(0, 0, 255, 80).getRGB(), new Color(0, 0, 255, 80).getRGB());
                    RenderUtils.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, new Color(0, 0, 255, 80).getRGB(), new Color(0, 0, 255, 80).getRGB());
                    RenderUtils.drawGradientRect(this.xPosition + this.width - 1, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, new Color(0, 0, 255, 80).getRGB(), new Color(0, 0, 255, 80).getRGB());
                    RenderUtils.originalRoundedRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, 0F, new Color(0F, 0F, 0F, this.alpha / 255F).getRGB());
                } else {
                    RenderUtils.originalRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0F, new Color(0F, 0F, 0F, this.alpha / 255F).getRGB());
                }
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