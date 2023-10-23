package net.aspw.client.injection.forge.mixins.entity;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.event.StepConfirmEvent;
import net.aspw.client.event.StepEvent;
import net.aspw.client.event.StrafeEvent;
import net.aspw.client.features.module.impl.combat.HitBox;
import net.aspw.client.features.module.impl.movement.AntiWaterPush;
import net.aspw.client.features.module.impl.movement.Flight;
import net.aspw.client.features.module.impl.other.InfinitePitch;
import net.aspw.client.injection.access.IEntity;
import net.aspw.client.protocol.Protocol;
import net.aspw.client.util.EntityUtils;
import net.aspw.client.util.MinecraftInstance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
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

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * The type Mixin entity.
 */
@Mixin({Entity.class})
public abstract class MixinEntity implements IEntity {

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

    @Shadow
    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
    }

    @Shadow
    protected abstract boolean canTriggerWalking();

    @Shadow
    public void playSound(String name, float volume, float pitch) {
    }


    /**
     * Gets distance to entity.
     *
     * @param entityIn the entity in
     * @return the distance to entity
     */
    @Shadow
    public abstract float getDistanceToEntity(Entity entityIn);

    /**
     * Is in water boolean.
     *
     * @return the boolean
     */
    @Shadow
    public abstract boolean isInWater();

    @Shadow
    protected abstract String getSwimSound();

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
            if (!Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x")) {
                callbackInfoReturnable.setReturnValue(hitBoxes.getSizeValue().get());
            } else {
                callbackInfoReturnable.setReturnValue(0.1F + hitBoxes.getSizeValue().get());
            }
        } else if (!Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x")) {
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

    /**
     * @author As_pw
     * @reason Patch
     */
    @Overwrite
    public void moveEntity(double x, double y, double z) {
        MoveEvent moveEvent = new MoveEvent(x, y, z);
        Client.eventManager.callEvent(moveEvent);

        if (moveEvent.isCancelled())
            return;

        x = moveEvent.getX();
        y = moveEvent.getY();
        z = moveEvent.getZ();

        if (this.noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
            this.posY = this.getEntityBoundingBox().minY;
            this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
        } else {
            this.worldObj.theProfiler.startSection("move");
            double d0 = this.posX;
            double d1 = this.posY;
            double d2 = this.posZ;

            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double d3 = x;
            double d4 = y;
            double d5 = z;
            boolean flag = this.onGround && this.isSneaking();

            if (flag || moveEvent.isSafeWalk()) {
                double d6;

                for (d6 = 0.05D; x != 0.0D && this.worldObj.getCollidingBoundingBoxes((Entity) (Object) this, this.getEntityBoundingBox().offset(x, -1.0D, 0.0D)).isEmpty(); d3 = x) {
                    if (x < d6 && x >= -d6) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= d6;
                    } else {
                        x += d6;
                    }
                }

                for (; z != 0.0D && this.worldObj.getCollidingBoundingBoxes((Entity) (Object) this, this.getEntityBoundingBox().offset(0.0D, -1.0D, z)).isEmpty(); d5 = z) {
                    if (z < d6 && z >= -d6) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= d6;
                    } else {
                        z += d6;
                    }
                }

                for (; x != 0.0D && z != 0.0D && this.worldObj.getCollidingBoundingBoxes((Entity) (Object) this, this.getEntityBoundingBox().offset(x, -1.0D, z)).isEmpty(); d5 = z) {
                    if (x < d6 && x >= -d6) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= d6;
                    } else {
                        x += d6;
                    }

                    d3 = x;

                    if (z < d6 && z >= -d6) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= d6;
                    } else {
                        z += d6;
                    }
                }
            }

            List<AxisAlignedBB> list1 = this.worldObj.getCollidingBoundingBoxes((Entity) (Object) this, this.getEntityBoundingBox().addCoord(x, y, z));
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();

            for (AxisAlignedBB axisalignedbb1 : list1) {
                y = axisalignedbb1.calculateYOffset(this.getEntityBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            boolean flag1 = this.onGround || d4 != y && d4 < 0.0D;

            for (AxisAlignedBB axisalignedbb2 : list1) {
                x = axisalignedbb2.calculateXOffset(this.getEntityBoundingBox(), x);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));

            for (AxisAlignedBB axisalignedbb13 : list1) {
                z = axisalignedbb13.calculateZOffset(this.getEntityBoundingBox(), z);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));

            if (this.stepHeight > 0.0F && flag1 && (d3 != x || d5 != z)) {
                StepEvent stepEvent = new StepEvent(this.stepHeight);
                Client.eventManager.callEvent(stepEvent);
                double d11 = x;
                double d7 = y;
                double d8 = z;
                AxisAlignedBB axisalignedbb3 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                y = stepEvent.getStepHeight();
                List<AxisAlignedBB> list = this.worldObj.getCollidingBoundingBoxes((Entity) (Object) this, this.getEntityBoundingBox().addCoord(d3, y, d5));
                AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
                AxisAlignedBB axisalignedbb5 = axisalignedbb4.addCoord(d3, 0.0D, d5);
                double d9 = y;

                for (AxisAlignedBB axisalignedbb6 : list) {
                    d9 = axisalignedbb6.calculateYOffset(axisalignedbb5, d9);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, d9, 0.0D);
                double d15 = d3;

                for (AxisAlignedBB axisalignedbb7 : list) {
                    d15 = axisalignedbb7.calculateXOffset(axisalignedbb4, d15);
                }

                axisalignedbb4 = axisalignedbb4.offset(d15, 0.0D, 0.0D);
                double d16 = d5;

                for (AxisAlignedBB axisalignedbb8 : list) {
                    d16 = axisalignedbb8.calculateZOffset(axisalignedbb4, d16);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d16);
                AxisAlignedBB axisalignedbb14 = this.getEntityBoundingBox();
                double d17 = y;

                for (AxisAlignedBB axisalignedbb9 : list) {
                    d17 = axisalignedbb9.calculateYOffset(axisalignedbb14, d17);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, d17, 0.0D);
                double d18 = d3;

                for (AxisAlignedBB axisalignedbb10 : list) {
                    d18 = axisalignedbb10.calculateXOffset(axisalignedbb14, d18);
                }

                axisalignedbb14 = axisalignedbb14.offset(d18, 0.0D, 0.0D);
                double d19 = d5;

                for (AxisAlignedBB axisalignedbb11 : list) {
                    d19 = axisalignedbb11.calculateZOffset(axisalignedbb14, d19);
                }

                axisalignedbb14 = axisalignedbb14.offset(0.0D, 0.0D, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d10 = d18 * d18 + d19 * d19;

                if (d20 > d10) {
                    x = d15;
                    z = d16;
                    y = -d9;
                    this.setEntityBoundingBox(axisalignedbb4);
                } else {
                    x = d18;
                    z = d19;
                    y = -d17;
                    this.setEntityBoundingBox(axisalignedbb14);
                }

                for (AxisAlignedBB axisalignedbb12 : list) {
                    y = axisalignedbb12.calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

                if (d11 * d11 + d8 * d8 >= x * x + z * z) {
                    x = d11;
                    y = d7;
                    z = d8;
                    this.setEntityBoundingBox(axisalignedbb3);
                } else {
                    Client.eventManager.callEvent(new StepConfirmEvent());
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
            this.posY = this.getEntityBoundingBox().minY;
            this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
            this.isCollidedHorizontally = d3 != x || d5 != z;
            this.isCollidedVertically = d4 != y;
            this.onGround = this.isCollidedVertically && d4 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            int k = MathHelper.floor_double(this.posZ);
            BlockPos blockpos = new BlockPos(i, j, k);
            Block block1 = this.worldObj.getBlockState(blockpos).getBlock();

            if (block1.getMaterial() == Material.air) {
                Block block = this.worldObj.getBlockState(blockpos.down()).getBlock();

                if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate) {
                    block1 = block;
                    blockpos = blockpos.down();
                }
            }

            this.updateFallState(y, this.onGround, block1, blockpos);

            if (d3 != x) {
                this.motionX = 0.0D;
            }

            if (d5 != z) {
                this.motionZ = 0.0D;
            }

            if (d4 != y) {
                block1.onLanded(this.worldObj, (Entity) (Object) this);
            }

            if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
                double d12 = this.posX - d0;
                double d13 = this.posY - d1;
                double d14 = this.posZ - d2;

                if (block1 != Blocks.ladder) {
                    d13 = 0.0D;
                }

                if (block1 != null && this.onGround) {
                    block1.onEntityCollidedWithBlock(this.worldObj, blockpos, (Entity) (Object) this);
                }

                this.distanceWalkedModified = (float) ((double) this.distanceWalkedModified + (double) MathHelper.sqrt_double(d12 * d12 + d14 * d14) * 0.6D);
                this.distanceWalkedOnStepModified = (float) ((double) this.distanceWalkedOnStepModified + (double) MathHelper.sqrt_double(d12 * d12 + d13 * d13 + d14 * d14) * 0.6D);

                if (this.distanceWalkedOnStepModified > (float) getNextStepDistance() && block1.getMaterial() != Material.air) {
                    setNextStepDistance((int) this.distanceWalkedOnStepModified + 1);

                    if (this.isInWater()) {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F) {
                            f = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.playStepSound(blockpos, block1);
                }
            }

            try {
                this.doBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            boolean flag2 = this.isWet();

            if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001D, 0.001D, 0.001D))) {
                this.dealFireDamage(1);

                if (!flag2) {
                    setFire(getFire() + 1);

                    if (getFire() == 0) {
                        this.setFire(8);
                    }
                }
            } else if (getFire() <= 0) {
                setFire(-this.fireResistance);
            }

            if (flag2 && getFire() > 0) {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                setFire(-this.fireResistance);
            }

            this.worldObj.theProfiler.endSection();
        }
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