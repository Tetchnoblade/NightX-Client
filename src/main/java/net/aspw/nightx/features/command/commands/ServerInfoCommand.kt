package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.Listenable
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.command.Command
import net.minecraft.network.handshake.client.C00Handshake

class ServerInfoCommand : Command("serverinfo", emptyArray()), Listenable {
    init {
        NightX.eventManager.registerListener(this)
    }

    private var ip = ""
    private var port = 0

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (mc.currentServerData == null) {
            chat("This command does not work in single player.")
            return
        }

        val data = mc.currentServerData

        chat("Server infos:")
        chat("§7Name: §8${data.serverName}")
        chat("§7IP: §8$ip:$port")
        chat("§7Players: §8${data.populationInfo}")
        chat("§7MOTD: §8${data.serverMOTD}")
        chat("§7ServerVersion: §8${data.gameVersion}")
        chat("§7ProtocolVersion: §8${data.version}")
        chat("§7Ping: §8${data.pingToServer}")
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C00Handshake) {
            ip = packet.ip
            port = packet.port
        }
    }

    override fun handleEvents() = true
}