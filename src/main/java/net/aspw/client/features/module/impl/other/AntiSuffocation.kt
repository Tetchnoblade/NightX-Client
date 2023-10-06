package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(
    name = "AntiSuffocation",
    spacedName = "Anti Suffocation",
    description = "",
    category = ModuleCategory.OTHER
)
class AntiSuffocation : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Legit", "Teleport", "GodMode"), "Legit")
    private val breakPositionValue = ListValue(
        "BreakPosition",
        arrayOf("Normal", "Down"),
        "Normal"
    ) { modeValue.get().equals("legit", ignoreCase = true) }
    private val swingValue = ListValue(
        "Swing",
        arrayOf("Normal", "Packet", "None"),
        "Packet"
    ) { modeValue.get().equals("legit", ignoreCase = true) }

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.isEntityInsideOpaqueBlock) {
            when (modeValue.get().lowercase()) {
                "legit" -> {
                    when (breakPositionValue.get().lowercase()) {
                        "normal" -> mc.playerController.onPlayerDamageBlock(
                            BlockPos(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + 1,
                                mc.thePlayer.posZ
                            ), EnumFacing.NORTH
                        )

                        "down" -> mc.playerController.onPlayerDamageBlock(
                            BlockPos(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY - 1,
                                mc.thePlayer.posZ
                            ), EnumFacing.NORTH
                        )
                    }
                    when (swingValue.get().lowercase(Locale.getDefault())) {
                        "normal" -> mc.thePlayer.swingItem()
                        "packet" -> mc.netHandler.addToSendQueue(C0APacketAnimation())
                    }
                }

                "teleport" -> {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ)
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (mc.thePlayer.isEntityInsideOpaqueBlock) {
            when (modeValue.get().lowercase()) {
                "godmode" -> {
                    if (packet is C03PacketPlayer || packet is C0BPacketEntityAction)
                        event.cancelEvent()
                }
            }
        }
    }
}