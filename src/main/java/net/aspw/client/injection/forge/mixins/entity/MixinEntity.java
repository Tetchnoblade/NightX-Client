package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Launch;
import net.aspw.client.event.StrafeEvent;
import net.aspw.client.features.module.impl.combat.HitBox;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.MinecraftInstance;
import net.aspw.client.utils.PredictUtils;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(Entity.class)
public abstract class MixinEntity implements ICommandSender {

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public float rotationPitch;
    @Shadow
    public float rotationYaw;
    @Shadow
    public Entity ridingEntity;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public boolean onGround;
    @Shadow
    public boolean isAirBorne;
    @Shadow
    public boolean noClip;
    @Shadow
    public World worldObj;
    @Shadow
    public boolean isInWeb;
    @Shadow
    public float stepHeight;
    @Shadow
    public boolean isCollidedHorizontally;
    @Shadow
    public boolean isCollidedVertically;
    @Shadow
    public boolean isCollided;
    @Shadow
    public float distanceWalkedModified;
    @Shadow
    public float distanceWalkedOnStepModified;
    @Shadow
    public int fireResistance;
    @Shadow
    public int timeUntilPortal;
    @Shadow
    public float width;
    @Shadow
    protected Random rand;
    @Shadow
    protected boolean inPortal;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private int fire;
    @Shadow(remap = false)
    private CapabilityDispatcher capabilities;

    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Shadow
    public abstract void setEntityBoundingBox(AxisAlignedBB bb);

    @Shadow
    public abstract float getDistanceToEntity(Entity entityIn);

    @Shadow
    public abstract boolean isInWater();

    @Shadow
    public abstract boolean isRiding();

    @Shadow
    protected abstract void dealFireDamage(int amount);

    @Shadow
    public abstract boolean isWet();

    @Shadow
    public abstract void addEntityCrashInfo(CrashReportCategory category);

    @Shadow
    protected abstract void doBlockCollisions();

    @Shadow
    protected abstract void playStepSound(BlockPos pos, Block blockIn);

    public int getNextStepDistance() {
        return nextStepDistance;
    }

    public void setNextStepDistance(int nextStepDistance) {
        this.nextStepDistance = nextStepDistance;
    }

    public int getFire() {
        return fire;
    }

    @Shadow
    public abstract void setFire(int seconds);

    @Shadow
    protected abstract boolean getFlag(int p_getFlag_1_);

    /**
     * @author As_pw
     * @reason Sneaking Fix
     */
    @Overwrite
    public boolean isSneaking() {
        return this.getFlag(1);
    }

    @Shadow
    public void moveEntity(double x, double y, double z) {
    }

    @Inject(method = "getCollisionBorderSize", at = @At("HEAD"), cancellable = true)
    private void getCollisionBorderSize(final CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final HitBox hitBox = Objects.requireNonNull(Launch.moduleManager.getModule(HitBox.class));

        if (hitBox.getState() && EntityUtils.isSelected(((Entity) ((Object) this)), true))
            callbackInfoReturnable.setReturnValue(0.1F + hitBox.getSizeValue().get());
    }

    /**
     * @author As_pw
     * @reason Strafe Event
     */
    @Overwrite
    public void moveFlying(float strafe, float forward, float friction) {
        float rotationYaw = this.rotationYaw;
        if ((Object) this == MinecraftInstance.mc.thePlayer) {

            final StrafeEvent strafeEvent = new StrafeEvent(strafe, forward, friction, rotationYaw);
            Launch.eventManager.callEvent(strafeEvent);

            if (strafeEvent.isCancelled())
                return;

            strafe = strafeEvent.getStrafe();
            forward = strafeEvent.getForward();
            friction = strafeEvent.getFriction();
            rotationYaw = strafeEvent.getYaw();
        }

        float f = strafe * strafe + forward * forward;

        if (!(f < 1.0E-4F)) {
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper.sin(rotationYaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(rotationYaw * (float) Math.PI / 180.0F);
            this.motionX += strafe * f2 - forward * f1;
            this.motionZ += forward * f2 + strafe * f1;
        }
    }

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void isInWater(final CallbackInfoReturnable<Boolean> cir) {
        final Flight flight = Objects.requireNonNull(Launch.moduleManager.getModule(Flight.class));

        if (flight.getState() && flight.modeValue.get().contains("FakeWater"))
            cir.setReturnValue(true);
    }

    @Redirect(method = "getBrightnessForRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/BlockPos;)Z"))
    public boolean alwaysReturnTrue(World world, BlockPos pos) {
        return true;
    }

    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void spawnRunningParticles(CallbackInfo ci) {
        if (!this.onGround || PredictUtils.predicting) ci.cancel();
    }

    /**
     * @author As_pw
     * @reason Fix Capabilities
     */
    @Overwrite(remap = false)
    public boolean hasCapability(Capability<?> capability, EnumFacing direction) {
        return this.capabilities != null && this.capabilities.hasCapability(capability, direction);
    }
}