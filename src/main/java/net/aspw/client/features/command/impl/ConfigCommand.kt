package net.aspw.client.features.command.impl

import net.aspw.client.Launch
import net.aspw.client.features.command.Command
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.SettingsUtils
import net.aspw.client.utils.misc.StringUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.awt.Desktop
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
                        val scriptFile = File(Launch.fileManager.settingsDir, args[2])
                        if (scriptFile.exists()) {
                            try {
                                val settings = scriptFile.readText()
                                SettingsUtils.executeScript(settings)
                                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                                }
                                chat("§6Config updated successfully!")
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

                args[1].equals("onlineload", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val httpClient: CloseableHttpClient = HttpClients.createDefault()
                        val request = HttpGet(Launch.CLIENT_CONFIGS + args[2])
                        val response = httpClient.execute(request)
                        val entity = response.entity
                        val content = EntityUtils.toString(entity)
                        EntityUtils.consume(entity)
                        response.close()
                        httpClient.close()
                        SettingsUtils.executeScript(content)
                        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                        }
                        chat("§6Config updated successfully!")
                        return
                    }
                    chatSyntax("config onlineload <name>")
                    return
                }

                args[1].equals("save", ignoreCase = true) || args[1].equals("s", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val scriptFile = File(Launch.fileManager.settingsDir, args[2])

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
                            if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                                Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                            }
                            chat("§aSuccessfully saved new config!")
                        } catch (_: Throwable) {
                        }
                        return
                    }
                    chatSyntax("config save <name>")
                    return
                }

                args[1].equals("delete", ignoreCase = true) || args[1].equals("d", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val scriptFile = File(Launch.fileManager.settingsDir, args[2])

                        if (scriptFile.exists()) {
                            scriptFile.delete()
                            if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                                Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                            }
                            chat("§6Config deleted successfully!")
                            return
                        }
                        chatSyntax("config delete <name>")
                        return
                    }
                    chatSyntax("config delete <name>")
                    return
                }

                args[1].equals("fix", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val scriptFile = File(Launch.fileManager.settingsDir, args[2])

                        if (scriptFile.exists()) {
                            try {
                                val settings = scriptFile.readText()
                                SettingsUtils.executeScript(settings)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

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
                                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                                }
                                chat("§6Config fixed successfully!")
                            } catch (_: Throwable) {
                            }
                        } else chatSyntax("config fix <name>")
                        return
                    }
                    chatSyntax("config fix <name>")
                    return
                }

                args[1].equals("list", ignoreCase = true) -> {
                    chat("§cConfigs:")

                    val settings = this.getLocalSettings() ?: return

                    for (file in settings)
                        chat("" + file.name)
                    return
                }

                args[1].equals("onlinelist", ignoreCase = true) -> {
                    val httpClient: CloseableHttpClient = HttpClients.createDefault()
                    val request = HttpGet(Launch.CLIENT_CONFIGLIST)
                    val response = httpClient.execute(request)
                    val entity = response.entity
                    val content = EntityUtils.toString(entity)
                    EntityUtils.consume(entity)
                    response.close()
                    httpClient.close()
                    chat("§cOnlineConfigs:")
                    chat(content)
                    return
                }

                args[1].equals("folder", ignoreCase = true) -> {
                    Desktop.getDesktop().open(Launch.fileManager.settingsDir)
                    chat("Successfully opened configs folder.")
                    return
                }
            }
        }
        chatSyntax("config <load/save/list/delete/fix/folder/onlineload/onlinelist>")
    }

    private fun getLocalSettings(): Array<File>? = Launch.fileManager.settingsDir.listFiles()

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("delete", "list", "load", "l", "save", "fix", "folder", "onlineload", "onlinelist").filter {
                it.startsWith(
                    args[0],
                    true
                )
            }

            2 -> {
                when (args[0].lowercase(Locale.getDefault())) {
                    "delete", "load", "l", "fix" -> {
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
}