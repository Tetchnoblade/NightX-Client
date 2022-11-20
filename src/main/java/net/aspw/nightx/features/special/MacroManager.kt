package net.aspw.nightx.features.special

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.KeyEvent
import net.aspw.nightx.event.Listenable
import net.aspw.nightx.utils.MinecraftInstance

object MacroManager : MinecraftInstance(), Listenable {

    val macroMapping = hashMapOf<Int, String>()

    @EventTarget
    fun onKey(event: KeyEvent) {
        mc.thePlayer ?: return
        NightX.commandManager
        macroMapping.filter { it.key == event.key }.forEach {
            if (it.value.startsWith(NightX.commandManager.prefix))
                NightX.commandManager.executeCommands(it.value)
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