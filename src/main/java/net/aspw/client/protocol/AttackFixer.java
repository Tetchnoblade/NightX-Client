package net.aspw.client.protocol;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class AttackFixer {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendConditionalSwing(final MovingObjectPosition mop) {
        if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            mc.thePlayer.swingItem();
        }
    }

    public static void sendFixedAttack(final EntityPlayer entityIn, final Entity target) {
        if (!Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x")) {
            send1_9Attack(entityIn, target);
        } else {
            send1_8Attack(entityIn, target);
        }
    }

    private static void send1_8Attack(EntityPlayer entityIn, Entity target) {
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(entityIn, target);
    }

    private static void send1_9Attack(EntityPlayer entityIn, Entity target) {
        mc.playerController.attackEntity(entityIn, target);
        mc.thePlayer.swingItem();
    }
}