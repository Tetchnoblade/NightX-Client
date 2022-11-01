package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.client.ColorMixer;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.render.BlendUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.WorldToScreen;
import net.ccbluex.liquidbounce.utils.render.shader.FramebufferShader;
import net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader;
import net.ccbluex.liquidbounce.utils.render.shader.shaders.OutlineShader;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.text.DecimalFormat;

import static net.ccbluex.liquidbounce.utils.render.WorldToScreen.getMatrix;
import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ESP", category = ModuleCategory.RENDER)
public class ESP extends Module {

    public static boolean renderNameTags = true;
    public final ListValue modeValue = new ListValue("Mode", new String[]{"Box", "OtherBox", "WireFrame", "2D", "Real2D", "Outline", "ShaderOutline", "ShaderGlow"}, "Real2D");
    public final BoolValue real2dcsgo = new BoolValue("2D-CSGOStyle", false, () -> modeValue.get().equalsIgnoreCase("real2d"));
    public final BoolValue real2dShowHealth = new BoolValue("2D-ShowHealth", false, () -> modeValue.get().equalsIgnoreCase("real2d"));
    public final BoolValue real2dShowHeldItem = new BoolValue("2D-ShowHeldItem", false, () -> modeValue.get().equalsIgnoreCase("real2d"));
    public final BoolValue real2dShowName = new BoolValue("2D-ShowEntityName", false, () -> modeValue.get().equalsIgnoreCase("real2d"));
    public final BoolValue real2dOutline = new BoolValue("2D-Outline", false, () -> modeValue.get().equalsIgnoreCase("real2d"));
    public final FloatValue outlineWidth = new FloatValue("Outline-Width", 3F, 0.5F, 5F, () -> modeValue.get().equalsIgnoreCase("outline"));
    public final FloatValue wireframeWidth = new FloatValue("WireFrame-Width", 2F, 0.5F, 5F, () -> modeValue.get().equalsIgnoreCase("wireframe"));
    private final FloatValue shaderOutlineRadius = new FloatValue("ShaderOutline-Radius", 1.35F, 1F, 2F, "x", () -> modeValue.get().equalsIgnoreCase("shaderoutline"));
    private final FloatValue shaderGlowRadius = new FloatValue("ShaderGlow-Radius", 2.5F, 2F, 3F, "x", () -> modeValue.get().equalsIgnoreCase("shaderglow"));
    private final ListValue colorModeValue = new ListValue("Color", new String[]{"Custom", "Health", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"}, "Custom");
    private final IntegerValue colorRedValue = new IntegerValue("Red", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("Green", 0, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    private final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
    private final BoolValue colorTeam = new BoolValue("Team", false);
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0");

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final String mode = modeValue.get();

        Matrix4f mvMatrix = getMatrix(GL11.GL_MODELVIEW_MATRIX);
        Matrix4f projectionMatrix = getMatrix(GL11.GL_PROJECTION_MATRIX);

        boolean real2d = mode.equalsIgnoreCase("real2d");

        if (real2d) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0, mc.displayWidth, mc.displayHeight, 0, -1.0f, 1.0);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();

            glDisable(GL_DEPTH_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);

            GL11.glLineWidth(1.0f);
        }

        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != null && entity != mc.thePlayer && EntityUtils.isSelected(entity, false) && RenderUtils.isInViewFrustrum(entity)) {
                final EntityLivingBase entityLiving = (EntityLivingBase) entity;

                Color color = getColor(entityLiving);

                switch (mode.toLowerCase()) {
                    case "box":
                    case "otherbox":
                        RenderUtils.drawEntityBox(entity, color, !mode.equalsIgnoreCase("otherbox"));
                        break;
                    case "2d": {
                        final RenderManager renderManager = mc.getRenderManager();
                        final Timer timer = mc.timer;

                        final double posX = entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX;
                        final double posY = entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY;
                        final double posZ = entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ;

                        RenderUtils.draw2D(entityLiving, posX, posY, posZ, color.getRGB(), Color.BLACK.getRGB());
                        break;
                    }
                    case "real2d": {
                        final RenderManager renderManager = mc.getRenderManager();
                        final Timer timer = mc.timer;

                        AxisAlignedBB bb = entityLiving.getEntityBoundingBox()
                                .offset(-entityLiving.posX, -entityLiving.posY, -entityLiving.posZ)
                                .offset(entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks,
                                        entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks,
                                        entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks)
                                .offset(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ);

                        double[][] boxVertices = {
                                {bb.minX, bb.minY, bb.minZ},
                                {bb.minX, bb.maxY, bb.minZ},
                                {bb.maxX, bb.maxY, bb.minZ},
                                {bb.maxX, bb.minY, bb.minZ},
                                {bb.minX, bb.minY, bb.maxZ},
                                {bb.minX, bb.maxY, bb.maxZ},
                                {bb.maxX, bb.maxY, bb.maxZ},
                                {bb.maxX, bb.minY, bb.maxZ},
                        };

                        float minX = mc.displayWidth;
                        float minY = mc.displayHeight;

                        float maxX = 0;
                        float maxY = 0;

                        for (double[] boxVertex : boxVertices) {
                            Vector2f screenPos = WorldToScreen.worldToScreen(new Vector3f((float) boxVertex[0], (float) boxVertex[1], (float) boxVertex[2]), mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight);

                            if (screenPos == null) {
                                continue;
                            }

                            minX = Math.min(screenPos.x, minX);
                            minY = Math.min(screenPos.y, minY);

                            maxX = Math.max(screenPos.x, maxX);
                            maxY = Math.max(screenPos.y, maxY);
                        }

                        if (!(minX >= mc.displayWidth || minY >= mc.displayHeight || maxX <= 0 || maxY <= 0)) {
                            if (real2dOutline.get()) {
                                GL11.glLineWidth(2f);
                                GL11.glColor4f(0f, 0f, 0f, 1.0f);

                                if (real2dcsgo.get()) {
                                    float distX = (maxX - minX) / 3f;
                                    float distY = (maxY - minY) / 3f;

                                    GL11.glBegin(GL11.GL_LINE_STRIP);

                                    GL11.glVertex2f(minX, minY + distY);
                                    GL11.glVertex2f(minX, minY);
                                    GL11.glVertex2f(minX + distX, minY);

                                    GL11.glEnd();

                                    GL11.glBegin(GL11.GL_LINE_STRIP);

                                    GL11.glVertex2f(minX, maxY - distY);
                                    GL11.glVertex2f(minX, maxY);
                                    GL11.glVertex2f(minX + distX, maxY);

                                    GL11.glEnd();

                                    GL11.glBegin(GL11.GL_LINE_STRIP);

                                    GL11.glVertex2f(maxX - distX, minY);
                                    GL11.glVertex2f(maxX, minY);
                                    GL11.glVertex2f(maxX, minY + distY);

                                    GL11.glEnd();

                                    GL11.glBegin(GL11.GL_LINE_STRIP);

                                    GL11.glVertex2f(maxX - distX, maxY);
                                    GL11.glVertex2f(maxX, maxY);
                                    GL11.glVertex2f(maxX, maxY - distY);

                                    GL11.glEnd();
                                } else {
                                    GL11.glBegin(GL11.GL_LINE_LOOP);

                                    GL11.glVertex2f(minX, minY);
                                    GL11.glVertex2f(minX, maxY);
                                    GL11.glVertex2f(maxX, maxY);
                                    GL11.glVertex2f(maxX, minY);

                                    GL11.glEnd();
                                }

                                GL11.glLineWidth(1.0f);
                            }

                            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);

                            if (real2dcsgo.get()) {
                                float distX = (maxX - minX) / 3f;
                                float distY = (maxY - minY) / 3f;

                                GL11.glBegin(GL11.GL_LINE_STRIP);

                                GL11.glVertex2f(minX, minY + distY);
                                GL11.glVertex2f(minX, minY);
                                GL11.glVertex2f(minX + distX, minY);

                                GL11.glEnd();

                                GL11.glBegin(GL11.GL_LINE_STRIP);

                                GL11.glVertex2f(minX, maxY - distY);
                                GL11.glVertex2f(minX, maxY);
                                GL11.glVertex2f(minX + distX, maxY);

                                GL11.glEnd();

                                GL11.glBegin(GL11.GL_LINE_STRIP);

                                GL11.glVertex2f(maxX - distX, minY);
                                GL11.glVertex2f(maxX, minY);
                                GL11.glVertex2f(maxX, minY + distY);

                                GL11.glEnd();

                                GL11.glBegin(GL11.GL_LINE_STRIP);

                                GL11.glVertex2f(maxX - distX, maxY);
                                GL11.glVertex2f(maxX, maxY);
                                GL11.glVertex2f(maxX, maxY - distY);

                                GL11.glEnd();
                            } else {
                                GL11.glBegin(GL11.GL_LINE_LOOP);

                                GL11.glVertex2f(minX, minY);
                                GL11.glVertex2f(minX, maxY);
                                GL11.glVertex2f(maxX, maxY);
                                GL11.glVertex2f(maxX, minY);

                                GL11.glEnd();
                            }

                            if (real2dShowHealth.get()) {
                                float barHeight = (maxY - minY) * (1 - entityLiving.getHealth() / entityLiving.getMaxHealth());

                                GL11.glColor4f(0.1f, 1f, 0.1f, 1f);

                                GL11.glBegin(GL11.GL_QUADS);

                                GL11.glVertex2f(maxX + 2, minY + barHeight);
                                GL11.glVertex2f(maxX + 2, maxY);
                                GL11.glVertex2f(maxX + 4, maxY);
                                GL11.glVertex2f(maxX + 4, minY + barHeight);

                                GL11.glEnd();

                                GL11.glColor4f(1f, 1f, 1f, 1f);
                                glEnable(GL_TEXTURE_2D);
                                glEnable(GL_DEPTH_TEST);

                                mc.fontRendererObj.drawStringWithShadow(decimalFormat.format(entityLiving.getHealth()) + " HP", maxX + 4, minY + barHeight, -1);

                                glDisable(GL_TEXTURE_2D);
                                glDisable(GL_DEPTH_TEST);

                                GlStateManager.resetColor();
                            }

                            if (real2dShowHeldItem.get() && entityLiving.getHeldItem() != null && entityLiving.getHeldItem().getItem() != null) {
                                glEnable(GL_TEXTURE_2D);
                                glEnable(GL_DEPTH_TEST);

                                int stringWidth = mc.fontRendererObj.getStringWidth(entityLiving.getHeldItem().getDisplayName());
                                mc.fontRendererObj.drawStringWithShadow(entityLiving.getHeldItem().getDisplayName(), minX + (maxX - minX) / 2 - (stringWidth / 2), maxY + 2, -1);

                                glDisable(GL_TEXTURE_2D);
                                glDisable(GL_DEPTH_TEST);
                            }

                            if (real2dShowName.get()) {
                                glEnable(GL_TEXTURE_2D);
                                glEnable(GL_DEPTH_TEST);

                                int stringWidth = mc.fontRendererObj.getStringWidth(entityLiving.getDisplayName().getFormattedText());
                                mc.fontRendererObj.drawStringWithShadow(entityLiving.getDisplayName().getFormattedText(), minX + (maxX - minX) / 2 - (stringWidth / 2), minY - 12, -1);

                                glDisable(GL_TEXTURE_2D);
                                glDisable(GL_DEPTH_TEST);
                            }
                        }

                        break;
                    }
                }
            }
        }

        if (real2d) {
            glEnable(GL_DEPTH_TEST);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();

            GL11.glPopAttrib();
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final String mode = modeValue.get().toLowerCase();

        final FramebufferShader shader = mode.equalsIgnoreCase("shaderoutline")
                ? OutlineShader.OUTLINE_SHADER : mode.equalsIgnoreCase("shaderglow")
                ? GlowShader.GLOW_SHADER : null;

        if (shader == null) return;

        shader.startDraw(event.getPartialTicks());

        renderNameTags = false;

        try {
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (!EntityUtils.isSelected(entity, false))
                    continue;

                mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
            }
        } catch (final Exception ex) {
            ClientUtils.getLogger().error("An error occurred while rendering all entities for shader esp", ex);
        }

        renderNameTags = true;

        final float radius = mode.equalsIgnoreCase("shaderoutline")
                ? shaderOutlineRadius.get() : mode.equalsIgnoreCase("shaderglow")
                ? shaderGlowRadius.get() : 1F;

        shader.stopDraw(getColor(null), radius, 1F);
    }

    public final Color getColor(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

            if (colorModeValue.get().equalsIgnoreCase("Health"))
                return BlendUtils.getHealthColor(entityLivingBase.getHealth(), entityLivingBase.getMaxHealth());

            if (entityLivingBase.hurtTime > 0)
                return Color.RED;

            if (EntityUtils.isFriend(entityLivingBase))
                return Color.BLUE;

            if (colorTeam.get()) {
                final char[] chars = entityLivingBase.getDisplayName().getFormattedText().toCharArray();
                int color = Integer.MAX_VALUE;

                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] != '§' || i + 1 >= chars.length)
                        continue;

                    final int index = GameFontRenderer.getColorIndex(chars[i + 1]);

                    if (index < 0 || index > 15)
                        continue;

                    color = ColorUtils.hexColors[index];
                    break;
                }

                return new Color(color);
            }
        }

        switch (colorModeValue.get()) {
            case "Custom":
                return new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
            case "Rainbow":
                return new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
            case "Sky":
                return RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
            case "LiquidSlowly":
                return ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
            case "Mixer":
                return ColorMixer.getMixedColor(0, mixerSecondsValue.get());
            case "Fade":
                return ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
            default:
                return Color.white;
        }
    }
}
