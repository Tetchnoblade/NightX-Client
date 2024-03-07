package net.aspw.client.injection.forge.mixins.gui;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.visual.client.GuiMainMenu;
import net.aspw.client.visual.font.semi.AWTFontRenderer;
import net.aspw.client.visual.font.semi.Fonts;
import net.aspw.client.visual.font.smooth.FontLoaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;


/**
 * The type Mixin gui button.
 */
@Mixin(GuiButton.class)
public abstract class MixinGuiButton extends Gui {

    /**
     * The constant buttonTextures.
     */
    @Shadow
    @Final
    protected static ResourceLocation buttonTextures;
    /**
     * The Visible.
     */
    @Shadow
    public boolean visible;
    /**
     * The X position.
     */
    @Shadow
    public int xPosition;
    /**
     * The Y position.
     */
    @Shadow
    public int yPosition;
    /**
     * The Width.
     */
    @Shadow
    public int width;
    /**
     * The Height.
     */
    @Shadow
    public int height;
    /**
     * The Enabled.
     */
    @Shadow
    public boolean enabled;
    /**
     * The Display string.
     */
    @Shadow
    public String displayString;
    /**
     * The Hovered.
     */
    @Shadow
    protected boolean hovered;
    private float alpha;

    /**
     * Mouse dragged.
     *
     * @param mc     the mc
     * @param mouseX the mouse x
     * @param mouseY the mouse y
     */
    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    /**
     * Gets hover state.
     *
     * @param mouseOver the mouse over
     * @return the hover state
     */
    @Shadow
    protected abstract int getHoverState(boolean mouseOver);

    /**
     * Draw button.
     *
     * @param mc     the mc
     * @param mouseX the mouse x
     * @param mouseY the mouse y
     * @author As_pw
     * @reason Font Render
     */
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            final FontRenderer fontRenderer =
                    mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.minecraftFont;
            hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition &&
                    mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

            final Interface anInterface = Launch.moduleManager.getModule(Interface.class);

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
                AWTFontRenderer.Companion.setAssumeNonVolatile(true);
                fontRenderer.drawStringWithShadow(displayString,
                        ((this.xPosition + this.width / 2F) -
                                fontRenderer.getStringWidth(displayString) / 2F),
                        (int) (this.yPosition + (this.height - 4) / 2F - 2), b);
                AWTFontRenderer.Companion.setAssumeNonVolatile(false);
                GlStateManager.resetColor();
            }
        }
    }
}