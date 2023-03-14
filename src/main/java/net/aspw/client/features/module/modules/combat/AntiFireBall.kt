package net.aspw.client.features.module.modules.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation

@ModuleInfo(name = "AntiFireBall", spacedName = "Anti Fire Ball", category = ModuleCategory.COMBAT)
class AntiFireBall : Module() {
    private val timer = MSTimer()

    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val rotationValue = BoolValue("Rotation", true)

    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityFireball && mc.thePlayer.getDistanceToEntity(entity) < 5.5 && timer.hasTimePassed(300)) {
                if (rotationValue.get()) {
                    RotationUtils.setTargetRotation(RotationUtils.getRotations(entity))
                }

                mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

                if (swingValue.get().equals("Normal")) {
                    mc.thePlayer.swingItem()
                } else if (swingValue.get().equals("Packet")) {
                    mc.netHandler.addToSendQueue(C0APacketAnimation())
                }

                timer.reset()
                break
            }
        }
    }
}