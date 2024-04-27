package net.aspw.client.features.module.impl.visual;

import net.aspw.client.Launch;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Render2DEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.features.module.impl.other.MurdererDetector;
import net.aspw.client.features.module.impl.targets.AntiBots;
import net.aspw.client.features.module.impl.targets.AntiTeams;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.value.BoolValue;
import net.aspw.client.visual.font.smooth.FontLoaders;
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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ModuleInfo(name = "ESP", category = ModuleCategory.VISUAL)
public final class ESP extends Module {
    public static List<Entity> collectedEntities = new ArrayList<>();
    public final BoolValue localPlayer = new BoolValue("Local-Player", false);
    private final IntBuffer viewport;
    private final FloatBuffer modelview;
    private final FloatBuffer projection;
    private final FloatBuffer vector;

    public ESP() {
        this.viewport = GLAllocation.createDirectIntBuffer(16);
        this.modelview = GLAllocation.createDirectFloatBuffer(16);
        this.projection = GLAllocation.createDirectFloatBuffer(16);
        this.vector = GLAllocation.createDirectFloatBuffer(4);
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
        int i = 0;

        for (int collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; ++i) {
            Entity entity = collectedEntities.get(i);
            if (RenderUtils.isInViewFrustrum(entity)) {
                double x = RenderUtils.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                double y = RenderUtils.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                double z = RenderUtils.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                double width = (double) entity.width / 1.5D;
                double height = (double) entity.height + (entity.isSneaking() ? -0.3D : 0.2D);
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));
                entityRenderer.setupCameraTransform(partialTicks, 0);
                Vector4d position = null;

                for (Vector3d vector : vectors) {
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

                    boolean living = entity instanceof EntityLivingBase;
                    EntityLivingBase entityLivingBase;

                    if (living) {
                        final MurdererDetector murdererDetector = Objects.requireNonNull(Launch.moduleManager.getModule(MurdererDetector.class));
                        final AntiTeams antiTeams = Objects.requireNonNull(Launch.moduleManager.getModule(AntiTeams.class));
                        final AntiBots antiBots = Objects.requireNonNull(Launch.moduleManager.getModule(AntiBots.class));
                        entityLivingBase = (EntityLivingBase) entity;
                        String entName;
                        if (murdererDetector.getState() && MurdererDetector.isMurderer(entityLivingBase)) {
                            entName = "§c[Murderer] §7- §r" + entityLivingBase.getDisplayName().getFormattedText();
                        } else if (EntityUtils.isFriend(entity)) {
                            entName = "§e[Friend] §7- §r" + entityLivingBase.getDisplayName().getFormattedText();
                        } else if (antiBots.getState() && AntiBots.isBot(entityLivingBase)) {
                            entName = "§c[Bot] §7- §r" + entityLivingBase.getDisplayName().getFormattedText();
                        } else if (antiTeams.getState() && antiTeams.isInYourTeam(entityLivingBase)) {
                            entName = "§e[Team] §7- §r" + entityLivingBase.getDisplayName().getFormattedText();
                        } else {
                            entName = entityLivingBase.getDisplayName().getFormattedText();
                        }
                        try {
                            drawScaledCenteredString(entName, posX + (endPosX - posX) / 2F, posY - 1F - FontLoaders.SF21.getHeight() * 1F);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GlStateManager.enableBlend();
        GlStateManager.resetColor();
        entityRenderer.setupOverlayRendering();
    }

    private void drawScaledString(String text, double x, double y) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, x);
        GlStateManager.scale(1.0, 1.0, 1.0);
        FontLoaders.SF21.drawStringWithShadow(text, 0, 0, -1);
        GlStateManager.popMatrix();
    }

    private void drawScaledCenteredString(String text, double x, double y) {
        drawScaledString(text, x - FontLoaders.SF21.getStringWidth(text) / 2F * 1.0, y);
    }

    public static boolean shouldCancelNameTag(EntityLivingBase entity) {
        return Objects.requireNonNull(Launch.moduleManager.getModule(ESP.class)).getState() && collectedEntities.contains(entity);
    }

    private void collectEntities() {
        collectedEntities.clear();
        List<Entity> playerEntities = mc.theWorld.loadedEntityList;
        int i = 0;

        for (int playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; ++i) {
            Entity entity = playerEntities.get(i);
            if (entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP) && !entity.isInvisible() && !((EntityPlayer) entity).isSpectator() || (localPlayer.get() && entity instanceof EntityPlayerSP && mc.gameSettings.thirdPersonView != 0)) {
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