package net.aspw.nightx.injection.forge.mixins.render;

import net.aspw.nightx.NightX;
import net.aspw.nightx.features.module.modules.client.Chams;
import net.aspw.nightx.features.module.modules.client.ColorMixer;
import net.aspw.nightx.features.module.modules.client.SilentView;
import net.aspw.nightx.features.module.modules.client.TwoDTags;
import net.aspw.nightx.features.module.modules.render.NameTags;
import net.aspw.nightx.features.module.modules.render.NoRender;
import net.aspw.nightx.features.module.modules.render.PlayerEdit;
import net.aspw.nightx.features.module.modules.render.ShowInvis;
import net.aspw.nightx.utils.EntityUtils;
import net.aspw.nightx.utils.RotationUtils;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity extends MixinRender {

    @Shadow
    protected ModelBase mainModel;

    @Shadow
    private static final Logger logger = LogManager.getLogger();

    @Shadow
    protected <T extends EntityLivingBase> float getDeathMaxRotation(T p_getDeathMaxRotation_1_) {
        return 90.0F;
    }

    @Shadow
    protected abstract float interpolateRotation(float par1, float par2, float par3);

    @Shadow
    protected boolean renderOutlines = false;

    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks);

    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setScoreTeamColor(T entityLivingBaseIn);

    @Shadow
    protected abstract<T extends EntityLivingBase> float getSwingProgress(T livingBase, float partialTickTime);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract <T extends EntityLivingBase> void preRenderCallback(T entitylivingbaseIn, float partialTickTime);

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLivingAt(T entityLivingBaseIn, double x, double y, double z);

    @Shadow
    protected abstract<T extends EntityLivingBase> float handleRotationFloat(T livingBase, float partialTicks);

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_);

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected <T extends EntityLivingBase> void rotateCorpse(T p_rotateCorpse_1_, float p_rotateCorpse_2_, float p_rotateCorpse_3_, float p_rotateCorpse_4_) {
        final PlayerEdit playerEdit = NightX.moduleManager.getModule(PlayerEdit.class);
        GlStateManager.rotate(180.0F - p_rotateCorpse_3_, 0.0F, 1.0F, 0.0F);
        if (p_rotateCorpse_1_.deathTime > 0) {
            float f = ((float)p_rotateCorpse_1_.deathTime + p_rotateCorpse_4_ - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt_float(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            GlStateManager.rotate(f * this.getDeathMaxRotation(p_rotateCorpse_1_), 0.0F, 0.0F, 1.0F);
        } else {
            String s = EnumChatFormatting.getTextWithoutFormattingCodes(p_rotateCorpse_1_.getName());
            if (s != null && (PlayerEdit.rotatePlayer.get() && p_rotateCorpse_1_.equals(Minecraft.getMinecraft().thePlayer) && NightX.moduleManager.get(PlayerEdit.class).getState()) && (!(p_rotateCorpse_1_ instanceof EntityPlayer) || ((EntityPlayer)p_rotateCorpse_1_).isWearing(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0F, p_rotateCorpse_1_.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void injectChamsPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        final Chams chams = NightX.moduleManager.getModule(Chams.class);
        final NoRender noRender = NightX.moduleManager.getModule(NoRender.class);

        if (noRender.getState() && noRender.shouldStopRender(entity)) {
            callbackInfo.cancel();
            return;
        }

        if (chams.getState() && chams.getTargetsValue().get() && chams.getLegacyMode().get() && ((chams.getLocalPlayerValue().get() && entity == Minecraft.getMinecraft().thePlayer) || EntityUtils.isSelected(entity, false))) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("RETURN"))
    private <T extends EntityLivingBase> void injectChamsPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        final Chams chams = NightX.moduleManager.getModule(Chams.class);
        final NoRender noRender = NightX.moduleManager.getModule(NoRender.class);

        if (chams.getState() && chams.getTargetsValue().get() && chams.getLegacyMode().get() && ((chams.getLocalPlayerValue().get() && entity == Minecraft.getMinecraft().thePlayer) || EntityUtils.isSelected(entity, false))
                && !(noRender.getState() && noRender.shouldStopRender(entity))) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void canRenderName(T entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final NoRender noRender = NightX.moduleManager.getModule(NoRender.class);
        final TwoDTags twoDTags = NightX.moduleManager.getModule(TwoDTags.class);

        if (NightX.moduleManager.getModule(NameTags.class).getState() && ((NightX.moduleManager.getModule(NameTags.class).getLocalValue().get() && entity == Minecraft.getMinecraft().thePlayer && (!NightX.moduleManager.getModule(NameTags.class).getNfpValue().get() || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) || EntityUtils.isSelected(entity, false))
                || (noRender.getState() && noRender.getNameTagsValue().get()) || twoDTags.getState() && twoDTags.tagsValue.get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"))
    private <T extends EntityLivingBase> void injectFakeBody(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = entity.isRiding();
        this.mainModel.isChild = entity.isChild();

        try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;

            if (entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity.ridingEntity;
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f2 = f1 - f;
                float f3 = MathHelper.wrapAngleTo180_float(f2);

                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;

                if (f3 * f3 > 2500.0F) {
                    f += f3 * 0.2F;
                }
            }

            float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f8 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f8, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entity, partialTicks);
            float f4 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

            if (entity.isChild()) {
                f6 *= 3.0F;
            }

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625F, entity);

            if (this.renderOutlines) {
                boolean flag1 = this.setScoreTeamColor(entity);
                this.renderModels(entity, f6, f5, f8, f2, f7, 0.0625F);

                if (flag1) {
                    this.unsetScoreTeamColor();
                }
            } else {

                boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModels(entity, f6, f5, f8, f2, f7, 0.0625F);

                if (flag) {
                    this.unsetBrightness();
                }

                GlStateManager.depthMask(true);

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                    this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, 0.0625F);
                }
            }


            SilentView rotations = (SilentView) NightX.moduleManager.getModule(SilentView.class);
            final int blend = 3042;
            final int depth = 2929;
            final int srcAlpha = 770;
            final int srcAlphaPlus1 = srcAlpha + 1;
            final int polygonOffsetLine = 10754;
            final int texture2D = 3553;
            final int lighting = 2896; // 250.36f / 255, 250.36f / 255, 250.36f / 255, 4.64f / 255
            float renderpitch = (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 && rotations.getState() && rotations.getMode().get().equals("CSGO") && entity == Minecraft.getMinecraft().thePlayer) ? (entity.prevRotationPitch + (((RotationUtils.serverRotation.getPitch() != 0.0f) ? RotationUtils.serverRotation.getPitch() : entity.rotationPitch) - entity.prevRotationPitch)) : (entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks);
            float renderyaw = (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 && rotations.getState() && rotations.getMode().get().equals("CSGO") && entity == Minecraft.getMinecraft().thePlayer) ? (entity.prevRotationYaw + (((RotationUtils.serverRotation.getYaw() != 0.0f) ? RotationUtils.serverRotation.getYaw() : entity.rotationYaw) - entity.prevRotationYaw)) : (entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks);

            if (entity == Minecraft.getMinecraft().thePlayer && rotations.getState() && rotations.getMode().get().equals("CSGO") && rotations.shouldRotate()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(250.36f / 255, 250.36f / 255, 250.36f / 255, 4.64f / 255);
                GlStateManager.popMatrix();
            }

            if (rotations.getState() && rotations.getMode().get().equals("CSGO") && entity.equals(Minecraft.getMinecraft().thePlayer) && rotations.shouldRotate()) {
                GL11.glPushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glDisable(2929);
                GL11.glDisable(3553);
                GL11.glDisable(3553);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2896);
                GL11.glPolygonMode(1032, 6914);
                if (Minecraft.getMinecraft().thePlayer.hurtTime > 0) {
                    GL11.glColor4f(rotations.getR().get() / 255, rotations.getG().get() / 255, rotations.getB().get() / 255, rotations.getAlpha().get() / 255);
                } else {
                    GL11.glColor4f(rotations.getR().get() / 255, rotations.getG().get() / 255, rotations.getB().get() / 255, rotations.getAlpha().get() / 255);
                }
                GL11.glRotatef(renderyaw - f, 0, 0.001f, 0);
                this.mainModel.render(Minecraft.getMinecraft().thePlayer, f6, f5, renderpitch, f2, renderpitch, 0.0625F);
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
            logger.error((String) "Couldn\'t render entity", (Throwable) exception);
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
     * @author CCBlueX
     */

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    protected <T extends EntityLivingBase> void renderModel(T p_renderModel_1_, float p_renderModel_2_, float p_renderModel_3_, float p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_, CallbackInfo ci) {
        boolean visible = !p_renderModel_1_.isInvisible();
        final ShowInvis trueSight = NightX.moduleManager.getModule(ShowInvis.class);
        final Chams chams = NightX.moduleManager.getModule(Chams.class);
        boolean chamsFlag = (chams.getState() && chams.getTargetsValue().get() && !chams.getLegacyMode().get() && ((chams.getLocalPlayerValue().get() && p_renderModel_1_ == Minecraft.getMinecraft().thePlayer) || EntityUtils.isSelected(p_renderModel_1_, false)));
        boolean semiVisible = !visible && (!p_renderModel_1_.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) || (trueSight.getState() && trueSight.getEntitiesValue().get()));

        if (visible || semiVisible) {
            if (!this.bindEntityTexture(p_renderModel_1_))
                return;

            if (semiVisible) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            final int blend = 3042;
            final int depth = 2929;
            final int srcAlpha = 770;
            final int srcAlphaPlus1 = srcAlpha + 1;
            final int polygonOffsetLine = 10754;
            final int texture2D = 3553;
            final int lighting = 2896;

            boolean textured = chams.getTexturedValue().get();

            Color chamsColor = new Color(0x00000000);

            switch (chams.getColorModeValue().get()) {
                case "Custom":
                    chamsColor = new Color(chams.getRedValue().get(), chams.getGreenValue().get(), chams.getBlueValue().get());
                    break;
                case "Rainbow":
                    chamsColor = new Color(RenderUtils.getRainbowOpaque(chams.getMixerSecondsValue().get(), chams.getSaturationValue().get(), chams.getBrightnessValue().get(), 0));
                    break;
                case "Sky":
                    chamsColor = RenderUtils.skyRainbow(0, chams.getSaturationValue().get(), chams.getBrightnessValue().get());
                    break;
                case "LiquidSlowly":
                    chamsColor = ColorUtils.LiquidSlowly(System.nanoTime(), 0, chams.getSaturationValue().get(), chams.getBrightnessValue().get());
                    break;
                case "Mixer":
                    chamsColor = ColorMixer.getMixedColor(0, chams.getMixerSecondsValue().get());
                    break;
                case "Fade":
                    chamsColor = ColorUtils.fade(new Color(chams.getRedValue().get(), chams.getGreenValue().get(), chams.getBlueValue().get(), chams.getAlphaValue().get()), 0, 100);
                    break;
            }

            chamsColor = ColorUtils.reAlpha(chamsColor, chams.getAlphaValue().get());

            if (chamsFlag) {
                Color chamsColor2 = new Color(0x00000000);

                switch (chams.getBehindColorModeValue().get()) {
                    case "Same":
                        chamsColor2 = chamsColor;
                        break;
                    case "Opposite":
                        chamsColor2 = ColorUtils.getOppositeColor(chamsColor);
                        break;
                    case "Red":
                        chamsColor2 = new Color(0xffEF2626);
                        break;
                }

                GL11.glPushMatrix();
                GL11.glEnable(polygonOffsetLine);
                GL11.glPolygonOffset(1.0F, 1000000.0F);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

                if (!textured) {
                    GL11.glEnable(blend);
                    GL11.glDisable(texture2D);
                    GL11.glDisable(lighting);
                    GL11.glBlendFunc(srcAlpha, srcAlphaPlus1);
                    GL11.glColor4f(chamsColor2.getRed() / 255.0F, chamsColor2.getGreen() / 255.0F, chamsColor2.getBlue() / 255.0F, chamsColor2.getAlpha() / 255.0F);
                }

                GL11.glDisable(depth);
                GL11.glDepthMask(false);
            }

            this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

            if (chamsFlag) {
                GL11.glEnable(depth);
                GL11.glDepthMask(true);

                if (!textured) {
                    GL11.glColor4f(chamsColor.getRed() / 255.0F, chamsColor.getGreen() / 255.0F, chamsColor.getBlue() / 255.0F, chamsColor.getAlpha() / 255.0F);
                }

                this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

                if (!textured) {
                    GL11.glEnable(texture2D);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(blend);
                    GL11.glEnable(lighting);
                }

                GL11.glPolygonOffset(1.0f, -1000000.0f);
                GL11.glDisable(polygonOffsetLine);
                GL11.glPopMatrix();
            }

            if (semiVisible) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }

        ci.cancel();
    }
    protected <T extends EntityLivingBase> void renderModels(T p_renderModel_1_, float p_renderModel_2_, float p_renderModel_3_, float p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_) {
        boolean visible = !p_renderModel_1_.isInvisible();
        final ShowInvis trueSight = NightX.moduleManager.getModule(ShowInvis.class);
        final Chams chams = NightX.moduleManager.getModule(Chams.class);
        boolean chamsFlag = (chams.getState() && chams.getTargetsValue().get() && !chams.getLegacyMode().get() && ((chams.getLocalPlayerValue().get() && p_renderModel_1_ == Minecraft.getMinecraft().thePlayer) || EntityUtils.isSelected(p_renderModel_1_, false)));
        boolean semiVisible = !visible && (!p_renderModel_1_.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) || (trueSight.getState() && trueSight.getEntitiesValue().get()));

        if (visible || semiVisible) {
            if (!this.bindEntityTexture(p_renderModel_1_))
                return;

            if (semiVisible) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            final int blend = 3042;
            final int depth = 2929;
            final int srcAlpha = 770;
            final int srcAlphaPlus1 = srcAlpha + 1;
            final int polygonOffsetLine = 10754;
            final int texture2D = 3553;
            final int lighting = 2896;

            boolean textured = chams.getTexturedValue().get();

            Color chamsColor = new Color(0x00000000);

            switch (chams.getColorModeValue().get()) {
                case "Custom":
                    chamsColor = new Color(chams.getRedValue().get(), chams.getGreenValue().get(), chams.getBlueValue().get());
                    break;
                case "Rainbow":
                    chamsColor = new Color(RenderUtils.getRainbowOpaque(chams.getMixerSecondsValue().get(), chams.getSaturationValue().get(), chams.getBrightnessValue().get(), 0));
                    break;
                case "Sky":
                    chamsColor = RenderUtils.skyRainbow(0, chams.getSaturationValue().get(), chams.getBrightnessValue().get());
                    break;
                case "LiquidSlowly":
                    chamsColor = ColorUtils.LiquidSlowly(System.nanoTime(), 0, chams.getSaturationValue().get(), chams.getBrightnessValue().get());
                    break;
                case "Mixer":
                    chamsColor = ColorMixer.getMixedColor(0, chams.getMixerSecondsValue().get());
                    break;
                case "Fade":
                    chamsColor = ColorUtils.fade(new Color(chams.getRedValue().get(), chams.getGreenValue().get(), chams.getBlueValue().get(), chams.getAlphaValue().get()), 0, 100);
                    break;
            }

            chamsColor = ColorUtils.reAlpha(chamsColor, chams.getAlphaValue().get());

            if (chamsFlag) {
                Color chamsColor2 = new Color(0x00000000);

                switch (chams.getBehindColorModeValue().get()) {
                    case "Same":
                        chamsColor2 = chamsColor;
                        break;
                    case "Opposite":
                        chamsColor2 = ColorUtils.getOppositeColor(chamsColor);
                        break;
                    case "Red":
                        chamsColor2 = new Color(0xffEF2626);
                        break;
                }

                glPushMatrix();
                GL11.glEnable(polygonOffsetLine);
                GL11.glPolygonOffset(1.0F, 1000000.0F);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

                if (!textured) {
                    GL11.glEnable(blend);
                    GL11.glDisable(texture2D);
                    GL11.glDisable(lighting);
                    GL11.glBlendFunc(srcAlpha, srcAlphaPlus1);
                    GL11.glColor4f(chamsColor2.getRed() / 255.0F, chamsColor2.getGreen() / 255.0F, chamsColor2.getBlue() / 255.0F, chamsColor2.getAlpha() / 255.0F);
                }

                GL11.glDisable(depth);
                GL11.glDepthMask(false);
            }

            this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

            if (chamsFlag) {
                GL11.glEnable(depth);
                GL11.glDepthMask(true);

                if (!textured) {
                    GL11.glColor4f(chamsColor.getRed() / 255.0F, chamsColor.getGreen() / 255.0F, chamsColor.getBlue() / 255.0F, chamsColor.getAlpha() / 255.0F);
                }

                this.mainModel.render(p_renderModel_1_, p_renderModel_2_, p_renderModel_3_, p_renderModel_4_, p_renderModel_5_, p_renderModel_6_, p_renderModel_7_);

                if (!textured) {
                    GL11.glEnable(texture2D);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(blend);
                    GL11.glEnable(lighting);
                }

                GL11.glPolygonOffset(1.0f, -1000000.0f);
                GL11.glDisable(polygonOffsetLine);
                GL11.glPopMatrix();
            }

            if (semiVisible) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }
}