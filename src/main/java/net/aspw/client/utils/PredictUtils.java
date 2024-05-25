package net.aspw.client.utils;

import net.aspw.client.utils.MinecraftInstance;
import net.aspw.client.utils.block.BlockUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PredictUtils extends MinecraftInstance {

    public static boolean predicting = false;

    public static LinkedList<Vec3> predict(int tick) {
        predicting = true;
        LinkedList<Vec3> positions = new LinkedList<>();
        EntityPlayerSP sp = new EntityPlayerSP(
                mc,
                mc.theWorld,
                mc.getNetHandler(),
                new StatFileWriter()
        );
        sp.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        sp.onGround = mc.thePlayer.onGround;
        sp.setSprinting(mc.thePlayer.isSprinting());
        sp.setSneaking(mc.thePlayer.isSneaking());
        sp.motionX = mc.thePlayer.motionX;
        sp.motionY = mc.thePlayer.motionY;
        sp.motionZ = mc.thePlayer.motionZ;
        sp.movementInput = new MovementInputFromOptions(mc.gameSettings);
        for (int i = 0; i < tick; i++) {
            sp.movementInput.moveStrafe = mc.thePlayer.movementInput.moveStrafe;
            sp.movementInput.moveForward = mc.thePlayer.movementInput.moveForward;
            sp.movementInput.jump = mc.thePlayer.movementInput.jump;
            sp.movementInput.sneak = mc.thePlayer.movementInput.sneak;
            sp.moveForward = mc.thePlayer.moveForward;
            sp.moveStrafing = mc.thePlayer.moveStrafing;
            sp.setJumping(mc.thePlayer.movementInput.jump);
            sp.onUpdate();
            positions.add(new Vec3(sp.posX, sp.posY, sp.posZ));
        }
        predicting = false;
        return positions;
    }

    public static boolean checkVoid(int tick) {
        predicting = true;
        EntityPlayerSP sp = new EntityPlayerSP(
                mc,
                mc.theWorld,
                mc.getNetHandler(),
                new StatFileWriter()
        );
        sp.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        sp.onGround = false;
        sp.setSneaking(mc.thePlayer.isSneaking());
        sp.motionX = mc.thePlayer.motionX;
        sp.motionY = mc.thePlayer.motionY;
        sp.motionZ = mc.thePlayer.motionZ;
        sp.movementInput = new MovementInputFromOptions(mc.gameSettings);
        for (int i = 0; i < tick; i++) {
            sp.movementInput.moveStrafe = mc.thePlayer.movementInput.moveStrafe;
            sp.movementInput.moveForward = mc.thePlayer.movementInput.moveForward;
            sp.movementInput.sneak = mc.thePlayer.movementInput.sneak;
            sp.moveForward = mc.thePlayer.moveForward;
            sp.moveStrafing = mc.thePlayer.moveStrafing;
            sp.setJumping(mc.thePlayer.movementInput.jump);
            sp.onUpdate();
            boolean isNotSafe = detectVoid(sp);
            if (isNotSafe) {
                predicting = false;
                return true;
            }
        }
        predicting = false;
        return false;
    }

    private static boolean detectVoid(EntityPlayerSP sp) {
        boolean doing = true;
        for (double yOffset = -1; yOffset >= -100; yOffset--) {
            BlockPos blockPos = new BlockPos(sp.posX, sp.posY + yOffset, sp.posZ);
            if (!(BlockUtils.getBlock(blockPos) instanceof BlockAir)) {
                doing = false;
                break;
            }
        }
        return doing && sp.fallDistance != 0 && mc.thePlayer != null && mc.theWorld != null && !mc.thePlayer.isSneaking() && !mc.thePlayer.capabilities.isFlying;
    }
}