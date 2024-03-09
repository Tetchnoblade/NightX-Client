package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Launch;
import net.aspw.client.features.module.impl.visual.Cape;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;

import java.util.Objects;

/**
 * The type Mixin layer arrow.
 */
@Mixin(LayerCape.class)
public class MixinLayerCape {

    @Mutable
    @Final
    @Shadow
    private final RenderPlayer playerRenderer;

    public MixinLayerCape(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
    }

    /**
     * @author As_pw
     * @reason Improve Cape Renderer
     */
    @Overwrite
    public void doRenderLayer(final AbstractClientPlayer entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        final Cape cape = Objects.requireNonNull(Launch.moduleManager.getModule(Cape.class));
        if (!entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)) {
            if (entitylivingbaseIn == MinecraftInstance.mc.thePlayer) {
                if (entitylivingbaseIn.getLocationCape() != null || cape.getCustomCape().get()) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
                    GL11.glPushMatrix();
                    GL11.glTranslatef(0.0F, 0.0F, 0.125F);
                    double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double) partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double) partialTicks);
                    double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double) partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double) partialTicks);
                    double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double) partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double) partialTicks);
                    float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
                    double d3 = MathHelper.sin(f * 3.1415927F / 180.0F);
                    double d4 = -MathHelper.cos(f * 3.1415927F / 180.0F);
                    float f1 = (float) d1 * 10.0F;
                    f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
                    float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                    float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

                    if (f2 < 0.0F) {
                        f2 = 0.0F;
                    }

                    if (f2 > 165.0F) {
                        f2 = 165.0F;
                    }

                    if (f1 < -5.0F) {
                        f1 = -5.0F;
                    }

                    float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
                    f1 += MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

                    if (entitylivingbaseIn.isSneaking()) {
                        f1 += 25.0F;
                        GlStateManager.translate(0.0F, 0.142F, -0.0178F);
                    }

                    GL11.glRotatef(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    this.playerRenderer.getMainModel().renderCape(0.0625F);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            } else if (entitylivingbaseIn.getLocationCape() != null) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 0.0F, 0.125F);
                double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double) partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double) partialTicks);
                double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double) partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double) partialTicks);
                double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double) partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double) partialTicks);
                float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
                double d3 = MathHelper.sin(f * 3.1415927F / 180.0F);
                double d4 = -MathHelper.cos(f * 3.1415927F / 180.0F);
                float f1 = (float) d1 * 10.0F;
                f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
                float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                if (f2 > 165.0F) {
                    f2 = 165.0F;
                }

                if (f1 < -5.0F) {
                    f1 = -5.0F;
                }

                float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
                f1 += MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

                if (entitylivingbaseIn.isSneaking()) {
                    f1 += 25.0F;
                    GlStateManager.translate(0.0F, 0.142F, -0.0178F);
                }

                GL11.glRotatef(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                this.playerRenderer.getMainModel().renderCape(0.0625F);
                GL11.glPopMatrix();
            }
        }
    }

    /**
     * @author As_pw
     * @reason Cape Fix
     */
    @Overwrite
    public boolean shouldCombineTextures() {
        return false;
    }
}
