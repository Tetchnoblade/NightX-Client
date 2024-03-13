package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C0BPacketEntityAction
import java.util.*

@ModuleInfo(name = "SilentSneak", spacedName = "Silent Sneak", category = ModuleCategory.MOVEMENT)
class SilentSneak : Module() {
    @JvmField
    val modeValue = ListValue("Mode", arrayOf("Normal", "Legit"), "Normal")

    private var sneaking = false

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer.isSneaking)
            sneaking = false
        if (!sneaking && !mc.thePlayer.isSneaking) {
            when (modeValue.get().lowercase(Locale.getDefault())) {
                "legit" -> mc.gameSettings.keyBindSneak.pressed = true

                "normal" -> {
                    if (event.eventState === EventState.PRE) {
                        mc.netHandler.addToSendQueue(
                            C0BPacketEntityAction(
                                mc.thePlayer,
                                C0BPacketEntityAction.Action.START_SNEAKING
                            )
                        )
                    }
                }
            }
            sneaking = true
        }
    }

    override fun onDisable() {
        sneaking = false
        if (mc.thePlayer == null) return
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "legit" -> if (!GameSettings.isKeyDown(
                    mc.gameSettings.keyBindSneak
                )
            ) mc.gameSettings.keyBindSneak.pressed = false

            "normal" -> mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SNEAKING
                )
            )
        }
    }
}