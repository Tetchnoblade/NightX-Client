package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Launch;
import net.aspw.client.event.StrafeEvent;
import net.aspw.client.features.module.impl.combat.HitBox;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.EntityUtils;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.raphimc.vialoader.util.VersionEnum;
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
import java.util.UUID;

/**
 * The type Mixin entity.
 */
@Mixin(Entity.class)
public abstract class MixinEntity implements ICommandSender {

    /**
     * The Pos x.
     */
    @Shadow
    public double posX;

    /**
     * The Pos y.
     */
    @Shadow
    public double posY;

    /**
     * The Pos z.
     */
    @Shadow
    public double posZ;
    /**
     * The Rotation pitch.
     */
    @Shadow
    public float rotationPitch;
    /**
     * The Rotation yaw.
     */
    @Shadow
    public float rotationYaw;
    /**
     * The Riding entity.
     */
    @Shadow
    public Entity ridingEntity;
    /**
     * The Motion x.
     */
    @Shadow
    public double motionX;
    /**
     * The Motion y.
     */
    @Shadow
    public double motionY;
    /**
     * The Motion z.
     */
    @Shadow
    public double motionZ;
    /**
     * The On ground.
     */
    @Shadow
    public boolean onGround;
    /**
     * The Is air borne.
     */
    @Shadow
    public boolean isAirBorne;
    /**
     * The No clip.
     */
    @Shadow
    public boolean noClip;
    /**
     * The World obj.
     */
    @Shadow
    public World worldObj;
    /**
     * The Is in web.
     */
    @Shadow
    public boolean isInWeb;
    /**
     * The Step height.
     */
    @Shadow
    public float stepHeight;
    /**
     * The Is collided horizontally.
     */
    @Shadow
    public boolean isCollidedHorizontally;
    /**
     * The Is collided vertically.
     */
    @Shadow
    public boolean isCollidedVertically;
    /**
     * The Is collided.
     */
    @Shadow
    public boolean isCollided;
    /**
     * The Distance walked modified.
     */
    @Shadow
    public float distanceWalkedModified;
    /**
     * The Distance walked on step modified.
     */
    @Shadow
    public float distanceWalkedOnStepModified;
    /**
     * The Fire resistance.
     */
    @Shadow
    public int fireResistance;
    /**
     * The Time until portal.
     */
    @Shadow
    public int timeUntilPortal;
    /**
     * The Width.
     */
    @Shadow
    public float width;
    /**
     * The Prev rotation pitch.
     */
    @Shadow
    public float prevRotationPitch;
    /**
     * The Prev rotation yaw.
     */
    @Shadow
    public float prevRotationYaw;
    /**
     * The Rand.
     */
    @Shadow
    protected Random rand;
    /**
     * The In portal.
     */
    @Shadow
    protected boolean inPortal;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private int fire;
    @Shadow(remap = false)
    private CapabilityDispatcher capabilities;

    /**
     * Is sprinting boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isSprinting();

    /**
     * Gets entity bounding box.
     *
     * @return the entity bounding box
     */
    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    /**
     * Sets entity bounding box.
     *
     * @param bb the bb
     */
    @Shadow
    public abstract void setEntityBoundingBox(AxisAlignedBB bb);

    /**
     * Gets distance to entity.
     *
     * @param entityIn the entity in
     * @return the distance to entity
     */
    @Shadow
    public abstract float getDistanceToEntity(Entity entityIn);

    /**
     * Move entity.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    @Shadow
    public void moveEntity(double x, double y, double z) {
    }

    /**
     * Is in water boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isInWater();

    /**
     * Is riding boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isRiding();

    /**
     * Deal fire damage.
     *
     * @param amount the amount
     */
    @Shadow
    protected abstract void dealFireDamage(int amount);

    /**
     * Is wet boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isWet();

    /**
     * Add entity crash info.
     *
     * @param category the category
     */
    @Shadow
    public abstract void addEntityCrashInfo(CrashReportCategory category);

    /**
     * Do block collisions.
     */
    @Shadow
    protected abstract void doBlockCollisions();

    /**
     * Play step sound.
     *
     * @param pos     the pos
     * @param blockIn the block in
     */
    @Shadow
    protected abstract void playStepSound(BlockPos pos, Block blockIn);

    /**
     * Gets vector for rotation.
     *
     * @param pitch the pitch
     * @param yaw   the yaw
     * @return the vector for rotation
     */
    @Shadow
    protected abstract Vec3 getVectorForRotation(float pitch, float yaw);

    /**
     * Gets unique id.
     *
     * @return the unique id
     */
    @Shadow
    public abstract UUID getUniqueID();

    /**
     * Is inside of material boolean.
     *
     * @param materialIn the material in
     * @return the boolean
     */
    @Shadow
    public abstract boolean isInsideOfMaterial(Material materialIn);

    /**
     * Gets next step distance.
     *
     * @return the next step distance
     */
    public int getNextStepDistance() {
        return nextStepDistance;
    }

    /**
     * Sets next step distance.
     *
     * @param nextStepDistance the next step distance
     */
    public void setNextStepDistance(int nextStepDistance) {
        this.nextStepDistance = nextStepDistance;
    }

    /**
     * Gets fire.
     *
     * @return the fire
     */
    public int getFire() {
        return fire;
    }

    /**
     * Sets fire.
     *
     * @param seconds the seconds
     */
    @Shadow
    public abstract void setFire(int seconds);

    @Shadow
    public abstract float getEyeHeight();

    @Shadow
    public abstract Vec3 getLook(float p_getLook_1_);

    @Shadow
    protected abstract boolean getFlag(int p_getFlag_1_);

    @Inject(method = "getCollisionBorderSize", at = @At("HEAD"), cancellable = true)
    private void getCollisionBorderSize(final CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final HitBox hitBoxes = Objects.requireNonNull(Launch.moduleManager.getModule(HitBox.class));

        if (hitBoxes.getState() && EntityUtils.isSelected(((Entity) ((Object) this)), true)) {
            if (ProtocolBase.getManager().getTargetVersion().isNewerThan(VersionEnum.r1_8) && !MinecraftInstance.mc.isIntegratedServerRunning()) {
                callbackInfoReturnable.setReturnValue(hitBoxes.getSizeValue().get());
            } else {
                callbackInfoReturnable.setReturnValue(0.1F + hitBoxes.getSizeValue().get());
            }
        } else if (ProtocolBase.getManager().getTargetVersion().isNewerThan(VersionEnum.r1_8) && !MinecraftInstance.mc.isIntegratedServerRunning()) {
            callbackInfoReturnable.setReturnValue(0.0F);
        }
    }

    /**
     * @author As_pw
     * @reason Sneak
     */
    @Overwrite
    public boolean isSneaking() {
        return this.getFlag(1);
    }

    /**
     * @author As_pw
     * @reason Event
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

    /**
     * Always return true boolean.
     *
     * @param world the world
     * @param pos   the pos
     * @return the boolean
     */
    @Redirect(method = "getBrightnessForRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/BlockPos;)Z"))
    public boolean alwaysReturnTrue(World world, BlockPos pos) {
        return true;
    }

    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void checkGroundState(CallbackInfo ci) {
        if (!this.onGround) ci.cancel();
    }

    /**
     * Has capability boolean.
     *
     * @param capability the capability
     * @param direction  the direction
     * @return the boolean
     * @author As_pw
     * @reason Fix Capability
     */
    @Overwrite(remap = false)
    public boolean hasCapability(Capability<?> capability, EnumFacing direction) {
        return this.capabilities != null && this.capabilities.hasCapability(capability, direction);
    }
}