package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtilsFix
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "EntityFlight", "Entity Flight", category = ModuleCategory.MOVEMENT)
class EntityFlight : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Motion", "Clip", "Velocity"), "Motion")
    private val speedValue = FloatValue("Speed", 0.3f, 0.0f, 1.0f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.isRiding) return

        val vehicle = mc.thePlayer.ridingEntity
        val x = -sin(MovementUtilsFix.direction) * speedValue.get()
        val z = cos(MovementUtilsFix.direction) * speedValue.get()

        when (modeValue.get().lowercase()) {
            "motion" -> {
                vehicle.motionX = x
                vehicle.motionY = (if (mc.gameSettings.keyBindJump.pressed) speedValue.get() else 0).toDouble()
                vehicle.motionZ = z
            }

            "clip" -> {
                vehicle.setPosition(
                    vehicle.posX + x,
                    vehicle.posY + (if (mc.gameSettings.keyBindJump.pressed) speedValue.get() else 0).toDouble(),
                    vehicle.posZ + z
                )
            }

            "velocity" -> {
                vehicle.addVelocity(x, if (mc.gameSettings.keyBindJump.pressed) speedValue.get().toDouble() else 0.0, z)
            }
        }
    }
}