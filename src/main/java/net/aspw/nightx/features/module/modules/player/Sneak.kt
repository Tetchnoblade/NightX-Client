package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.EventState
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.ListValue
import net.minecraft.client.settings.GameSettings
import net.minecraft.network.play.client.C0BPacketEntityAction
import java.util.*

@ModuleInfo(name = "Sneak", category = ModuleCategory.PLAYER)
class Sneak : Module() {
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