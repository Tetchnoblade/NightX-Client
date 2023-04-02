package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventTarget
import net.aspw.client.event.JumpEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import java.util.*

@ModuleInfo(name = "AntiWeb", spacedName = "Anti Web", category = ModuleCategory.MOVEMENT)
class AntiWeb : Module() {

    private val modeValue = ListValue(
        "Mode",
        arrayOf("None", "AAC", "LAAC", "Rewi", "AACv4", "Cardinal", "Horizon", "Spartan", "Negativity"),
        "AAC"
    )
    private val horizonSpeed = FloatValue("HorizonSpeed", 0.1F, 0.01F, 0.8F)

    private var usedTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        if (!mc.thePlayer.isInWeb)
            return

        when (modeValue.get().lowercase(Locale.getDefault())) {
            "none" -> mc.thePlayer.isInWeb = false
            "aac" -> {
                mc.thePlayer.jumpMovementFactor = 0.59f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0
            }

            "laac" -> {
                mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.movementInput.moveStrafe != 0f) 1.0f else 1.21f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }

            "rewi" -> {
                mc.thePlayer.jumpMovementFactor = 0.42f

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }
            //i hate this
            "aacv4" -> {
                mc.gameSettings.keyBindRight.pressed = false
                mc.gameSettings.keyBindBack.pressed = false
                mc.gameSettings.keyBindLeft.pressed = false

                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(0.25F)
                } else {
                    MovementUtils.strafe(0.12F)
                    mc.thePlayer.motionY = 0.0
                }
            }

            "cardinal" -> {
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(0.262F)
                } else {
                    MovementUtils.strafe(0.366F)
                }
            }

            "horizon" -> {
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(horizonSpeed.get())
                }
            }

            "spartan" -> {
                MovementUtils.strafe(0.27F)
                mc.timer.timerSpeed = 3.7F
                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.timer.timerSpeed = 1.7F
                }
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3F
                }
                usedTimer = true
            }

            "negativity" -> {
                mc.thePlayer.jumpMovementFactor = 0.4f
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.thePlayer.jumpMovementFactor = 0.53F
                }
                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0
            }
        }
    }

    fun onJump(event: JumpEvent) {
        if (modeValue.get().equals("AACv4", true) || modeValue.get().equals("Negativity", true) || modeValue.get()
                .equals("Intave", true)
        )
            event.cancelEvent()
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0F
    }


    override val tag: String
        get() = modeValue.get()
}
