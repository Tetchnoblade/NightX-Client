package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.exploit.Plugins
import net.aspw.client.features.module.impl.visual.Interface

class PluginsCommand : Command("plugins", arrayOf("pl")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
        }
        Launch.moduleManager.getModule(Plugins::class.java)?.state = true
    }
}