package net.aspw.client.features.module.impl.visual;

import net.aspw.client.Client;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.utils.render.ColorUtils;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.FloatValue;
import net.aspw.client.value.IntegerValue;
import net.aspw.client.value.ListValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Crosshair", category = ModuleCategory.VISUAL)
public class Crosshair extends Module {

    //Rainbow thingy
    private final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
    //Color
    public ListValue colorModeValue = new ListValue("Color", new String[]{"Custom", "Rainbow", "LiquidSlowly", "Sky", "Fade", "Mixer"}, "Custom");
    public IntegerValue colorRedValue = new IntegerValue("Red", 255, 0, 255);
    public IntegerValue colorGreenValue = new IntegerValue("Green", 255, 0, 255);
    public IntegerValue colorBlueValue = new IntegerValue("Blue", 0, 0, 255);
    public IntegerValue colorAlphaValue = new IntegerValue("Alpha", 180, 0, 255);
    public FloatValue widthVal = new FloatValue("Width", 0.4F, 0.25F, 10);
    public FloatValue sizeVal = new FloatValue("Size/Length", 4, 0.25F, 15);
    public FloatValue gapVal = new FloatValue("Gap", 3F, 0.25F, 15);
    public BoolValue hitMarkerVal = new BoolValue("HitMarker", false);

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
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

            EntityLivingBase target = Client.moduleManager.getModule(KillAura.class).getTarget();

            if (hitMarkerVal.get() && target != null && target.hurtTime > 0) {
                glPushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

                glColor4f(1, 1, 1, (float) target.hurtTime / (float) target.maxHurtTime);
                glEnable(GL_LINE_SMOOTH);
                glLineWidth(1F);

                glBegin(3);

                glVertex2f(scaledRes.getScaledWidth() / 2F + gap, scaledRes.getScaledHeight() / 2F + gap);
                glVertex2f(scaledRes.getScaledWidth() / 2F + gap + size, scaledRes.getScaledHeight() / 2F + gap + size);

                glEnd();

                glBegin(3);

                glVertex2f(scaledRes.getScaledWidth() / 2F - gap, scaledRes.getScaledHeight() / 2F - gap);
                glVertex2f(scaledRes.getScaledWidth() / 2F - gap - size, scaledRes.getScaledHeight() / 2F - gap - size);

                glEnd();

                glBegin(3);

                glVertex2f(scaledRes.getScaledWidth() / 2F - gap, scaledRes.getScaledHeight() / 2F + gap);
                glVertex2f(scaledRes.getScaledWidth() / 2F - gap - size, scaledRes.getScaledHeight() / 2F + gap + size);

                glEnd();

                glBegin(3);

                glVertex2f(scaledRes.getScaledWidth() / 2F + gap, scaledRes.getScaledHeight() / 2F - gap);
                glVertex2f(scaledRes.getScaledWidth() / 2F + gap + size, scaledRes.getScaledHeight() / 2F - gap - size);

                glEnd();

                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                glPopMatrix();
            }
        }
    }

    private boolean isMoving() {
        return mc.thePlayer.isSwingInProgress;
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