package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.MovementUtils
import net.aspw.client.value.ListValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(
    name = "WTap",
    category = ModuleCategory.COMBAT
)
class WTap : Module() {
    private val modeValue = ListValue("Mode", arrayOf("FullPacket", "LessPacket", "FakeSneak"), "FullPacket")

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (!MovementUtils.isMoving()) return
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

            "lesspacket" -> {
                if (mc.thePlayer.isSprinting)
                    mc.thePlayer.isSprinting = false
                mc.thePlayer.serverSprintState = false
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
        }
    }

    override val tag: String
        get() = modeValue.get()
}