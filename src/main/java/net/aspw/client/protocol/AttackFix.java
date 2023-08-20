package net.aspw.client.protocol;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AttackFix {
    private final static Minecraft mc = Minecraft.getMinecraft();

    public static void sendFixedAttack(EntityPlayer entityIn, Entity target) {
        if (Protocol.versionSlider.getSliderVersion().getName().equals("1.8.x")) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(entityIn, target);
        } else {
            mc.playerController.attackEntity(entityIn, target);
            mc.thePlayer.swingItem();
        }
    }
}