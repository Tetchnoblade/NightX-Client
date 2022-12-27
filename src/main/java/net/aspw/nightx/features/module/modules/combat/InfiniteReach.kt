package net.aspw.nightx.features.module.modules.combat

import net.aspw.nightx.event.EventState
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.*
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.util.function.Consumer
import javax.vecmath.Vector3d

@ModuleInfo(name = "InfiniteReach", spacedName = "Infinite Reach", category = ModuleCategory.COMBAT)
class InfiniteReach : Module() {
    private var targetEntity: EntityLivingBase? = null
    private var shouldHit = false

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState !== EventState.PRE) return
        val facedEntity =
            RaycastUtils.raycastEntity(100.0) { raycastedEntity: Entity? -> raycastedEntity is EntityLivingBase }
        val thePlayer = mc.thePlayer ?: return
        if (mc.gameSettings.keyBindAttack.isKeyDown && EntityUtils.isSelected(facedEntity, true)) {
            if (facedEntity.getDistanceSqToEntity(mc.thePlayer) >= 1.0) targetEntity = facedEntity as EntityLivingBase
        }
        if (targetEntity != null) {
            if (!shouldHit) {
                shouldHit = true
                mc.thePlayer.swingItem()
                return
            }
            val rotationVector = RotationUtils.getVectorForRotation(Rotation(mc.thePlayer.rotationYaw, 0f))
            val x = mc.thePlayer.posX + rotationVector.xCoord * (mc.thePlayer.getDistanceToEntity(targetEntity) - 1.0f)
            val z = mc.thePlayer.posZ + rotationVector.zCoord * (mc.thePlayer.getDistanceToEntity(targetEntity) - 1.0f)
            val y = targetEntity!!.position.y + 0.25
            PathUtils.findPath(x, y + 1, z, 4.0).forEach(Consumer { pos: Vector3d ->
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        false
                    )
                )
            })
            mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK))
            shouldHit = false
            targetEntity = null
        } else shouldHit = false
    }
}