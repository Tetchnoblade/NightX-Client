package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.Client;
import net.aspw.client.features.module.impl.visual.*;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.render.ColorUtils;
import net.aspw.client.utils.render.RenderUtils;
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

import static org.lwjgl.opengl.GL11.glPushMatrix;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity extends MixinRender {
    @Shadow
    private static final Logger logger = LogManager.getLogger();
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected boolean renderOutlines = false;

    @Shadow
    protected <T extends EntityLivingBase> float getDeathMaxRotation(T p_getDeathMaxRotation_1_) {
        return 90.0F;
    }

    @Shadow
    protected abstract float interpolateRotation(float par1, float par2, float par3);

    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks);

    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setScoreTeamColor(T entityLivingBaseIn);

    @Shadow
    protected abstract <T extends EntityLivingBase> float getSwingProgress(T livingBase, float partialTickTime);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract <T extends EntityLivingBase> void preRenderCallback(T entitylivingbaseIn, float partialTickTime);

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLivingAt(T entityLivingBaseIn, double x, double y, double z);

    @Shadow
    protected abstract <T extends EntityLivingBase> float handleRotationFloat(T livingBase, float partialTicks);

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_);

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected <T extends EntityLivingBase> void rotateCorpse(T p_rotateCorpse_1_, float p_rotateCorpse_2_, float p_rotateCorpse_3_, float p_rotateCorpse_4_) {
        final PlayerEdit playerEdit = Client.moduleManager.getModule(PlayerEdit.class);
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
            if (s != null && (PlayerEdit.rotatePlayer.get() && p_rotateCorpse_1_.equals(Minecraft.getMinecraft().thePlayer) && Client.moduleManager.get(PlayerEdit.class).getState()) && (!(p_rotateCorpse_1_ instanceof EntityPlayer) || ((EntityPlayer) p_rotateCorpse_1_).isWearing(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0F, p_rotateCorpse_1_.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void injectChamsPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        final Chams chams = Client.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getTargetsValue().get() && chams.getLegacyMode().get() && ((chams.getLocalPlayerValue().get() && entity == Minecraft.getMinecraft().thePlayer) || EntityUtils.isSelected(entity, false))) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V", at = @At("RETURN"))
    private <T extends EntityLivingBase> void injectChamsPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        final Chams chams = Client.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getTargetsValue().get() && chams.getLegacyMode().get() && ((chams.getLocalPlayerValue().get() && entity == Minecraft.getMinecraft().thePlayer) || EntityUtils.isSelected(entity, false))) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void canRenderName(T entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Client.moduleManager.getModule(ESP.class).getState() && Client.moduleManager.getModule(ESP.class).tagsValue.get()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    /**
     * @author CCBlueX
     */

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    protected <T extends EntityLivingBase> void renderModel(T p_renderModel_1_, float p_renderModel_2_, float p_renderModel_3_, float p_renderModel_4_, float p_renderModel_5_, float p_renderModel_6_, float p_renderModel_7_, CallbackInfo ci) {
        boolean visible = !p_renderModel_1_.isInvisible();
        final ShowInvis trueSight = Client.moduleManager.getModule(ShowInvis.class);
        final Chams chams = Client.moduleManager.getModule(Chams.class);
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
        final ShowInvis trueSight = Client.moduleManager.getModule(ShowInvis.class);
        final Chams chams = Client.moduleManager.getModule(Chams.class);
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