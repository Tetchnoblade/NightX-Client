package net.aspw.client.features.module.impl.movement

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C0BPacketEntityAction
import java.util.*

@ModuleInfo(name = "SilentSneak", spacedName = "Silent Sneak", category = ModuleCategory.MOVEMENT)
class SilentSneak : Module() {
    @JvmField
    val modeValue = ListValue("Mode", arrayOf("Normal", "Legit", "Vanilla", "Switch", "AAC3.6.4"), "Normal")

    @JvmField
    val stopMoveValue = BoolValue("StopMove", false)
    private var sneaked = false
    override fun onEnable() {
        if (mc.thePlayer == null) return
        if ("vanilla".equals(modeValue.get(), ignoreCase = true)) {
            mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.START_SNEAKING
                )
            )
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (stopMoveValue.get() && MovementUtils.isMoving()) {
            if (sneaked) {
                onDisable()
                sneaked = false
            }
            return
        }
        sneaked = true
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "legit" -> mc.gameSettings.keyBindSneak.pressed = true
            "switch" -> when (event.eventState) {
                EventState.PRE -> {
                    if (!MovementUtils.isMoving()) return
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.STOP_SNEAKING
                        )
                    )
                }

                EventState.POST -> {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.STOP_SNEAKING
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                }
            }

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

            "aac3.6.4" -> {
                mc.gameSettings.keyBindSneak.pressed = true
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getSpeed() * 1.251f)
                } else {
                    MovementUtils.strafe(MovementUtils.getSpeed() * 1.03f)
                }
            }
        }
    }

    override fun onDisable() {
        if (mc.thePlayer == null) return
        when (modeValue.get().lowercase(Locale.getDefault())) {
            "legit", "vanilla", "switch", "aac3.6.4" -> if (!GameSettings.isKeyDown(
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