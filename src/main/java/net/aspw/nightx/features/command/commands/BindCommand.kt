package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.ui.client.hud.element.elements.Notification
import org.lwjgl.input.Keyboard
import java.util.*

class BindCommand : Command("bind", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            // Get module by name
            val module = NightX.moduleManager.getModule(args[1])

            if (module == null) {
                chat("Module §a§l" + args[1] + "§3 not found.")
                return
            }
            // Find key by name and change
            val key = Keyboard.getKeyIndex(args[2].uppercase(Locale.getDefault()))
            module.keyBind = key

            // Response to user
            chat("Bound module §a§l${module.name}§3 to key §a§l${Keyboard.getKeyName(key)}§3.")
            NightX.hud.addNotification(
                Notification(
                    "Bound ${module.name} to ${Keyboard.getKeyName(key)}",
                    Notification.Type.SUCCESS
                )
            )
            playEdit()
            return
        }

        chatSyntax(arrayOf("<module> <key>", "<module> none"))
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> NightX.moduleManager.modules
                .map { it.name }
                .filter { it.startsWith(moduleName, true) }
                .toList()

            else -> emptyList()
        }
    }
}