package net.aspw.client.features.module.impl.visual;

import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.features.module.impl.targets.AntiBots;
import net.aspw.client.util.render.BlendUtils;
import net.aspw.client.util.render.RenderUtils;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.FloatValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "ESP", description = "", category = ModuleCategory.VISUAL)
public final class ESP extends Module {
    public static List collectedEntities = new ArrayList();
    public final BoolValue tagsValue = new BoolValue("Tags", true);
    public final BoolValue healthBar = new BoolValue("Health-Bar", true);
    public final BoolValue localPlayer = new BoolValue("Local-Player", false);
    private final FloatValue fontScaleValue = new FloatValue("Font-Scale", 1F, 0F, 1F, "x");
    private final IntBuffer viewport;
    private final FloatBuffer modelview;
    private final FloatBuffer projection;
    private final FloatBuffer vector;
    private final int backgroundColor;

    private final DecimalFormat dFormat = new DecimalFormat("0.0");

    public ESP() {
        this.viewport = GLAllocation.createDirectIntBuffer(16);
        this.modelview = GLAllocation.createDirectFloatBuffer(16);
        this.projection = GLAllocation.createDirectFloatBuffer(16);
        this.vector = GLAllocation.createDirectFloatBuffer(4);
        this.backgroundColor = new Color(0, 0, 0, 120).getRGB();
        int black = Color.BLACK.getRGB();
    }

    @Override
    public void onDisable() {
        collectedEntities.clear();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        GL11.glPushMatrix();
        this.collectEntities();
        float partialTicks = event.getPartialTicks();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        double scaling = (double) scaleFactor / Math.pow(scaleFactor, 2.0D);
        GL11.glScaled(scaling, scaling, scaling);
        RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        boolean health = this.healthBar.get();
        int i = 0;

        for (int collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; ++i) {
            Entity entity = (Entity) collectedEntities.get(i);
            if (RenderUtils.isInViewFrustrum(entity)) {
                double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                double y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                double z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                double width = (double) entity.width / 1.5D;
                double height = (double) entity.height + (entity.isSneaking() ? -0.3D : 0.2D);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                List vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                entityRenderer.setupCameraTransform(partialTicks, 0);
                Vector4d position = null;

                for (Object o : vectors) {
                    Vector3d vector = (Vector3d) o;
                    vector = this.project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                    if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0D);
                        }

                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                if (position != null) {
                    entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;

                    boolean living = entity instanceof EntityLivingBase;
                    EntityLivingBase entityLivingBase;
                    float armorValue;
                    float itemDurability;
                    double durabilityWidth;
                    double textWidth;
                    if (living) {
                        entityLivingBase = (EntityLivingBase) entity;
                        if (health) {
                            armorValue = entityLivingBase.getHealth();
                            itemDurability = entityLivingBase.getMaxHealth();
                            if (armorValue > itemDurability)
                                armorValue = itemDurability;

                            durabilityWidth = armorValue / itemDurability;
                            textWidth = (endPosY - posY) * durabilityWidth;
                            RenderUtils.newDrawRect(posX - 3.5D, posY - 0.5D, posX - 1.5D, endPosY + 0.5D, this.backgroundColor);
                            if (armorValue > 0.0F) {
                                int healthColor = BlendUtils.getHealthColor(armorValue, itemDurability).getRGB();
                                RenderUtils.newDrawRect(posX - 3.0D, endPosY, posX - 2.0D, endPosY - textWidth, healthColor);
                            }
                        }
                    }

                    if (living && tagsValue.get()) {
                        entityLivingBase = (EntityLivingBase) entity;
                        String entName = entityLivingBase.getName();
                        drawScaledCenteredString(entName, posX + (endPosX - posX) / 2F, posY - 1F - mc.fontRendererObj.FONT_HEIGHT * fontScaleValue.get(), fontScaleValue.get(), -1);
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        GlStateManager.resetColor();
        entityRenderer.setupOverlayRendering();
    }

    private void drawScaledString(String text, double x, double y, double scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(scale, scale, scale);
        mc.fontRendererObj.drawStringWithShadow(text, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private void drawScaledCenteredString(String text, double x, double y, double scale, int color) {
        drawScaledString(text, x - mc.fontRendererObj.getStringWidth(text) / 2F * scale, y, scale, color);
    }

    private void collectEntities() {
        collectedEntities.clear();
        List playerEntities = mc.theWorld.loadedEntityList;
        int i = 0;

        for (int playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; ++i) {
            Entity entity = (Entity) playerEntities.get(i);
            if (entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP) && !entity.isInvisible() && !((EntityPlayer) entity).isSpectator() && !AntiBots.isBot((EntityPlayer) entity) || (localPlayer.get() && entity instanceof EntityPlayerSP && mc.gameSettings.thirdPersonView != 0)) {
                collectedEntities.add(entity);
            }
        }

    }

    private Vector3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.vector) ? new Vector3d(this.vector.get(0) / (float) scaleFactor, ((float) Display.getHeight() - this.vector.get(1)) / (float) scaleFactor, this.vector.get(2)) : null;
    }
}