package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.features.module.modules.client.Hud
import net.aspw.nightx.visual.hud.Config
import net.aspw.nightx.visual.hud.element.elements.Notification
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
                        val themeFile = File(NightX.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            try {
                                val theme = themeFile.readText()
                                NightX.isStarting = true
                                NightX.hud.clearElements()
                                NightX.hud = Config(theme).toHUD()
                                NightX.isStarting = false
                                if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                    NightX.tipSoundManager.popSound.asyncPlay(90f)
                                }
                                chat("§6Theme updated successfully!")
                                NightX.hud.addNotification(
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
                        val themeFile = File(NightX.fileManager.themesDir, args[2])

                        try {
                            if (themeFile.exists())
                                themeFile.delete()
                            themeFile.createNewFile()

                            val settingsTheme = Config(NightX.hud).toJson()
                            themeFile.writeText(settingsTheme)
                            if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                NightX.tipSoundManager.popSound.asyncPlay(90f)
                            }
                            chat("§6Successfully saved new theme!")
                            NightX.hud.addNotification(
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
                        val themeFile = File(NightX.fileManager.themesDir, args[2])

                        if (themeFile.exists()) {
                            themeFile.delete()
                            if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                NightX.tipSoundManager.popSound.asyncPlay(90f)
                            }
                            chat("§6Theme deleted successfully!")
                            NightX.hud.addNotification(
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

    private fun getLocalThemes(): Array<File>? = NightX.fileManager.themesDir.listFiles()
}