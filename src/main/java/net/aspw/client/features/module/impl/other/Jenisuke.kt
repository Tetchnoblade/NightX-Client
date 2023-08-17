package net.aspw.client.features.module.impl.other

import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.event.WorldEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.connection.LoginID
import net.aspw.client.util.misc.RandomUtils
import net.aspw.client.value.IntegerValue
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.util.ChatComponentText

@ModuleInfo(name = "Jenisuke", description = "", category = ModuleCategory.OTHER)
class Jenisuke : Module() {
    private val packetAmount = IntegerValue("Max-Stress", 100, 0, 200)
    private var jenisukeCount = 0

    override fun onDisable() {
        jenisukeCount = 0
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C08PacketPlayerBlockPlacement) {
            if (!mc.isIntegratedServerRunning) {
                jenisukeCount++
                chat(
                    packetAmount.get()
                        .toString() + "になるほどブロックを置いたらjenisukeがコーラがぶ飲み！現在のカウント:    " + jenisukeCount.toString()
                )
            } else {
                chat("ふっざけんな！シングル非対応！")
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        jenisukeCount = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.isIntegratedServerRunning) {
            if (jenisukeCount >= packetAmount.get()) {
                chat("まずは、KillAura!")
                mc.thePlayer.sendQueue.handleDisconnect(
                    S40PacketDisconnect(
                        ChatComponentText(
                            "§cYou are temporarily banned for " + "§f29d 23h 59m 59s" + " §cfrom this server!" + "\n\n§7Reason: " + "§fWATCHDOG CHEAT DETECTION " + "§7§o[GG-0" + RandomUtils.randomNumber(
                                6
                            ) + "]\n§7Find out more: " + "§b§nhttps://hypixel.net/appeal" + "\n\n§7Sharing your Ban ID may affect the processing of your appeal!"
                        )
                    )
                )
            }
        } else {
            jenisukeCount = 0
        }
    }
}