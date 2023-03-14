package net.aspw.client.features.command.commands

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.utils.misc.StringUtils
import java.util.*

class EnemyCommand : Command("enemy", arrayOf("enemys")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            val enemysConfig = Client.fileManager.enemysConfig

            when {
                args[1].equals("add", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val name = args[2]

                        if (name.isEmpty()) {
                            chat("The name is empty.")
                            return
                        }

                        if (if (args.size > 3) enemysConfig.addEnemy(
                                name,
                                StringUtils.toCompleteString(args, 3)
                            ) else enemysConfig.addEnemy(name)
                        ) {
                            Client.fileManager.saveConfig(enemysConfig)
                            chat("§a§l$name§3 was added to your enemy list.")
                            playEdit()
                        } else
                            chat("The name is already in the list.")
                        return
                    }
                    chatSyntax("enemy add <name> [alias]")
                    return
                }

                args[1].equals("remove", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val name = args[2]

                        if (enemysConfig.removeEnemy(name)) {
                            Client.fileManager.saveConfig(enemysConfig)
                            chat("§a§l$name§3 was removed from your enemy list.")
                            playEdit()
                        } else
                            chat("This name is not in the list.")
                        return
                    }
                    chatSyntax("enemy remove <name>")
                    return
                }

                args[1].equals("clear", ignoreCase = true) -> {
                    val enemys = enemysConfig.enemys.size
                    enemysConfig.clearEnemys()
                    Client.fileManager.saveConfig(enemysConfig)
                    chat("Removed $enemys enemy(s).")
                    return
                }

                args[1].equals("list", ignoreCase = true) -> {
                    chat("Your Enemys:")

                    for (enemy in enemysConfig.enemys)
                        chat("§7> §a§l${enemy.playerName} §c(§7§l${enemy.alias}§c)")

                    chat("You have §c${enemysConfig.enemys.size}§3 enemys.")
                    return
                }
            }
        }

        chatSyntax("enemy <add/remove/list/clear>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("add", "remove", "list", "clear").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].lowercase(Locale.getDefault())) {
                    "add" -> {
                        return mc.theWorld.playerEntities
                            .map { it.name }
                            .filter { it.startsWith(args[1], true) }
                    }

                    "remove" -> {
                        return Client.fileManager.enemysConfig.enemys
                            .map { it.playerName }
                            .filter { it.startsWith(args[1], true) }
                    }
                }
                return emptyList()
            }

            else -> emptyList()
        }
    }
}