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
            if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
            }
            PacketManager.routeX = args[1].toDouble()
            PacketManager.routeY = args[2].toDouble()
            PacketManager.routeZ = args[3].toDouble()
            PacketManager.isRouteTracking = true
            chat("Started Route Tracking. (X: ${args[1]}, Y: ${args[2]}, Z: ${args[3]})")
            chat("Execute §8.route §ragain to stop tracking.")
        } else {
            if (PacketManager.isRouteTracking) {
                PacketManager.isRouteTracking = false
                chat("Stopped Route Tracking.")
            } else {
                chatSyntax("route <x y z>")
            }
        }
    }
}