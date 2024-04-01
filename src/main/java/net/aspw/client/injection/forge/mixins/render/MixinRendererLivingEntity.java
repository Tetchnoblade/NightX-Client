package net.aspw.client.injection.forge.mixins.render;

import net.aspw.client.features.module.impl.visual.ESP;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", at = @At("HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void canRenderName(T entity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (ESP.shouldCancelNameTag(entity))
            callbackInfoReturnable.setReturnValue(false);
    }

    /**
     * @author As_pw
     * @reason Fix Renderer
     */
    @Overwrite
    protected <T extends EntityLivingBase> void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor) {
        boolean visible = !entitylivingbaseIn.isInvisible();
        boolean semiVisible = !visible && (!entitylivingbaseIn.isInvisibleToPlayer(MinecraftInstance.mc.thePlayer));

        if (visible || semiVisible) {
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

            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);

            if (semiVisible) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }
}