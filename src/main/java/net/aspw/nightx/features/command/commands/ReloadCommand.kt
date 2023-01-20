package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.features.command.CommandManager
import net.aspw.nightx.features.module.modules.client.Hud
import net.aspw.nightx.utils.misc.sound.TipSoundManager
import net.aspw.nightx.visual.client.clickgui.NewUi
import net.aspw.nightx.visual.font.Fonts

class ReloadCommand : Command("reload", arrayOf("configreload")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        NightX.commandManager = CommandManager()
        NightX.commandManager.registerCommands()
        NightX.isStarting = true
        NightX.scriptManager.disableScripts()
        NightX.scriptManager.unloadScripts()
        for (module in NightX.moduleManager.modules)
            NightX.moduleManager.generateCommand(module)
        NightX.scriptManager.loadScripts()
        NightX.scriptManager.enableScripts()
        Fonts.loadFonts()
        NightX.tipSoundManager = TipSoundManager()
        NightX.fileManager.loadConfig(NightX.fileManager.modulesConfig)
        NightX.isStarting = false
        NightX.fileManager.loadConfig(NightX.fileManager.valuesConfig)
        NightX.fileManager.loadConfig(NightX.fileManager.accountsConfig)
        NightX.fileManager.loadConfig(NightX.fileManager.friendsConfig)
        NightX.fileManager.loadConfig(NightX.fileManager.xrayConfig)
        NightX.fileManager.loadConfig(NightX.fileManager.hudConfig)
        NewUi.resetInstance()
        NightX.isStarting = false
        if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            NightX.tipSoundManager.popSound.asyncPlay(90f)
        }
        chat("Reloaded!")
    }
}
