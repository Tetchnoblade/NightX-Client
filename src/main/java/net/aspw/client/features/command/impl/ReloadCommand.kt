package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.api.CombatManager
import net.aspw.client.features.command.Command
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.util.misc.sound.TipSoundManager
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.tab.NewUi

class ReloadCommand : Command("reload", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        Client.commandManager = CommandManager()
        Client.commandManager.registerCommands()
        Client.scriptManager.disableScripts()
        Client.scriptManager.unloadScripts()
        for (module in Client.moduleManager.modules)
            Client.moduleManager.generateCommand(module)
        Client.scriptManager.loadScripts()
        Client.scriptManager.enableScripts()
        Client.tipSoundManager = TipSoundManager()
        Client.combatManager = CombatManager()
        Client.fileManager.loadConfig(Client.fileManager.modulesConfig)
        Client.fileManager.loadConfig(Client.fileManager.valuesConfig)
        Client.fileManager.loadConfig(Client.fileManager.accountsConfig)
        Client.fileManager.loadConfig(Client.fileManager.friendsConfig)
        Client.clickGui = ClickGui()
        NewUi.resetInstance()
        if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
            Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
        }
        chat("Reloaded!")
    }
}
