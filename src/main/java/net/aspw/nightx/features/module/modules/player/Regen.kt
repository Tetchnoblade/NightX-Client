package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion
import java.util.*

@ModuleInfo(name = "Regen", category = ModuleCategory.PLAYER)
class Regen : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Spartan"), "Vanilla")
    private val healthValue = IntegerValue("Health", 18, 0, 20)
    private val foodValue = IntegerValue("Food", 18, 0, 20)
    private val speedValue = IntegerValue("Speed", 25, 1, 100, "x")
    private val noAirValue = BoolValue("NoAir", false)
    private val potionEffectValue = BoolValue("PotionEffect", false)

    private var resetTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (resetTimer)
            mc.timer.timerSpeed = 1F
        resetTimer = false

        if ((!noAirValue.get() || mc.thePlayer.onGround) && !mc.thePlayer.capabilities.isCreativeMode &&
            mc.thePlayer.foodStats.foodLevel > foodValue.get() && mc.thePlayer.isEntityAlive && mc.thePlayer.health < healthValue.get()
        ) {
            if (potionEffectValue.get() && !mc.thePlayer.isPotionActive(Potion.regeneration))
                return

            when (modeValue.get().lowercase(Locale.getDefault())) {
                "vanilla" -> {
                    repeat(speedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }
                }

                "spartan" -> {
                    if (MovementUtils.isMoving() || !mc.thePlayer.onGround)
                        return

                    repeat(9) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }
            }
        }
    }
}
