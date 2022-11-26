
package net.aspw.nightx.features.module.modules.combat;

import net.aspw.nightx.event.EventState;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "TPHit", spacedName = "TP Hit", category = ModuleCategory.COMBAT)
public class TPHit extends Module {
    private EntityLivingBase targetEntity;
    private boolean shouldHit;

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.getEventState() != EventState.PRE)
            return;

        final Entity facedEntity = RaycastUtils.raycastEntity(100D, raycastedEntity -> raycastedEntity instanceof EntityLivingBase);

        EntityPlayerSP thePlayer = mc.thePlayer;

        if (thePlayer == null)
            return;

        if (mc.gameSettings.keyBindAttack.isKeyDown() && EntityUtils.isSelected(facedEntity, true)) {
            if (facedEntity.getDistanceSqToEntity(mc.thePlayer) >= 1D) targetEntity = (EntityLivingBase) facedEntity;
        }

        if (targetEntity != null) {
            if (!shouldHit) {
                shouldHit = true;
                return;
            }

                final Vec3 rotationVector = RotationUtils.getVectorForRotation(new Rotation(mc.thePlayer.rotationYaw, 0F));
                final double x = mc.thePlayer.posX + rotationVector.xCoord * (mc.thePlayer.getDistanceToEntity(targetEntity) - 1.0F);
                final double z = mc.thePlayer.posZ + rotationVector.zCoord * (mc.thePlayer.getDistanceToEntity(targetEntity) - 1.0F);
                final double y = targetEntity.getPosition().getY() + 0.25D;

                PathUtils.findPath(x, y + 1.0D, z, 4D).forEach(pos -> mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY(), pos.getZ(), false)));

                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
                shouldHit = false;
                targetEntity = null;
        } else
            shouldHit = false;
    }
}
