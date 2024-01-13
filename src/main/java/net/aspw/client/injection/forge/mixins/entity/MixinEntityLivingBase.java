package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.event.JumpEvent;
import net.aspw.client.features.module.impl.movement.Jesus;
import net.aspw.client.features.module.impl.movement.NoJumpDelay;
import net.aspw.client.features.module.impl.movement.Sprint;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.SilentView;
import net.aspw.client.features.module.impl.visual.VisualAbilities;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.MovementUtils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Objects;

/**
 * The type Mixin entity living base.
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    /**
     * The Swing progress int.
     */
    @Shadow
    public int swingProgressInt;
    /**
     * The Is swing in progress.
     */
    @Shadow
    public boolean isSwingInProgress;
    /**
     * The Swing progress.
     */
    @Shadow
    public float swingProgress;
    /**
     * The Is jumping.
     */
    @Shadow
    protected boolean isJumping;
    /**
     * The Jump ticks.
     */
    @Shadow
    public int jumpTicks;

    /**
     * Gets jump upwards motion.
     *
     * @return the jump upwards motion
     */
    @Shadow
    protected abstract float getJumpUpwardsMotion();

    /**
     * Gets active potion effect.
     *
     * @param potionIn the potion in
     * @return the active potion effect
     */
    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    /**
     * Is potion active boolean.
     *
     * @param potionIn the potion in
     * @return the boolean
     */
    @Shadow
    public abstract boolean isPotionActive(Potion potionIn);

    /**
     * On living update.
     */
    @Shadow
    public void onLivingUpdate() {
    }

    /**
     * Update fall state.
     *
     * @param y          the y
     * @param onGroundIn the on ground in
     * @param blockIn    the block in
     * @param pos        the pos
     */
    @Shadow
    protected abstract void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos);

    /**
     * Gets health.
     *
     * @return the health
     */
    @Shadow
    public abstract float getHealth();

    /**
     * Gets held item.
     *
     * @return the held item
     */
    @Shadow
    public abstract ItemStack getHeldItem();

    /**
     * Update ai tick.
     */
    @Shadow
    protected abstract void updateAITick();

    /**
     * The Render yaw offset.
     */
    @Shadow
    public float renderYawOffset;

    /**
     * The Rotation yaw head.
     */
    @Shadow
    public float rotationYawHead;

    @Shadow
    public float prevRotationYawHead;

    @Shadow
    public float prevRenderYawOffset;

    /**
     * Update distance float.
     *
     * @param p_1101461 the p 1101461
     * @param p_1101462 the p 1101462
     * @return the float
     * @author As_pw
     * @reason SilentView
     */
    @Overwrite
    protected float updateDistance(float p_1101461, float p_1101462) {
        float rotationYaw = this.rotationYaw;
        final SilentView silentView = Objects.requireNonNull(Client.moduleManager.getModule(SilentView.class));
        if ((silentView.getRotationMode().get().equals("Normal") || silentView.getRotationMode().get().equals("Old")) && silentView.getState() && silentView.getPlayerYaw() != null && (EntityLivingBase) (Object) this instanceof EntityPlayerSP) {
            if (this.swingProgress > 0F && !silentView.getBodyLockValue().get()) {
                p_1101461 = silentView.getPlayerYaw();
            }
            rotationYaw = silentView.getPlayerYaw();
            rotationYawHead = silentView.getPlayerYaw();
        }
        float f = MathHelper.wrapAngleTo180_float(p_1101461 - this.renderYawOffset);
        if ((silentView.getRotationMode().get().equals("Normal") || silentView.getRotationMode().get().equals("Old")) && silentView.getBodyLockValue().get() && silentView.getState() && silentView.getPlayerYaw() != null && (EntityLivingBase) (Object) this instanceof EntityPlayerSP)
            this.renderYawOffset += f;
        else this.renderYawOffset += f * 0.3F;
        float f1 = MathHelper.wrapAngleTo180_float(rotationYaw - this.renderYawOffset);
        boolean flag = f1 < 90.0F || f1 >= 90.0F;

        if ((silentView.getRotationMode().get().equals("Normal") || silentView.getRotationMode().get().equals("Old")) && silentView.getBodyLockValue().get() && silentView.getState() && silentView.getPlayerYaw() != null && (EntityLivingBase) (Object) this instanceof EntityPlayerSP) {
            f1 = 0.0F;
        }

        if (f1 < -75.0F) {
            f1 = -75.0F;
        }

        if (f1 >= 75.0F) {
            f1 = 75.0F;
        }

        this.renderYawOffset = rotationYaw - f1;

        if (f1 * f1 > 2500.0F) {
            this.renderYawOffset += f1 * 0.2F;
        }

        if (flag) {
            p_1101462 *= -1.0F;
        }

        return p_1101462;
    }

    /**
     * Jump.
     *
     * @author As_pw
     * @reason Jump
     */
    @Overwrite
    protected void jump() {
        final JumpEvent jumpEvent = new JumpEvent(this.getJumpUpwardsMotion(), this.rotationYaw);
        Client.eventManager.callEvent(jumpEvent);
        if (jumpEvent.isCancelled())
            return;

        this.motionY = jumpEvent.getMotion();

        if (this.isPotionActive(Potion.jump))
            this.motionY += (float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;

        if (this.isSprinting()) {
            final Sprint sprint = Objects.requireNonNull(Client.moduleManager.getModule(Sprint.class));
            if (sprint.getState() && sprint.getAllDirectionsValue().get())
                jumpEvent.setYaw(MovementUtils.getRawDirection());
            float f = jumpEvent.getYaw() * ((float) Math.PI / 180F);
            this.motionX -= MathHelper.sin(f) * 0.2F;
            this.motionZ += MathHelper.cos(f) * 0.2F;
        }

        this.isAirBorne = true;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (Objects.requireNonNull(Client.moduleManager.getModule(NoJumpDelay.class)).getState())
            jumpTicks = 0;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;isJumping:Z", ordinal = 1))
    private void onJumpSection(CallbackInfo callbackInfo) {
        final Jesus jesus = Objects.requireNonNull(Client.moduleManager.getModule(Jesus.class));

        if (jesus.getState() && !isJumping && !isSneaking() && isInWater() &&
                jesus.modeValue.get().equalsIgnoreCase("Swim")) {
            this.updateAITick();
        }
    }

    @Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
    private void isPotionActive(Potion p_isPotionActive_1_, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final VisualAbilities visualAbilities = Objects.requireNonNull(Client.moduleManager.getModule(VisualAbilities.class));

        if ((p_isPotionActive_1_ == Potion.confusion || p_isPotionActive_1_ == Potion.blindness) && visualAbilities.getState() && visualAbilities.getConfusionEffect().get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.005D))
    private double ViaVersion_MovementThreshold(double constant) {
        if (ProtocolBase.getManager().getTargetVersion().isNewerThan(VersionEnum.r1_8) && !MinecraftInstance.mc.isIntegratedServerRunning())
            return 0.003D;
        return 0.005D;
    }

    /**
     * @author As_pw
     * @reason VisionFX
     */
    @Overwrite
    private int getArmSwingAnimationEnd() {
        int speed = (EntityLivingBase) (Object) this instanceof EntityPlayerSP ? 2 + (20 - Animations.SpeedSwing.get() - 16) : 6;
        return this.isPotionActive(Potion.digSpeed) ? speed - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) : (this.isPotionActive(Potion.digSlowdown) ? speed + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : speed);
    }
}