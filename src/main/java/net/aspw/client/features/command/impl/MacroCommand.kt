package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.api.MacroManager
import net.aspw.client.features.command.Command
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.misc.StringUtils
import org.lwjgl.input.Keyboard
import java.util.*

class MacroCommand : Command("macro", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            val key = Keyboard.getKeyIndex(args[2].uppercase(Locale.getDefault()))
            if (key == 0) {
                chat("§c§lKeybind doesn't exist, or not allowed.")
                chatSyntax("macro <list/clear/add/remove>")
                return
            }
            when (args[1].lowercase(Locale.getDefault())) {
                "add" -> {
                    if (args.size < 4) {
                        chatSyntax("macro add <key name> <message>")
                        return
                    }
                    val message = StringUtils.toCompleteString(args, 3)
                    val existed = MacroManager.macroMapping.containsKey(key)
                    MacroManager.addMacro(key, message)
                    Client.fileManager.saveConfig(Client.fileManager.valuesConfig)
                    if (existed)
                        chat("§a§lSuccessfully changed macro in key §7${Keyboard.getKeyName(key)} to §r$message.")
                    else
                        chat("§a§lSuccessfully added §r$message §a§lto key §7${Keyboard.getKeyName(key)}.")
                    playEdit()
                    return
                }

                "remove" -> {
                    if (MacroManager.macroMapping.containsKey(key)) {
                        val lastMessage = MacroManager.macroMapping[key]
                        MacroManager.removeMacro(key)
                        Client.fileManager.saveConfig(Client.fileManager.valuesConfig)
                        chat("§a§lSuccessfully removed the macro §r$lastMessage §a§lfrom §7${Keyboard.getKeyName(key)}.")
                        playEdit()
                        return
                    }
                    chat("§c§lThere's no macro bound to this key.")
                    chatSyntax("macro remove <key name>")
                    return
                }
            }
        }
        if (args.size == 2) {
            when (args[1].lowercase(Locale.getDefault())) {
                "list" -> {
                    chat("§6§lMacros:")
                    MacroManager.macroMapping.forEach {
                        ClientUtils.displayChatMessage("§6> §c${Keyboard.getKeyName(it.key)}: §r${it.value}")
                    }
                    return
                }

                "clear" -> {
                    MacroManager.macroMapping.clear()
                    playEdit()
                    Client.fileManager.saveConfig(Client.fileManager.valuesConfig)
                    chat("§a§lSuccessfully cleared macro list.")
                    return
                }

                "add" -> {
                    chatSyntax("macro add <key name> <message>")
                    return
                }

                "remove" -> {
                    chatSyntax("macro remove <key name>")
                    return
                }
            }
        }

        chatSyntax("macro <list/clear/add/remove>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("add", "remove", "list", "clear")
                .filter { it.startsWith(args[0], true) }

            else -> emptyList()
        }
    }
}