package net.ccbluex.liquidbounce.features.special

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.utils.MinecraftInstance

object MacroManager : MinecraftInstance(), Listenable {

    val macroMapping = hashMapOf<Int, String>()

    @EventTarget
    fun onKey(event: KeyEvent) {
        mc.thePlayer ?: return
        LiquidBounce.commandManager
        macroMapping.filter { it.key == event.key }.forEach {
            if (it.value.startsWith(LiquidBounce.commandManager.prefix))
                LiquidBounce.commandManager.executeCommands(it.value)
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