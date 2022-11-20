package net.aspw.nightx.features.module.modules.combat

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.RotationUtils
import net.aspw.nightx.utils.timer.MSTimer
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.ListValue
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation

@ModuleInfo(name = "AntiFireBall", spacedName = "AntiFire Ball", category = ModuleCategory.COMBAT)
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