package net.aspw.client.features.command.impl

import net.aspw.client.features.command.Command

class HelpCommand : Command("help", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        try {
            chat("§c§lHelp")
            chat("§a------------")
            chat(".bind <module> <key> / .bind <module> none")
            chat("§a------------")
            chat(".binds / .binds clear")
            chat("§a------------")
            chat(".clip <value>")
            chat("§a------------")
            chat(".(config, c) <load/save/list/delete/fix/folder>")
            chat("§a------------")
            chat(".enchant <type> [level]")
            chat("§a------------")
            chat(".friend <add/remove/list/clear>")
            chat("§a------------")
            chat(".(give, item, i, get) <item> [amount] [data] [datatag]")
            chat("§a------------")
            chat(".hide <module/list/clear/reset/category>")
            chat("§a------------")
            chat(".(damage, dmg) / .(damage, dmg) <size>")
            chat("§a------------")
            chat(".ign")
            chat("§a------------")
            chat(".(register, r)")
            chat("§a------------")
            chat(".(login, l)")
            chat("§a------------")
            chat(".macro <list/clear/add/remove>")
            chat("§a------------")
            chat(".(magictrick, mt)")
            chat("§a------------")
            chat(".ping")
            chat("§a------------")
            chat(".(plugins, pl)")
            chat("§a------------")
            chat(".reload")
            chat("§a------------")
            chat(".(remoteview, rv) <username>")
            chat("§a------------")
            chat(".rename <name>")
            chat("§a------------")
            chat(".(repeat, rp) <amount> <message>")
            chat("§a------------")
            chat(".say <message>")
            chat("§a------------")
            chat(".(skinstealer, steal) <id>")
            chat("§a------------")
            chat(".(teleport, tp) <player name/x y z>")
            chat("§a------------")
            chat(".theme <load/save/list/delete>")
            chat("§a------------")
            chat(".(toggle, t) <module> [on/off]")
            chat("§a------------")
            chat(".vclip <value>")
            chat("§a------------")
            return
        } catch (ex: NumberFormatException) {
            chatSyntaxError()
        }

        return
    }
}
