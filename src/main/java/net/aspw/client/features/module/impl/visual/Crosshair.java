package net.aspw.client.features.module.impl.visual;

import net.aspw.client.Client;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.MovementUtils;
import net.aspw.client.util.render.ColorUtils;
import net.aspw.client.util.render.RenderUtils;
import net.aspw.client.value.FloatValue;
import net.aspw.client.value.IntegerValue;
import net.aspw.client.value.ListValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 * The type Crosshair.
 */
@ModuleInfo(name = "Crosshair", description = "", category = ModuleCategory.VISUAL)
public class Crosshair extends Module {

    //Rainbow thingy
    private final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
    /**
     * The Color mode value.
     */
//Color
    public ListValue colorModeValue = new ListValue("Color", new String[]{"Custom", "Rainbow", "LiquidSlowly", "Sky", "Fade", "Mixer"}, "Custom");
    /**
     * The Color red value.
     */
    public IntegerValue colorRedValue = new IntegerValue("Red", 255, 0, 255);
    /**
     * The Color green value.
     */
    public IntegerValue colorGreenValue = new IntegerValue("Green", 255, 0, 255);
    /**
     * The Color blue value.
     */
    public IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    /**
     * The Color alpha value.
     */
    public IntegerValue colorAlphaValue = new IntegerValue("Alpha", 255, 0, 255);
    /**
     * The Width val.
     */
    public FloatValue widthVal = new FloatValue("Width", 0.5F, 0.25F, 10);
    /**
     * The Size val.
     */
    public FloatValue sizeVal = new FloatValue("Size/Length", 7, 0.25F, 15);
    /**
     * The Gap val.
     */
    public FloatValue gapVal = new FloatValue("Gap", 5F, 0.25F, 15);

    /**
     * On render 2 d.
     *
     * @param event the event
     */
    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!Objects.requireNonNull(Client.moduleManager.getModule(Interface.class)).getNof5crossHair().get() || MinecraftInstance.mc.gameSettings.thirdPersonView == 0 && Objects.requireNonNull(Client.moduleManager.getModule(Interface.class)).getNof5crossHair().get()) {
            final ScaledResolution scaledRes = new ScaledResolution(mc);
            float width = widthVal.get();
            float size = sizeVal.get();
            float gap = gapVal.get();

            glPushMatrix();
            RenderUtils.drawBorderedRect(scaledRes.getScaledWidth() / 2F - width, scaledRes.getScaledHeight() / 2F - gap - size - (this.isMoving() ? 2 : 0), scaledRes.getScaledWidth() / 2F + 1.0f + width, scaledRes.getScaledHeight() / 2F - gap - (this.isMoving() ? 2 : 0), 0.5F, new Color(0, 0, 0, colorAlphaValue.get()).getRGB(), getCrosshairColor().getRGB());
            RenderUtils.drawBorderedRect(scaledRes.getScaledWidth() / 2F - width, scaledRes.getScaledHeight() / 2F + gap + 1 + (this.isMoving() ? 2 : 0) - 0.15F, scaledRes.getScaledWidth() / 2F + 1.0f + width, scaledRes.getScaledHeight() / 2F + 1 + gap + size + (this.isMoving() ? 2 : 0) - 0.15F, 0.5F, new Color(0, 0, 0, colorAlphaValue.get()).getRGB(), getCrosshairColor().getRGB());
            RenderUtils.drawBorderedRect(scaledRes.getScaledWidth() / 2F - gap - size - (this.isMoving() ? 2 : 0) + 0.15F, scaledRes.getScaledHeight() / 2F - width, scaledRes.getScaledWidth() / 2F - gap - (this.isMoving() ? 2 : 0) + 0.15F, scaledRes.getScaledHeight() / 2 + 1.0f + width, 0.5F, new Color(0, 0, 0, colorAlphaValue.get()).getRGB(), getCrosshairColor().getRGB());
            RenderUtils.drawBorderedRect(scaledRes.getScaledWidth() / 2F + 1 + gap + (this.isMoving() ? 2 : 0), scaledRes.getScaledHeight() / 2F - width, scaledRes.getScaledWidth() / 2F + size + gap + 1.0F + (this.isMoving() ? 2 : 0), scaledRes.getScaledHeight() / 2 + 1.0f + width, 0.5F, new Color(0, 0, 0, colorAlphaValue.get()).getRGB(), getCrosshairColor().getRGB());
            glPopMatrix();

            GlStateManager.resetColor();
        }
    }

    private boolean isMoving() {
        return MovementUtils.isMoving();
    }

    private Color getCrosshairColor() {
        switch (colorModeValue.get()) {
            case "Custom":
                return new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get());
            case "Rainbow":
                return new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
            case "Sky":
                return ColorUtils.reAlpha(RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get()), colorAlphaValue.get());
            case "LiquidSlowly":
                return ColorUtils.reAlpha(ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get()), colorAlphaValue.get());
            case "Mixer":
                return ColorUtils.reAlpha(ColorMixer.getMixedColor(0, mixerSecondsValue.get()), colorAlphaValue.get());
            default:
                return ColorUtils.reAlpha(ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100), colorAlphaValue.get());
        }
    }

}