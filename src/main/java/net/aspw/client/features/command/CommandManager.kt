package net.aspw.client.features.command

import net.aspw.client.Client
import net.aspw.client.features.command.impl.*
import net.aspw.client.util.ClientUtils

class CommandManager {
    val commands = mutableListOf<Command>()

    /**
     * Register all default commands
     */
    fun registerCommands() {
        registerCommand(BindCommand())
        registerCommand(VClipCommand())
        registerCommand(SayCommand())
        registerCommand(MacroCommand())
        registerCommand(FriendCommand())
        registerCommand(ConfigCommand())
        registerCommand(ToggleCommand())
        registerCommand(BindsCommand())
        registerCommand(PingCommand())
        registerCommand(ReloadCommand())
        registerCommand(HideCommand())
        registerCommand(TeleportCommand())
        registerCommand(EnchantCommand())
        registerCommand(GiveCommand())
        registerCommand(DamageCommand())
        registerCommand(RemoteViewCommand())
        registerCommand(RenameCommand())
        registerCommand(IgnCommand())
        registerCommand(ThemeCommand())
        registerCommand(LoginCommand())
        registerCommand(ClipCommand())
        registerCommand(RegisterCommand())
        registerCommand(RepeatCommand())
        registerCommand(MagicTrickCommand())
        registerCommand(PluginsCommand())
        registerCommand(SkinStealerCommand())
        registerCommand(HelpCommand())
    }

    /**
     * Execute command by given [input]
     */
    fun executeCommands(input: String) {
        for (command in commands) {
            val args = input.split(" ").toTypedArray()

            if (args[0].equals("." + command.command, ignoreCase = true)) {
                command.execute(args)
                return
            }

            for (alias in command.alias) {
                if (!args[0].equals(".$alias", ignoreCase = true))
                    continue

                command.execute(args)
                return
            }
        }

        ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§cUnknown command. Try .help for a list of commands.")
    }

    /**
     * Register [command] by just adding it to the commands registry
     */
    fun registerCommand(command: Command) = commands.add(command)

    /**
     * Unregister [command] by just removing it from the commands registry
     */
    fun unregisterCommand(command: Command?) = commands.remove(command)
}
