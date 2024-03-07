package net.aspw.client.features.api

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.KeyEvent
import net.aspw.client.event.Listenable
import net.aspw.client.utils.MinecraftInstance

object MacroManager : MinecraftInstance(), Listenable {

    val macroMapping = hashMapOf<Int, String>()

    @EventTarget
    fun onKey(event: KeyEvent) {
        mc.thePlayer ?: return
        Launch.commandManager
        macroMapping.filter { it.key == event.key }.forEach {
            if (it.value.startsWith("."))
                Launch.commandManager.executeCommands(it.value)
            else
                mc.thePlayer.sendChatMessage(it.value)
        }
    }

    fun addMacro(keyCode: Int, command: String) {
        macroMapping[keyCode] = command
    }

    fun removeMacro(keyCode: Int) {
        macroMapping.remove(keyCode)
    }

    override fun handleEvents(): Boolean = true

}