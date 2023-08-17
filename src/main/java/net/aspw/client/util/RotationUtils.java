package net.aspw.client.util;

import net.aspw.client.Client;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Listenable;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.event.TickEvent;
import net.aspw.client.features.module.impl.combat.FastBow;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static java.lang.Math.PI;

/**
 * The type Rotation utils.
 */
public final class RotationUtils extends MinecraftInstance implements Listenable {

    private static final Random random = new Random();
    /**
     * The constant targetRotation.
     */
    public static Rotation targetRotation;
    /**
     * The constant serverRotation.
     */
    public static Rotation serverRotation = new Rotation(90F, 90F);
    /**
     * The constant keepCurrentRotation.
     */
    public static boolean keepCurrentRotation = false;
    private static int keepLength;
    private static double x = random.nextDouble();
    private static double y = random.nextDouble();
    private static double z = random.nextDouble();

    /**
     * Other rotation rotation.
     *
     * @param bb           the bb
     * @param vec          the vec
     * @param predict      the predict
     * @param throughWalls the through walls
     * @param distance     the distance
     * @return the rotation
     */
    public static Rotation OtherRotation(final AxisAlignedBB bb, final Vec3 vec, final boolean predict, final boolean throughWalls, final float distance) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
                mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);
        VecRotation vecRotation = null;
        for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);
                    final double vecDist = eyes.distanceTo(vec3);

                    if (vecDist > distance)
                        continue;

                    if (throughWalls || isVisible(vec3)) {
                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null)
                            vecRotation = currentVec;
                    }
                }
            }
        }

        if (predict) eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new Rotation(MathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), MathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));
    }

    /**
     * Face block vec rotation.
     *
     * @param blockPos the block pos
     * @return the vec rotation
     */
    public static VecRotation faceBlock(final BlockPos blockPos) {
        if (blockPos == null)
            return null;

        VecRotation vecRotation = null;

        for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
            for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                    final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                    final Vec3 posVec = new Vec3(blockPos).addVector(xSearch, ySearch, zSearch);
                    final double dist = eyesPos.distanceTo(posVec);

                    final double diffX = posVec.xCoord - eyesPos.xCoord;
                    final double diffY = posVec.yCoord - eyesPos.yCoord;
                    final double diffZ = posVec.zCoord - eyesPos.zCoord;

                    final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                    final Rotation rotation = new Rotation(
                            MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                            MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                    );

                    final Vec3 rotationVector = getVectorForRotation(rotation);
                    final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * dist, rotationVector.yCoord * dist,
                            rotationVector.zCoord * dist);
                    final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false,
                            false, true);

                    if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        final VecRotation currentVec = new VecRotation(posVec, rotation);

                        if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }

    /**
     * Face bow.
     *
     * @param target      the target
     * @param silent      the silent
     * @param predict     the predict
     * @param predictSize the predict size
     */
    public static void faceBow(final Entity target, final boolean silent, final boolean predict, final float predictSize) {
        final EntityPlayerSP player = mc.thePlayer;

        final double posX = target.posX + (predict ? (target.posX - target.prevPosX) * predictSize : 0) - (player.posX + (predict ? (player.posX - player.prevPosX) : 0));
        final double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * predictSize : 0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? (player.posY - player.prevPosY) : 0)) - player.getEyeHeight();
        final double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * predictSize : 0) - (player.posZ + (predict ? (player.posZ - player.prevPosZ) : 0));
        final double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

        float velocity = Client.moduleManager.getModule(FastBow.class).getState() ? 1F : player.getItemInUseDuration() / 20F;
        velocity = (velocity * velocity + velocity * 2) / 3;

        if (velocity > 1) velocity = 1;

        final Rotation rotation = new Rotation(
                (float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90,
                (float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - 0.006F * (0.006F * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006F * posSqrt)))
        );

        if (silent)
            setTargetRotation(rotation);
        else
            limitAngleChange(new Rotation(player.rotationYaw, player.rotationPitch), rotation, 10 +
                    new Random().nextInt(6)).toPlayer(mc.thePlayer);
    }

    /**
     * To rotation rotation.
     *
     * @param vec     the vec
     * @param predict the predict
     * @return the rotation
     */
    public static Rotation toRotation(final Vec3 vec, final boolean predict) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
                mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        if (predict) eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new Rotation(MathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), MathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));
    }

    /**
     * Gets center.
     *
     * @param bb the bb
     * @return the center
     */
    public static Vec3 getCenter(final AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    /**
     * Search center vec rotation.
     *
     * @param bb           the bb
     * @param outborder    the outborder
     * @param random       the random
     * @param predict      the predict
     * @param throughWalls the through walls
     * @param distance     the distance
     * @return the vec rotation
     */
    public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random,
                                           final boolean predict, final boolean throughWalls, final float distance) {
        return searchCenter(bb, outborder, random, predict, throughWalls, distance, 0F, false);
    }

    /**
     * Round rotation float.
     *
     * @param yaw      the yaw
     * @param strength the strength
     * @return the float
     */
    public static float roundRotation(float yaw, int strength) {
        return Math.round(yaw / strength) * strength;
    }

    /**
     * Search center vec rotation.
     *
     * @param bb             the bb
     * @param outborder      the outborder
     * @param random         the random
     * @param predict        the predict
     * @param throughWalls   the through walls
     * @param distance       the distance
     * @param randomMultiply the random multiply
     * @param newRandom      the new random
     * @return the vec rotation
     */
    public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean outborder, final boolean random,
                                           final boolean predict, final boolean throughWalls, final float distance, final float randomMultiply, final boolean newRandom) {
        if (outborder) {
            final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
            return new VecRotation(vec3, toRotation(vec3, predict));
        }

        final Vec3 randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * randomMultiply * (newRandom ? Math.random() : 1), bb.minY + (bb.maxY - bb.minY) * y * randomMultiply * (newRandom ? Math.random() : 1), bb.minZ + (bb.maxZ - bb.minZ) * z * randomMultiply * (newRandom ? Math.random() : 1));
        final Rotation randomRotation = toRotation(randomVec, predict);

        final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);

        VecRotation vecRotation = null;

        for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);
                    final double vecDist = eyes.distanceTo(vec3);

                    if (vecDist > distance)
                        continue;

                    if (throughWalls || isVisible(vec3)) {
                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }

    /**
     * Gets rotation difference.
     *
     * @param entity the entity
     * @return the rotation difference
     */
    public static double getRotationDifference(final Entity entity) {
        final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

        return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
    }


    /**
     * Gets rotation back difference.
     *
     * @param entity the entity
     * @return the rotation back difference
     */
    public static double getRotationBackDifference(final Entity entity) {
        final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

        return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw - 180, mc.thePlayer.rotationPitch));
    }

    /**
     * Gets rotation difference.
     *
     * @param rotation the rotation
     * @return the rotation difference
     */
    public static double getRotationDifference(final Rotation rotation) {
        return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
    }

    /**
     * Gets rotation difference.
     *
     * @param a the a
     * @param b the b
     * @return the rotation difference
     */
    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    /**
     * Limit angle change rotation.
     *
     * @param currentRotation the current rotation
     * @param targetRotation  the target rotation
     * @param turnSpeed       the turn speed
     * @return the rotation
     */
    @NotNull
    public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
        final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
        final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

        return new Rotation(
                currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
                currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
                ));
    }

    public static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    /**
     * Gets vector for rotation.
     *
     * @param rotation the rotation
     * @return the vector for rotation
     */
    public static Vec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    /**
     * Is faced boolean.
     *
     * @param targetEntity       the target entity
     * @param blockReachDistance the block reach distance
     * @return the boolean
     */
    public static boolean isFaced(final Entity targetEntity, double blockReachDistance) {
        return RaycastUtils.raycastEntity(blockReachDistance, entity -> entity == targetEntity) != null;
    }

    /**
     * Is visible boolean.
     *
     * @param vec3 the vec 3
     * @return the boolean
     */
    public static boolean isVisible(final Vec3 vec3) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        return mc.theWorld.rayTraceBlocks(eyesPos, vec3) == null;
    }

    /**
     * Sets target rotation.
     *
     * @param rotation the rotation
     */
    public static void setTargetRotation(final Rotation rotation) {
        setTargetRotation(rotation, 0);
    }

    /**
     * Sets target rotation.
     *
     * @param rotation   the rotation
     * @param keepLength the keep length
     */
    public static void setTargetRotation(final Rotation rotation, final int keepLength) {
        if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
                || rotation.getPitch() > 90 || rotation.getPitch() < -90)
            return;

        rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
        targetRotation = rotation;
        RotationUtils.keepLength = keepLength;
    }

    /**
     * Reset.
     */
    public static void reset() {
        keepLength = 0;
        targetRotation = null;
    }

    /**
     * Gets rotations entity.
     *
     * @param entity the entity
     * @return the rotations entity
     */
    public static Rotation getRotationsEntity(EntityLivingBase entity) {
        return RotationUtils.getRotations(entity.posX, entity.posY + entity.getEyeHeight() - 0.4, entity.posZ);
    }

    /**
     * Gets rotations.
     *
     * @param posX the pos x
     * @param posY the pos y
     * @param posZ the pos z
     * @return the rotations
     */
    public static Rotation getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = RotationUtils.mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / 3.141592653589793));
        return new Rotation(yaw, pitch);
    }

    public static Rotation calculate(final Vec3 from, final Vec3 to) {
        final Vec3 diff = to.subtract(from);
        final double distance = Math.hypot(diff.xCoord, diff.zCoord);
        final float yaw = (float) (MathHelper.atan2(diff.zCoord, diff.xCoord) * (180F / PI)) - 90.0F;
        final float pitch = (float) (-(MathHelper.atan2(diff.yCoord, distance) * (180F / PI)));
        return new Rotation(yaw, pitch);
    }

    public static Rotation calculate(final Vec3 to) {
        return calculate(mc.thePlayer.getPositionVector().add(new Vec3(0, mc.thePlayer.getEyeHeight(), 0)), new Vec3(to.xCoord, to.yCoord, to.zCoord));
    }

    /**
     * Gets rotations.
     *
     * @param ent the ent
     * @return the rotations
     */
    public static Rotation getRotations(Entity ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + (double) (ent.getEyeHeight() / 2.0f);
        return RotationUtils.getRotationFromPosition(x, z, y);
    }

    /**
     * Get rotations 1 float [ ].
     *
     * @param posX the pos x
     * @param posY the pos y
     * @param posZ the pos z
     * @return the float [ ]
     */
    public static float[] getRotations1(double posX, double posY, double posZ) {
        EntityPlayerSP player = RotationUtils.mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
        return new float[]{yaw, pitch};
    }

    /**
     * Gets rotation from position.
     *
     * @param x the x
     * @param z the z
     * @param y the y
     * @return the rotation from position
     */
    public static Rotation getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - mc.thePlayer.posX;
        double zDiff = z - mc.thePlayer.posZ;
        double yDiff = y - mc.thePlayer.posY - 1.2;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
        return new Rotation(yaw, pitch);
    }

    /**
     * On tick.
     *
     * @param event the event
     */
    @EventTarget
    public void onTick(final TickEvent event) {
        if (targetRotation != null) {
            keepLength--;

            if (keepLength <= 0)
                reset();
        }

        if (random.nextGaussian() > 0.8D) x = Math.random();
        if (random.nextGaussian() > 0.8D) y = Math.random();
        if (random.nextGaussian() > 0.8D) z = Math.random();
    }

    /**
     * On packet.
     *
     * @param event the event
     */
    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;

            if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
                packetPlayer.yaw = targetRotation.getYaw();
                packetPlayer.pitch = targetRotation.getPitch();
                packetPlayer.rotating = true;
            }

            if (packetPlayer.rotating) serverRotation = new Rotation(packetPlayer.yaw, packetPlayer.pitch);
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}