package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.ui.client.clickgui.newVer.NewUi
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.misc.sound.TipSoundManager

class ReloadCommand : Command("reload", arrayOf("configreload")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        LiquidBounce.commandManager = CommandManager()
        LiquidBounce.commandManager.registerCommands()
        LiquidBounce.isStarting = true
        LiquidBounce.scriptManager.disableScripts()
        LiquidBounce.scriptManager.unloadScripts()
        for (module in LiquidBounce.moduleManager.modules)
            LiquidBounce.moduleManager.generateCommand(module)
        LiquidBounce.scriptManager.loadScripts()
        LiquidBounce.scriptManager.enableScripts()
        Fonts.loadFonts()
        LiquidBounce.tipSoundManager = TipSoundManager()
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.modulesConfig)
        LiquidBounce.isStarting = false
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.valuesConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.accountsConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.friendsConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.xrayConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.hudConfig)
        NewUi.resetInstance()
        LiquidBounce.isStarting = false
        chat("Reloaded!")
    }
}
