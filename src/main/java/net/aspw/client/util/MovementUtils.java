package net.aspw.client.util;

import net.aspw.client.event.MoveEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

/**
 * The type Movement utils.
 */
public final class MovementUtils extends MinecraftInstance {

    private static double lastX = -999999.0;
    private static double lastZ = -999999.0;

    /**
     * Gets speed.
     *
     * @return the speed
     */
    public static float getSpeed() {
        return (float) getSpeed(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    /**
     * Gets speed.
     *
     * @param motionX the motion x
     * @param motionZ the motion z
     * @return the speed
     */
    public static double getSpeed(double motionX, double motionZ) {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }

    /**
     * Is on ice boolean.
     *
     * @return the boolean
     */
    public static boolean isOnIce() {
        final EntityPlayerSP player = mc.thePlayer;
        final Block blockUnder = mc.theWorld.getBlockState(new BlockPos(player.posX, player.posY - 1.0, player.posZ)).getBlock();
        return blockUnder instanceof BlockIce || blockUnder instanceof BlockPackedIce;
    }

    /**
     * Is block under boolean.
     *
     * @return the boolean
     */
    public static boolean isBlockUnder() {
        if (mc.thePlayer == null) return false;

        if (mc.thePlayer.posY < 0.0) {
            return false;
        }
        for (int off = 0; off < (int) mc.thePlayer.posY + 2; off += 2) {
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, -off, 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // xD
    public static boolean isRidingBlock() {
        return !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.8, mc.thePlayer.posZ)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 0.5, mc.thePlayer.posY - 1.8, mc.thePlayer.posZ + 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - 0.5, mc.thePlayer.posY - 1.8, mc.thePlayer.posZ + 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 0.5, mc.thePlayer.posY - 1.8, mc.thePlayer.posZ - 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - 0.5, mc.thePlayer.posY - 1.8, mc.thePlayer.posZ - 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 0.5, mc.thePlayer.posY - 1, mc.thePlayer.posZ + 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - 0.5, mc.thePlayer.posY - 1, mc.thePlayer.posZ + 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 0.5, mc.thePlayer.posY - 1, mc.thePlayer.posZ - 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - 0.5, mc.thePlayer.posY - 1, mc.thePlayer.posZ - 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 0.5, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ + 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - 0.5, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ + 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + 0.5, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ - 0.5)).getBlock() instanceof BlockAir) || !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX - 0.5, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ - 0.5)).getBlock() instanceof BlockAir);
    }

    /**
     * Accelerate.
     *
     * @param speed the speed
     */
    public static void accelerate(final float speed) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX += -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ += Math.cos(yaw) * speed;
    }

    /**
     * Strafe.
     */
    public static void strafe() {
        strafe(getSpeed());
    }

    /**
     * Is moving boolean.
     *
     * @return the boolean
     */
    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    /**
     * Has motion boolean.
     *
     * @return the boolean
     */
    public static boolean hasMotion() {
        return mc.thePlayer.motionX != 0D && mc.thePlayer.motionZ != 0D && mc.thePlayer.motionY != 0D;
    }

    /**
     * Strafe.
     *
     * @param speed the speed
     */
    public static void strafe(final float speed) {
        if (!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    /**
     * Forward.
     *
     * @param length the length
     */
    public static void forward(final double length) {
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }

    /**
     * Gets direction.
     *
     * @return the direction
     */
    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Gets raw direction.
     *
     * @return the raw direction
     */
    public static float getRawDirection() {
        return getRawDirectionRotation(mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing, mc.thePlayer.moveForward);
    }

    /**
     * Get xz dist double [ ].
     *
     * @param speed the speed
     * @param cYaw  the c yaw
     * @return the double [ ]
     */
    public static double[] getXZDist(float speed, float cYaw) {
        double[] arr = new double[2];
        final double yaw = getDirectionRotation(cYaw, mc.thePlayer.moveStrafing, mc.thePlayer.moveForward);
        arr[0] = -Math.sin(yaw) * speed;
        arr[1] = Math.cos(yaw) * speed;
        return arr;
    }

    /**
     * Gets prediction yaw.
     *
     * @param x the x
     * @param z the z
     * @return the prediction yaw
     */
    public static float getPredictionYaw(double x, double z) {
        if (mc.thePlayer == null) {
            lastX = -999999.0;
            lastZ = -999999.0;
            return 0F;
        }

        if (lastX == -999999.0)
            lastX = mc.thePlayer.prevPosX;

        if (lastZ == -999999.0)
            lastZ = mc.thePlayer.prevPosZ;

        float returnValue = (float) (Math.atan2(z - lastZ, x - lastX) * 180F / Math.PI);

        lastX = x;
        lastZ = z;

        return returnValue;
    }

    /**
     * Gets direction rotation.
     *
     * @param yaw      the yaw
     * @param pStrafe  the p strafe
     * @param pForward the p forward
     * @return the direction rotation
     */
    public static double getDirectionRotation(float yaw, float pStrafe, float pForward) {
        float rotationYaw = yaw;

        if (pForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (pForward < 0F)
            forward = -0.5F;
        else if (pForward > 0F)
            forward = 0.5F;

        if (pStrafe > 0F)
            rotationYaw -= 90F * forward;

        if (pStrafe < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Gets raw direction rotation.
     *
     * @param yaw      the yaw
     * @param pStrafe  the p strafe
     * @param pForward the p forward
     * @return the raw direction rotation
     */
    public static float getRawDirectionRotation(float yaw, float pStrafe, float pForward) {
        float rotationYaw = yaw;

        if (pForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if (pForward < 0F)
            forward = -0.5F;
        else if (pForward > 0F)
            forward = 0.5F;

        if (pStrafe > 0F)
            rotationYaw -= 90F * forward;

        if (pStrafe < 0F)
            rotationYaw += 90F * forward;

        return rotationYaw;
    }

    /**
     * Gets scaffold rotation.
     *
     * @param yaw    the yaw
     * @param strafe the strafe
     * @return the scaffold rotation
     */
    public static float getScaffoldRotation(float yaw, float strafe) {
        float rotationYaw = yaw;

        rotationYaw += 180F;

        float forward = -0.5F;

        if (strafe < 0F)
            rotationYaw -= 90F * forward;

        if (strafe > 0F)
            rotationYaw += 90F * forward;

        return rotationYaw;
    }

    /**
     * Gets speed effect.
     *
     * @return the speed effect
     */
    public static int getSpeedEffect() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }

    /**
     * Gets base move speed.
     *
     * @return the base move speed
     */
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D + 0.2D * (double) (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    /**
     * Gets base move speed.
     *
     * @param customSpeed the custom speed
     * @return the base move speed
     */
    public static double getBaseMoveSpeed(double customSpeed) {
        double baseSpeed = isOnIce() ? 0.258977700006 : customSpeed;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    /**
     * Gets jump boost modifier.
     *
     * @param baseJumpHeight the base jump height
     * @return the jump boost modifier
     */
    public static double getJumpBoostModifier(double baseJumpHeight) {
        return getJumpBoostModifier(baseJumpHeight, true);
    }

    /**
     * Gets jump boost modifier.
     *
     * @param baseJumpHeight the base jump height
     * @param potionJump     the potion jump
     * @return the jump boost modifier
     */
    public static double getJumpBoostModifier(double baseJumpHeight, boolean potionJump) {
        if (mc.thePlayer.isPotionActive(Potion.jump) && potionJump) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += ((float) (amplifier + 1) * 0.1f);
        }

        return baseJumpHeight;
    }

    /**
     * Sets motion.
     *
     * @param event        the event
     * @param speed        the speed
     * @param motion       the motion
     * @param smoothStrafe the smooth strafe
     */
    public static void setMotion(MoveEvent event, double speed, double motion, boolean smoothStrafe) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        double yaw = mc.thePlayer.rotationYaw;
        int direction = smoothStrafe ? 45 : 90;

        if ((forward == 0.0) && (strafe == 0.0)) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (forward > 0.0 ? -direction : direction);
                } else if (strafe < 0.0) {
                    yaw += (forward > 0.0 ? direction : -direction);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }

            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            event.setX((forward * speed * cos + strafe * speed * sin) * motion);
            event.setZ((forward * speed * sin - strafe * speed * cos) * motion);
        }
    }

    /**
     * Sets motion.
     *
     * @param speed        the speed
     * @param smoothStrafe the smooth strafe
     */
    public static void setMotion(double speed, boolean smoothStrafe) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        int direction = smoothStrafe ? 45 : 90;

        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -direction : direction);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? direction : -direction);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }

            mc.thePlayer.motionX = forward * speed * (-Math.sin(Math.toRadians(yaw))) + strafe * speed * Math.cos(Math.toRadians(yaw));
            mc.thePlayer.motionZ = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * (-Math.sin(Math.toRadians(yaw)));
        }
    }

    /**
     * Sets speed.
     *
     * @param moveEvent     the move event
     * @param moveSpeed     the move speed
     * @param pseudoYaw     the pseudo yaw
     * @param pseudoStrafe  the pseudo strafe
     * @param pseudoForward the pseudo forward
     */
    public static void setSpeed(final MoveEvent moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setZ(0);
            moveEvent.setX(0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            if (strafe > 0.0D) {
                strafe = 1.0D;
            } else if (strafe < 0.0D) {
                strafe = -1.0D;
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));

            moveEvent.setX((forward * moveSpeed * cos + strafe * moveSpeed * sin));
            moveEvent.setZ((forward * moveSpeed * sin - strafe * moveSpeed * cos));
        }
    }
}