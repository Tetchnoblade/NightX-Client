package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.Config
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import java.io.File
import java.io.IOException
import java.util.*

class ThemeCommand : Command("theme", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("load", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(LiquidBounce.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            try {
                                val theme = themeFile.readText()
                                LiquidBounce.isStarting = true
                                LiquidBounce.hud.clearElements()
                                LiquidBounce.hud = Config(theme).toHUD()
                                LiquidBounce.isStarting = false
                                chat("§6Theme updated successfully!")
                                LiquidBounce.hud.addNotification(
                                    Notification(
                                        "Theme updated successfully!",
                                        Notification.Type.SUCCESS
                                    )
                                )
                                playEdit()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            return
                        }
                        return
                    }
                    chatSyntax("theme load <name>")
                    return
                }

                args[1].equals("save", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(LiquidBounce.fileManager.themesDir, args[2])

                        try {
                            if (themeFile.exists())
                                themeFile.delete()
                            themeFile.createNewFile()

                            val settingsTheme = Config(LiquidBounce.hud).toJson()
                            themeFile.writeText(settingsTheme)
                            chat("§6Successfully saved new theme!")
                        } catch (throwable: Throwable) {
                        }
                        return
                    }

                    chatSyntax("theme save <name>")
                    return
                }

                args[1].equals("delete", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(LiquidBounce.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            themeFile.delete()
                            chat("§6Theme deleted successfully!")
                            return
                        }
                        return
                    }
                    chatSyntax("theme delete <name>")
                    return
                }

                args[1].equals("list", ignoreCase = true) -> {
                    chat("§cThemes:")

                    val themes = this.getLocalThemes() ?: return

                    for (file in themes)
                        chat("" + file.name)
                    return
                }
            }
        }
        chatSyntax("theme <load/save/list/delete>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("delete", "list", "load", "save").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].lowercase(Locale.getDefault())) {
                    "delete", "load" -> {
                        val settings = this.getLocalThemes() ?: return emptyList()

                        return settings
                            .map { it.name }
                            .filter { it.startsWith(args[1], true) }
                    }
                }
                return emptyList()
            }

            else -> emptyList()
        }
    }

    private fun getLocalThemes(): Array<File>? = LiquidBounce.fileManager.themesDir.listFiles()
}