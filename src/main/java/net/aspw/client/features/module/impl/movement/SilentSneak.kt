package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.event.UpdateEvent
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
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.get().equals("legit", true))
            mc.gameSettings.keyBindSneak.pressed = true
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (modeValue.get().equals("normal", true)) {
            if (mc.thePlayer.isSneaking)
                sneaking = false
            if (!sneaking && !mc.thePlayer.isSneaking) {
                if (event.eventState === EventState.PRE) {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                }
                sneaking = true
                chat("fix")
            }
        }
    }

    override fun onDisable() {
        sneaking = false
        if (mc.thePlayer == null) return
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "normal" -> mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SNEAKING
                )
            )

            "legit" -> if (!GameSettings.isKeyDown(
                    mc.gameSettings.keyBindSneak
                )
            ) mc.gameSettings.keyBindSneak.pressed = false
        }
    }
}