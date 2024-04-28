package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Launch;
import net.aspw.client.event.JumpEvent;
import net.aspw.client.features.module.impl.movement.Jesus;
import net.aspw.client.features.module.impl.movement.NoJumpDelay;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.VisualAbilities;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow
    protected boolean isJumping;
    @Shadow
    public int jumpTicks;
    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveForward;
    @Shadow
    protected abstract float getJumpUpwardsMotion();
    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion potionIn);
    @Shadow
    public abstract boolean isPotionActive(Potion potionIn);
    @Shadow
    public void onLivingUpdate() {
    }
    @Shadow
    protected abstract void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos);
    @Shadow
    public abstract float getHealth();
    @Shadow
    public abstract ItemStack getHeldItem();
    @Shadow
    protected abstract void updateAITick();
    @Shadow
    protected void updateEntityActionState() {
    }

    /**
     * @author As_pw
     * @reason Jump Event
     */
    @Overwrite
    protected void jump() {
        final JumpEvent jumpEvent = new JumpEvent(this.getJumpUpwardsMotion(), this.rotationYaw);
        Launch.eventManager.callEvent(jumpEvent);
        if (jumpEvent.isCancelled())
            return;

        this.motionY = jumpEvent.getMotion();

        if (this.isPotionActive(Potion.jump))
            this.motionY += (float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;

        if (this.isSprinting()) {
            float f = jumpEvent.getYaw() * 0.017453292F;

            this.motionX -= (MathHelper.sin(f) * 0.2F);
            this.motionZ += (MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (Objects.requireNonNull(Launch.moduleManager.getModule(NoJumpDelay.class)).getState())
            jumpTicks = 0;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;isJumping:Z", ordinal = 1))
    private void onJumpSection(CallbackInfo callbackInfo) {
        final Jesus jesus = Objects.requireNonNull(Launch.moduleManager.getModule(Jesus.class));

        if (jesus.getState() && !isJumping && !isSneaking() && isInWater() &&
                jesus.modeValue.get().equalsIgnoreCase("Swim")) {
            this.updateAITick();
        }
    }

    @Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
    private void isPotionActive(Potion p_isPotionActive_1_, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final VisualAbilities visualAbilities = Objects.requireNonNull(Launch.moduleManager.getModule(VisualAbilities.class));

        if ((p_isPotionActive_1_ == Potion.confusion || p_isPotionActive_1_ == Potion.blindness) && visualAbilities.getState() && visualAbilities.getConfusionEffect().get())
            callbackInfoReturnable.setReturnValue(false);
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