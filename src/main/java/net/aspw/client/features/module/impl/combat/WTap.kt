package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.utils.timer.MSTimer
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(
    name = "WTap",
    category = ModuleCategory.COMBAT
)
class WTap : Module() {
    private val modeValue = ListValue("Mode", arrayOf("FullPacket", "FakeSneak", "Legit"), "FullPacket")
    private val delayValue = IntegerValue("Delay", 4, 1, 10)

    private val delayTimer = MSTimer()
    private var attackTicks = 0

    override fun onDisable() {
        attackTicks = 0
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (!MovementUtils.isMoving() || !mc.thePlayer.isSprinting) return
        if (delayTimer.hasTimePassed(delayValue.get().toLong())) {
            when (modeValue.get().lowercase()) {
                "fullpacket" -> {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    if (mc.thePlayer.isSprinting)
                        mc.thePlayer.isSprinting = true
                    mc.thePlayer.serverSprintState = true
                }

                "fakesneak" -> {
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SNEAKING
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer,
                            C0BPacketEntityAction.Action.STOP_SNEAKING
                        )
                    )
                }

                "legit" -> attackTicks = 2
            }

            delayTimer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get().lowercase()) {
            "legit" -> {
                if (MovementUtils.isMoving()) {
                    if (attackTicks == 2) {
                        mc.thePlayer.isSprinting = false
                        attackTicks = 1
                    } else if (attackTicks == 1) {
                        mc.thePlayer.isSprinting = true
                        attackTicks = 0
                    }
                } else if (attackTicks != 0) attackTicks = 0
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}