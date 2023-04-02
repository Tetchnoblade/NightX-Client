package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.StrafeEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.EntityUtils
import net.aspw.client.utils.RotationUtils
import net.aspw.client.utils.extensions.getDistanceToEntityBox
import net.aspw.client.utils.extensions.rotation
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import kotlin.random.Random

@ModuleInfo(name = "AimAssist", spacedName = "Aim Assist", category = ModuleCategory.COMBAT)
class AimAssist : Module() {

    private val rangeValue = FloatValue("Range", 1000F, 1F, 1000F, "m")
    private val turnSpeedValue = FloatValue("TurnSpeed", 180F, 1F, 180F, "°")
    private val fovValue = FloatValue("FOV", 180F, 1F, 180F, "°")
    private val centerValue = BoolValue("Center", true)
    private val lockValue = BoolValue("Lock", true)
    private val onClickValue = BoolValue("OnClick", false)
    private val jitterValue = BoolValue("Jitter", false)

    private val clickTimer = MSTimer()

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.gameSettings.keyBindAttack.isKeyDown)
            clickTimer.reset()

        if (onClickValue.get() && clickTimer.hasTimePassed(1L))
            return

        val player = mc.thePlayer ?: return

        val range = rangeValue.get()
        val entity = mc.theWorld.loadedEntityList
            .filter {
                EntityUtils.isSelected(it, true) && player.canEntityBeSeen(it) &&
                        player.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(it) <= fovValue.get()
            }
            .minByOrNull { RotationUtils.getRotationDifference(it) } ?: return

        if (!lockValue.get() && RotationUtils.isFaced(entity, range.toDouble()))
            return

        val boundingBox = entity.entityBoundingBox ?: return

        val destinationRotation = if (centerValue.get()) {
            RotationUtils.toRotation(RotationUtils.getCenter(boundingBox) ?: return, true)
        } else {
            RotationUtils.searchCenter(boundingBox, false, false, true, false, range)?.rotation!!
        }
        val rotation = RotationUtils.limitAngleChange(
            player.rotation,
            destinationRotation,
            (turnSpeedValue.get() + Math.random()).toFloat()
        )

        rotation.toPlayer(player)

        if (jitterValue.get()) {
            val yaw = Random.nextBoolean()
            val pitch = Random.nextBoolean()
            val yawNegative = Random.nextBoolean()
            val pitchNegative = Random.nextBoolean()

            if (yaw)
                player.rotationYaw += if (yawNegative) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)

            if (pitch) {
                player.rotationPitch += if (pitchNegative) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(
                    0F,
                    1F
                )
                if (player.rotationPitch > 90.0F)
                    player.rotationPitch = 90F
                else if (player.rotationPitch < -90.0F)
                    player.rotationPitch = -90F
            }
        }
    }
}