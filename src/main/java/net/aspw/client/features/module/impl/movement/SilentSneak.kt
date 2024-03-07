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

    private var sneaked = false
    override fun onEnable() {
        if (mc.thePlayer == null) return
    }

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        sneaked = true
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
    }

    override fun onDisable() {
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
        super.onDisable()
    }
}