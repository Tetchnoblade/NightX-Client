package net.aspw.client.features.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.aspw.client.event.*;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class McUpdatesHandler extends MinecraftInstance implements Listenable {

    public static boolean isSwimmingOrCrawling = false;
    public static boolean doingEyeRot = false;
    public static float eyeHeight;
    public static float lastEyeHeight;

    private static boolean underWater() {
        final World world = mc.thePlayer.getEntityWorld();
        double eyeBlock = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - 0.25;
        BlockPos blockPos = new BlockPos(mc.thePlayer.posX, eyeBlock, mc.thePlayer.posZ);

        return world.getBlockState(blockPos).getBlock().getMaterial() == Material.water && !(mc.thePlayer.ridingEntity instanceof EntityBoat);
    }

    private static boolean isSwimming() {
        return !mc.thePlayer.noClip && mc.thePlayer.isInWater() && mc.thePlayer.isSprinting();
    }

    public static boolean shouldAnimation() {
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox();
        AxisAlignedBB crawl = new AxisAlignedBB(box.minX, box.minY + 0.9, box.minZ, box.minX + 0.6, box.minY + 1.5, box.minZ + 0.6);

        return isSwimmingOrCrawling && mc.thePlayer.isSprinting() && mc.thePlayer.isInWater() || isSwimmingOrCrawling && !mc.theWorld.getCollisionBoxes(crawl).isEmpty();
    }

    private static void resetAll() {
        if (isSwimmingOrCrawling) isSwimmingOrCrawling = false;
        if (doingEyeRot) doingEyeRot = false;
    }

    @EventTarget
    public void onPushOut(PushOutEvent event) {
        if (ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_14) && !mc.isIntegratedServerRunning() && (shouldAnimation() || mc.thePlayer.isSneaking()))
            event.cancelEvent();
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        resetAll();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_14) && !mc.isIntegratedServerRunning()) {
            float EYE_START_HEIGHT = 1.62f;
            float EYE_END_HEIGHT;

            lastEyeHeight = eyeHeight;

            EYE_END_HEIGHT = 0.45f;

            if (shouldAnimation()) {
                float delta = EYE_END_HEIGHT - eyeHeight;
                delta *= 0.85F;
                eyeHeight = EYE_END_HEIGHT - delta;
                doingEyeRot = true;
            } else if (eyeHeight < EYE_START_HEIGHT) {
                float delta = EYE_START_HEIGHT - eyeHeight;
                delta *= 0.85F;
                eyeHeight = EYE_START_HEIGHT - delta;
            }

            if (eyeHeight > EYE_START_HEIGHT - 0.01f && doingEyeRot)
                doingEyeRot = false;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (ProtocolBase.getManager().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_14) && !mc.isIntegratedServerRunning()) {
            if (isSwimming()) {
                if (mc.thePlayer.motionX < -0.4D) {
                    mc.thePlayer.motionX = -0.39F;
                }
                if (mc.thePlayer.motionX > 0.4D) {
                    mc.thePlayer.motionX = 0.39F;
                }
                if (mc.thePlayer.motionY < -0.4D) {
                    mc.thePlayer.motionY = -0.39F;
                }
                if (mc.thePlayer.motionY > 0.4D) {
                    mc.thePlayer.motionY = 0.39F;
                }
                if (mc.thePlayer.motionZ < -0.4D) {
                    mc.thePlayer.motionZ = -0.39F;
                }
                if (mc.thePlayer.motionZ > 0.4D) {
                    mc.thePlayer.motionZ = 0.39F;
                }

                double d3 = mc.thePlayer.getLookVec().yCoord;
                double d4 = 0.025D;

                if (d3 <= 0.0D || mc.thePlayer.worldObj.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0D - 0.64D, mc.thePlayer.posZ)).getBlock().getMaterial() == Material.water) {
                    mc.thePlayer.motionY += (d3 - mc.thePlayer.motionY) * d4;
                }

                mc.thePlayer.motionY += 0.018d;

                if (shouldAnimation()) {
                    mc.thePlayer.motionX *= 1.11F;
                    mc.thePlayer.motionZ *= 1.11F;
                }
            }

            double d0 = mc.thePlayer.width / 2.0;
            AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox();
            AxisAlignedBB setThrough = new AxisAlignedBB(mc.thePlayer.posX - d0, box.minY, mc.thePlayer.posZ - d0, mc.thePlayer.posX + d0, box.minY + mc.thePlayer.height, mc.thePlayer.posZ + d0);
            AxisAlignedBB sneak = new AxisAlignedBB(box.minX, box.minY + 0.9, box.minZ, box.minX + 0.6, box.minY + 1.8, box.minZ + 0.6);
            AxisAlignedBB crawl = new AxisAlignedBB(box.minX, box.minY + 0.9, box.minZ, box.minX + 0.6, box.minY + 1.5, box.minZ + 0.6);

            float newHeight;
            float newWidth;

            if (isSwimmingOrCrawling && underWater() && mc.thePlayer.rotationPitch >= 0.0) {
                newHeight = 0.6f;
                newWidth = 0.6f;
                isSwimmingOrCrawling = true;
                mc.thePlayer.setEntityBoundingBox(setThrough);
            } else if (isSwimming() && underWater() || !mc.theWorld.getCollisionBoxes(crawl).isEmpty()) {
                newHeight = 0.6f;
                newWidth = 0.6f;
                isSwimmingOrCrawling = true;
                mc.thePlayer.setEntityBoundingBox(setThrough);
            } else if (mc.thePlayer.isSneaking() && !underWater()) {
                newHeight = 1.5f;  // TODO: fix v1.8.x ~ v1.13.2 protocol sneaks (1.8 height everytime)
                newWidth = 0.6f;
                mc.thePlayer.setEntityBoundingBox(setThrough);
            } else {
                if (isSwimmingOrCrawling)
                    isSwimmingOrCrawling = false;
                newHeight = 1.8f;
                newWidth = 0.6f;
                mc.thePlayer.setEntityBoundingBox(setThrough);
            }

            if (mc.thePlayer.onGround && !mc.thePlayer.isSneaking() && !underWater() && (mc.thePlayer.height == 1.5f || mc.thePlayer.height == 0.6F) && !mc.theWorld.getCollisionBoxes(sneak).isEmpty()) {
                mc.gameSettings.keyBindSneak.pressed = true;
            } else if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && mc.theWorld.getCollisionBoxes(sneak).isEmpty()) {
                mc.gameSettings.keyBindSneak.pressed = false;
            }

            try {
                mc.thePlayer.height = newHeight;
                mc.thePlayer.width = newWidth;
            } catch (IllegalArgumentException ignored) {
            }
        } else resetAll();
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}