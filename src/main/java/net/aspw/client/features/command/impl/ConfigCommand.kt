package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Hud
import net.aspw.client.utils.SettingsUtils
import net.aspw.client.utils.misc.MiscUtils
import net.aspw.client.utils.misc.StringUtils
import net.aspw.client.visual.hud.element.elements.Notification
import java.io.File
import java.io.IOException
import java.util.*

class ConfigCommand : Command("config", arrayOf("c")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("load", ignoreCase = true) || args[1].equals("l", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val scriptFile = File(Client.fileManager.settingsDir, args[2])
                        if (scriptFile.exists()) {
                            try {
                                val settings = scriptFile.readText()
                                SettingsUtils.executeScript(settings)
                                if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                    Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                                }
                                chat("§6Config updated successfully!")
                                Client.hud.addNotification(
                                    Notification(
                                        "Config updated successfully!",
                                        Notification.Type.SUCCESS
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            return
                        }
                        return
                    }
                    chatSyntax("config load <name>")
                    return
                }

                args[1].equals("save", ignoreCase = true) || args[1].equals("s", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val scriptFile = File(Client.fileManager.settingsDir, args[2])

                        try {
                            if (scriptFile.exists())
                                if (scriptFile.delete()) {
                                    scriptFile.createNewFile()
                                } else {
                                    return
                                }

                            val option =
                                if (args.size > 3) StringUtils.toCompleteString(args, 3)
                                    .lowercase(Locale.getDefault()) else "all"
                            val values = option.contains("all") || option.contains("values")
                            val binds = option.contains("all") || option.contains("binds")
                            val states = option.contains("all") || option.contains("states")
                            if (!values && !binds && !states) {
                                return
                            }
                            val settingsScript = SettingsUtils.generateScript(values, binds, states)
                            scriptFile.writeText(settingsScript)
                            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                            }
                            chat("§aSuccessfully saved new config!")
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

                    chatSyntax("config save <name>")
                    return
                }

                args[1].equals("delete", ignoreCase = true) || args[1].equals("d", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val scriptFile = File(Client.fileManager.settingsDir, args[2])

                        if (scriptFile.exists()) {
                            scriptFile.delete()
                            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                            }
                            chat("§6Config deleted successfully!")
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
                    chatSyntax("config delete <name>")
                    return
                }

                args[1].equals("list", ignoreCase = true) -> {
                    chat("§cConfigs:")

                    val settings = this.getLocalSettings() ?: return

                    for (file in settings)
                        chat("" + file.name)
                    return
                }

                args[1].equals("download", ignoreCase = true) || args[1].equals("dl", ignoreCase = true) -> {
                    if (args.size > 1) {
                        try {
                            if (Client.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
                            }
                            MiscUtils.showURL(Client.CLIENT_CONFIGS)
                            chat("§6Opened browser to download configs!")
                            Client.hud.addNotification(
                                Notification(
                                    "Opened browser to download configs!",
                                    Notification.Type.INFO
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        return
                    }
                    return
                }
            }
        }
        chatSyntax("config <load/save/list/delete/download>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf(
                "delete",
                "d",
                "list",
                "load",
                "l",
                "save",
                "s",
                "download",
                "dl"
            ).filter { it.startsWith(args[0], true) }

            2 -> {
                when (args[0].lowercase(Locale.getDefault())) {
                    "delete", "d", "load", "l" -> {
                        val settings = this.getLocalSettings() ?: return emptyList()

                        return settings
                            .map { it.name }
                            .filter { it.startsWith(args[1], true) }
                    }
                }
                return emptyList()
            }

            3 -> {
                if (args[0].equals("save", true) || args[0].equals("s", true)) {
                    return listOf("all", "states", "binds", "values").filter { it.startsWith(args[2], true) }
                }
                return emptyList()
            }

            else -> emptyList()
        }
    }

    private fun getLocalSettings(): Array<File>? = Client.fileManager.settingsDir.listFiles()
}