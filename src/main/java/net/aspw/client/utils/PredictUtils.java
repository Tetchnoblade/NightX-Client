package net.aspw.client.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.Vec3;
import scala.collection.mutable.MutableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
}