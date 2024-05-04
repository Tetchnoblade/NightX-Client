package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.api.PacketManager
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface

class RouteCommand : Command("route", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size == 4) {
            try {
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                }
                val posX = if (args[1].equals("~", true)) mc.thePlayer.posX else args[1].toDouble()
                val posY = if (args[2].equals("~", true)) mc.thePlayer.posY else args[2].toDouble()
                val posZ = if (args[3].equals("~", true)) mc.thePlayer.posZ else args[3].toDouble()
                PacketManager.routeX = posX
                PacketManager.routeY = posY
                PacketManager.routeZ = posZ
                PacketManager.isRouteTracking = true
                chat("Started route tracking. (X: ${posX}, Y: ${posY}, Z: ${posZ})")
                chat("Execute §8.route §ragain to stop tracking.")
            } catch (e: NumberFormatException) {
                chat("Failed to start route tracking.")
            }
        } else {
            if (PacketManager.isRouteTracking) {
                PacketManager.isRouteTracking = false
                chat("Stopped route tracking.")
            } else {
                chatSyntax("route <x y z>")
            }
        }
    }
}