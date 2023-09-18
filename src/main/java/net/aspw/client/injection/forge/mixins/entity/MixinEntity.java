package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.event.StrafeEvent;
import net.aspw.client.features.module.impl.combat.HitBox;
import net.aspw.client.features.module.impl.movement.AntiWaterPush;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.features.module.impl.other.InfinitePitch;
import net.aspw.client.protocol.Protocol;
import net.aspw.client.util.EntityUtils;
import net.aspw.client.util.MinecraftInstance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
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
import java.util.UUID;

/**
 * The type Mixin entity.
 */
@Mixin(Entity.class)
public abstract class MixinEntity {

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
     * Is sneaking boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isSneaking();

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

    @Inject(method = "getCollisionBorderSize", at = @At("HEAD"), cancellable = true)
    private void getCollisionBorderSize(final CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final HitBox hitBoxes = Objects.requireNonNull(Client.moduleManager.getModule(HitBox.class));

        if (hitBoxes.getState() && EntityUtils.isSelected(((Entity) ((Object) this)), true)) {
            if (!MinecraftInstance.mc.isIntegratedServerRunning() && !Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x")) {
                callbackInfoReturnable.setReturnValue(hitBoxes.getSizeValue().get());
            } else {
                callbackInfoReturnable.setReturnValue(0.1F + hitBoxes.getSizeValue().get());
            }
        } else if (!MinecraftInstance.mc.isIntegratedServerRunning() && !Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x")) {
            callbackInfoReturnable.setReturnValue(0.0F);
        }
    }

    @Inject(method = "setAngles", at = @At("HEAD"), cancellable = true)
    private void setAngles(final float yaw, final float pitch, final CallbackInfo callbackInfo) {
        if (Objects.requireNonNull(Client.moduleManager.getModule(InfinitePitch.class)).getState()) {
            callbackInfo.cancel();

            float f = this.rotationPitch;
            float f1 = this.rotationYaw;
            this.rotationYaw = (float) ((double) this.rotationYaw + (double) yaw * 0.15D);
            this.rotationPitch = (float) ((double) this.rotationPitch - (double) pitch * 0.15D);
            this.prevRotationPitch += this.rotationPitch - f;
            this.prevRotationYaw += this.rotationYaw - f1;
        }
    }

    @Inject(method = "moveFlying", at = @At("HEAD"), cancellable = true)
    private void handleRotations(float strafe, float forward, float friction, final CallbackInfo callbackInfo) {
        if ((Object) this != MinecraftInstance.mc.thePlayer)
            return;

        final StrafeEvent strafeEvent = new StrafeEvent(strafe, forward, friction);
        Client.eventManager.callEvent(strafeEvent);

        if (strafeEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void isInWater(final CallbackInfoReturnable<Boolean> cir) {
        final AntiWaterPush antiWaterPush = Objects.requireNonNull(Client.moduleManager.getModule(AntiWaterPush.class));
        final Flight flight = Objects.requireNonNull(Client.moduleManager.getModule(Flight.class));

        if (antiWaterPush.getState() && antiWaterPush.getWaterValue().get()) {
            cir.setReturnValue(false);
            return;
        }
        if (flight.getState() && flight.modeValue.get().contains("Water"))
            cir.setReturnValue(true);
    }

    @Inject(method = "isInLava", at = @At("HEAD"), cancellable = true)
    private void isInLava(final CallbackInfoReturnable<Boolean> cir) {
        final AntiWaterPush antiWaterPush = Objects.requireNonNull(Client.moduleManager.getModule(AntiWaterPush.class));

        if (antiWaterPush.getState() && antiWaterPush.getLavaValue().get()) {
            cir.setReturnValue(false);
        }
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