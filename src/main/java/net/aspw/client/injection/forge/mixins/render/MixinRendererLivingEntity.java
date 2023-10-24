package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Client;
import net.aspw.client.features.api.PacketManager;
import net.aspw.client.features.module.impl.other.PlayerEdit;
import net.aspw.client.features.module.impl.visual.ESP;
import net.aspw.client.features.module.impl.visual.SilentView;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.RotationUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * The type Mixin renderer living entity.
 */
@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity extends MixinRender {
    @Final
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    /**
     * The Main model.
     */
    @Shadow
    protected ModelBase mainModel;
    /**
     * The Render outlines.
     */
    @Shadow
    protected boolean renderOutlines = false;

    /**
     * Gets death max rotation.
     *
     * @param <T>                      the type parameter
     * @param p_getDeathMaxRotation_1_ the p get death max rotation 1
     * @return the death max rotation
     */
    @Shadow
    protected <T extends EntityLivingBase> float getDeathMaxRotation(T p_getDeathMaxRotation_1_) {
        return 90.0F;
    }

    /**
     * Interpolate rotation float.
     *
     * @param par1 the par 1
     * @param par2 the par 2
     * @param par3 the par 3
     * @return the float
     */
    @Shadow
    protected abstract float interpolateRotation(float par1, float par2, float par3);

    /**
     * Sets do render brightness.
     *
     * @param <T>                the type parameter
     * @param entityLivingBaseIn the entity living base in
     * @param partialTicks       the partial ticks
     * @return the do render brightness
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks);

    /**
     * Sets score team color.
     *
     * @param <T>                the type parameter
     * @param entityLivingBaseIn the entity living base in
     * @return the score team color
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setScoreTeamColor(T entityLivingBaseIn);

    /**
     * Gets swing progress.
     *
     * @param <T>             the type parameter
     * @param livingBase      the living base
     * @param partialTickTime the partial tick time
     * @return the swing progress
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> float getSwingProgress(T livingBase, float partialTickTime);

    /**
     * Unset score team color.
     */
    @Shadow
    protected abstract void unsetScoreTeamColor();

    /**
     * Pre render callback.
     *
     * @param <T>                the type parameter
     * @param entitylivingbaseIn the entitylivingbase in
     * @param partialTickTime    the partial tick time
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> void preRenderCallback(T entitylivingbaseIn, float partialTickTime);

    /**
     * Unset brightness.
     */
    @Shadow
    protected abstract void unsetBrightness();

    /**
     * Render living at.
     *
     * @param <T>                the type parameter
     * @param entityLivingBaseIn the entity living base in
     * @param x                  the x
     * @param y                  the y
     * @param z                  the z
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLivingAt(T entityLivingBaseIn, double x, double y, double z);

    /**
     * Handle rotation float float.
     *
     * @param <T>          the type parameter
     * @param livingBase   the living base
     * @param partialTicks the partial ticks
     * @return the float
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> float handleRotationFloat(T livingBase, float partialTicks);

    /**
     * Render layers.
     *
     * @param <T>                the type parameter
     * @param entitylivingbaseIn the entitylivingbase in
     * @param p_177093_2_        the p 177093 2
     * @param p_177093_3_        the p 177093 3
     * @param partialTicks       the partial ticks
     * @param p_177093_5_        the p 177093 5
     * @param p_177093_6_        the p 177093 6
     * @param p_177093_7_        the p 177093 7
     * @param p_177093_8_        the p 177093 8
     */
    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_);

    /**
     * Rotate corpse.
     *
     * @param <T>               the type parameter
     * @param p_rotateCorpse_1_ the p rotate corpse 1
     * @param p_rotateCorpse_2_ the p rotate corpse 2
     * @param p_rotateCorpse_3_ the p rotate corpse 3
     * @param p_rotateCorpse_4_ the p rotate corpse 4
     * @author As_pw
     * @reason RotateCorpse
     */
    @Overwrite
    protected <T extends EntityLivingBase> void rotateCorpse(T p_rotateCorpse_1_, float p_rotateCorpse_2_, float p_rotateCorpse_3_, float p_rotateCorpse_4_) {
        final PlayerEdit playerEdit = Objects.requireNonNull(Client.moduleManager.getModule(PlayerEdit.class));
        GlStateManager.rotate(180.0F - p_rotateCorpse_3_, 0.0F, 1.0F, 0.0F);
        if (p_rotateCorpse_1_.deathTime > 0) {
            float f = ((float) p_rotateCorpse_1_.deathTime + p_rotateCorpse_4_ - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt_float(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            GlStateManager.rotate(f * this.getDeathMaxRotation(p_rotateCorpse_1_), 0.0F, 0.0F, 1.0F);
        } else {
            String s = EnumChatFormatting.getTextWithoutFormattingCodes(p_rotateCorpse_1_.getName());
            if (s != null && (PlayerEdit.rotatePlayer.get() && p_rotateCorpse_1_.equals(MinecraftInstance.mc.thePlayer) && playerEdit.getState()) && (!(p_rotateCorpse_1_ instanceof EntityPlayer) || ((EntityPlayer) p_rotateCorpse_1_).isWearing(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0F, p_rotateCorpse_1_.height + PlayerEdit.yPos.get() - 1.8F, 0.0F);
                GlStateManager.rotate(PlayerEdit.xRot.get(), 0.0F, 0.0F, 1.0F);
            }
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void injectChamsPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        if (PacketManager.shouldStopRender(entity))
            callbackInfo.cancel();
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void canRenderName(T entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Objects.requireNonNull(Client.moduleManager.getModule(ESP.class)).getState() && Objects.requireNonNull(Client.moduleManager.getModule(ESP.class)).tagsValue.get())
            callbackInfoReturnable.setReturnValue(false);
    }

    /**
     * @author As_pw
     * @reason Rotations
     */
    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"))
    private <T extends EntityLivingBase> void injectFakeBody(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        boolean shouldSit;
        SilentView rotations = Objects.requireNonNull(Client.moduleManager.getModule(SilentView.class));
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = shouldSit = entity.isRiding() && entity.ridingEntity != null
                && entity.ridingEntity.shouldRiderSit();
        this.mainModel.isChild = entity.isChild();

        try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f8 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float f2 = f1 - f;
            if (shouldSit && entity.ridingEntity instanceof EntityLivingBase) {
                float f3;
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity.ridingEntity;
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset,
                        partialTicks);
                if ((f3 = MathHelper.wrapAngleTo180_float(f2 = f1 - f)) < -85.0f) {
                    f3 = -85.0f;
                }
                if (f3 >= 85.0f) {
                    f3 = 85.0f;
                }
                f = f1 - f3;
                if (f3 * f3 > 2500.0f) {
                    f += f3 * 0.2f;
                }
            }

            renderLivingAt(entity, x, y, z);
            float f7 = this.handleRotationFloat(entity, partialTicks);
            float f5 = entity.prevLimbSwingAmount
                    + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            boolean flag = this.setDoRenderBrightness(entity, partialTicks);

            rotateCorpse(entity, f7, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            preRenderCallback(entity, partialTicks);
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);

            if (entity.isChild())
                f6 *= 3.0F;

            if (f5 > 1.0F)
                f5 = 1.0F;

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f7, f2, f8, 0.0625F, entity);

            if (this.renderOutlines) {
                boolean flag1 = setScoreTeamColor(entity);
                renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);

                if (flag1)
                    unsetScoreTeamColor();
            } else {
                renderModel(entity, f6, f5, f7, f2, f8, 0.0625F);
                if (flag)
                    unsetBrightness();

                GlStateManager.depthMask(true);

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                    renderLayers(entity, f6, f5, partialTicks, f7, f2, f8, 0.0625F);
                }
            }

            float renderpitch = (MinecraftInstance.mc.gameSettings.thirdPersonView != 0 && rotations.getState() && rotations.getSilentValue().get() && entity == MinecraftInstance.mc.thePlayer) ? (entity.prevRotationPitch + (((RotationUtils.serverRotation.getPitch() != 0.0f) ? RotationUtils.serverRotation.getPitch() : entity.rotationPitch) - entity.prevRotationPitch)) : (entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks);
            float renderyaw = (MinecraftInstance.mc.gameSettings.thirdPersonView != 0 && rotations.getState() && rotations.getSilentValue().get() && entity == MinecraftInstance.mc.thePlayer) ? (entity.prevRotationYaw + (((RotationUtils.serverRotation.getYaw() != 0.0f) ? RotationUtils.serverRotation.getYaw() : entity.rotationYaw) - entity.prevRotationYaw)) : (entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks);

            if (rotations.getState() && rotations.getSilentValue().get() && entity.equals(MinecraftInstance.mc.thePlayer) && rotations.shouldRotate()) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glDisable(2929);
                GL11.glDisable(3553);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2896);
                GL11.glPolygonMode(1032, 6914);
                if (MinecraftInstance.mc.thePlayer.hurtTime > 0)
                    GL11.glColor4f(255, 0, 0, 8);
                else GL11.glColor4f(255, 200, 0, 8f);
                GL11.glRotatef(renderyaw - f, 0, 0.001f, 0);
                this.mainModel.render(MinecraftInstance.mc.thePlayer, f6, f5, renderpitch, f2, renderpitch, 0.0625F);
                GL11.glEnable(2896);
                GL11.glDisable(3042);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glColor3d(1, 1, 1);
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }

            GlStateManager.disableRescaleNormal();

        } catch (Exception exception) {
            logger.error("Couldn't render entity", exception);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

        if (!this.renderOutlines) {
            super.doRenders(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * @author As_pw
     * @reason Fix Renderer
     */

    @Overwrite
    protected <T extends EntityLivingBase> void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor) {
        boolean visible = !entitylivingbaseIn.isInvisible();
        boolean semiVisible = !visible && (!entitylivingbaseIn.isInvisibleToPlayer(MinecraftInstance.mc.thePlayer));
        boolean silent = entitylivingbaseIn == MinecraftInstance.mc.thePlayer && Objects.requireNonNull(Client.moduleManager.getModule(SilentView.class)).getState() && Objects.requireNonNull(Client.moduleManager.getModule(SilentView.class)).getSilentValue().get() && Objects.requireNonNull(Client.moduleManager.getModule(SilentView.class)).shouldRotate();

        if (visible || semiVisible || silent) {
            if (!this.bindEntityTexture(entitylivingbaseIn))
                return;

            if (semiVisible) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            if (silent) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.2F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);

            if (semiVisible) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }

            if (silent) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.15F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }
}