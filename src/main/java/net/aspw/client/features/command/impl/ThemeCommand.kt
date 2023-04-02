package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.visual.hud.Config
import net.aspw.client.visual.hud.element.elements.Notification
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
                        val themeFile = File(Client.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            try {
                                val theme = themeFile.readText()
                                Client.isStarting = true
                                Client.hud.clearElements()
                                Client.hud = Config(theme).toHUD()
                                Client.isStarting = false
                                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                                }
                                chat("§6Theme updated successfully!")
                                Client.hud.addNotification(
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
                        val themeFile = File(Client.fileManager.themesDir, args[2])

                        try {
                            if (themeFile.exists())
                                themeFile.delete()
                            themeFile.createNewFile()

                            val settingsTheme = Config(Client.hud).toJson()
                            themeFile.writeText(settingsTheme)
                            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                            }
                            chat("§6Successfully saved new theme!")
                            Client.hud.addNotification(
                                Notification(
                                    "Config saved successfully!",
                                    Notification.Type.SUCCESS
                                )
                            )
                        } catch (throwable: Throwable) {
                        }
                        return
                    }

                    chatSyntax("theme save <name>")
                    return
                }

                args[1].equals("delete", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val themeFile = File(Client.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            themeFile.delete()
                            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                            }
                            chat("§6Theme deleted successfully!")
                            Client.hud.addNotification(
                                Notification(
                                    "Config deleted successfully!",
                                    Notification.Type.SUCCESS
                                )
                            )
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

    private fun getLocalThemes(): Array<File>? = Client.fileManager.themesDir.listFiles()
}