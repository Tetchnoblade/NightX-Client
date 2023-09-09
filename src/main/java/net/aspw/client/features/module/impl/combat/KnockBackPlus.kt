package net.aspw.client.features.module.impl.combat

import net.aspw.client.event.AttackEvent
import net.aspw.client.event.EventTarget
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.ListValue
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(
    name = "KnockBack+", spacedName = "Knock Back+", description = "",
    category = ModuleCategory.COMBAT
)
class KnockBackPlus : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Legit", "Packet"), "Legit")

    @EventTarget
    fun onAttack(event: AttackEvent) {
        when (modeValue.get().lowercase()) {
            "legit" -> {
                if (mc.thePlayer.isSprinting)
                    mc.thePlayer.isSprinting = false
            }

            "packet" -> {
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
        }
    }

    override val tag: String
        get() = modeValue.get()
}