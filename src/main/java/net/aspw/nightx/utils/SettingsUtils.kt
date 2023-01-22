package net.aspw.nightx.utils

import net.aspw.nightx.NightX
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.modules.client.*
import net.aspw.nightx.features.special.MacroManager
import net.aspw.nightx.utils.misc.HttpUtils.get
import net.aspw.nightx.utils.misc.StringUtils
import net.aspw.nightx.utils.render.ColorUtils.translateAlternateColorCodes
import net.aspw.nightx.value.*
import org.lwjgl.input.Keyboard

object SettingsUtils {

    /**
     * Execute settings [script]
     */
    fun executeScript(script: String) {
        script.lines().filter { it.isNotEmpty() && !it.startsWith('#') }.forEachIndexed { index, s ->
            val args = s.split(" ").toTypedArray()

            if (args.size <= 1) {
                return@forEachIndexed
            }

            when (args[0]) {
                "chat" -> ClientUtils.displayChatMessage(
                    NightX.CLIENT_CHAT + "§e${
                        translateAlternateColorCodes(
                            StringUtils.toCompleteString(args, 1)
                        )
                    }"
                )

                "unchat" -> ClientUtils.displayChatMessage(
                    translateAlternateColorCodes(
                        StringUtils.toCompleteString(
                            args,
                            1
                        )
                    )
                )

                "load" -> {
                    val urlRaw = StringUtils.toCompleteString(args, 1)
                    val url = urlRaw

                    try {
                        ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§7Loading settings from §a§l$url§7...")
                        executeScript(get(url))
                        ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§7Loaded settings from §a§l$url§7.")
                    } catch (e: Exception) {
                        ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§7Failed to load settings from §a§l$url§7.")
                    }
                }

                "macro" -> {
                    if (args[1] != "0") {
                        val macroBind = args[1]
                        val macroCommand = StringUtils.toCompleteString(args, 2)
                        try {
                            MacroManager.addMacro(macroBind.toInt(), macroCommand)
                            ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "Macro §c§l$macroCommand§7 has been bound to §a§l$macroBind§7.")
                        } catch (e: Exception) {
                            ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a§l${e.javaClass.name}§7(${e.message}) §cAn Exception occurred while importing macro with keybind §a§l$macroBind§c to §a§l$macroCommand§c.")
                        }
                    }
                }

                "targetPlayer", "targetPlayers" -> {
                    EntityUtils.targetPlayer = args[1].equals("true", ignoreCase = true)
                    ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a§l${args[0]}§7 set to §c§l${EntityUtils.targetPlayer}§7.")
                }

                "targetMobs" -> {
                    EntityUtils.targetMobs = args[1].equals("true", ignoreCase = true)
                    ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a§l${args[0]}§7 set to §c§l${EntityUtils.targetMobs}§7.")
                }

                "targetAnimals" -> {
                    EntityUtils.targetAnimals = args[1].equals("true", ignoreCase = true)
                    ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a§l${args[0]}§7 set to §c§l${EntityUtils.targetAnimals}§7.")
                }

                "targetInvisible" -> {
                    EntityUtils.targetInvisible = args[1].equals("true", ignoreCase = true)
                    ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a§l${args[0]}§7 set to §c§l${EntityUtils.targetInvisible}§7.")
                }

                "targetDead" -> {
                    EntityUtils.targetDead = args[1].equals("false", ignoreCase = true)
                    ClientUtils.displayChatMessage(NightX.CLIENT_CHAT + "§a§l${args[0]}§7 set to §c§l${EntityUtils.targetDead}§7.")
                }

                else -> {
                    if (args.size != 3) {
                        return@forEachIndexed
                    }

                    val moduleName = args[0]
                    val valueName = args[1]
                    val value = args[2]
                    val module = NightX.moduleManager.getModule(moduleName)

                    if (module == null) {
                        return@forEachIndexed
                    }

                    if (valueName.equals("toggle", ignoreCase = true)) {
                        module.state = value.equals("true", ignoreCase = true)
                        return@forEachIndexed
                    }

                    if (valueName.equals("bind", ignoreCase = true)) {
                        module.keyBind = Keyboard.getKeyIndex(value)
                        return@forEachIndexed
                    }

                    val moduleValue = module.getValue(valueName)
                    if (moduleValue == null) {
                        return@forEachIndexed
                    }

                    try {
                        when (moduleValue) {
                            is BoolValue -> moduleValue.changeValue(value.toBoolean())
                            is FloatValue -> moduleValue.changeValue(value.toFloat())
                            is IntegerValue -> moduleValue.changeValue(value.toInt())
                            is TextValue -> moduleValue.changeValue(value)
                            is ListValue -> moduleValue.changeValue(value)
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }

        NightX.fileManager.saveConfig(NightX.fileManager.valuesConfig)
    }

    /**
     * Generate settings script
     */
    fun generateScript(values: Boolean, binds: Boolean, states: Boolean): String {
        val stringBuilder = StringBuilder()

        MacroManager.macroMapping.filter { it.key != 0 }
            .forEach { stringBuilder.append("macro ${it.key} ${it.value}").append("\n") }

        NightX.moduleManager.modules.filter { it.category !== ModuleCategory.RENDER && it !is Cape && it !is ThunderNotifier && it !is HackerDetector && it !is Wings && it !is SilentView && it !is Hud && it !is NoInvClose && it !is HudEditor && it !is Gui && it !is Animations }
            .forEach {
                if (values)
                    it.values.forEach { value ->
                        stringBuilder.append("${it.name} ${value.name} ${value.get()}").append("\n")
                    }

                if (states)
                    stringBuilder.append("${it.name} toggle ${it.state}").append("\n")

                if (binds)
                    stringBuilder.append("${it.name} bind ${Keyboard.getKeyName(it.keyBind)}").append("\n")
            }

        return stringBuilder.toString()
    }
}