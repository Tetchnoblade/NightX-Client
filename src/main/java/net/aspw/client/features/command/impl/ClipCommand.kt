package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface

class ClipCommand : Command("clip", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val y = args[1].toDouble()
                val entity = if (mc.thePlayer.isRiding) mc.thePlayer.ridingEntity else mc.thePlayer

                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                }
                entity.setPosition(entity.posX, entity.posY + y, entity.posZ)
                chat("Successfully Teleported!")
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("clip <value>")
    }
}
