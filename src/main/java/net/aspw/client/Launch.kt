package net.aspw.client

import net.aspw.client.config.FileManager
import net.aspw.client.event.ClientShutdownEvent
import net.aspw.client.event.EventManager
import net.aspw.client.features.api.DiscordRPC
import net.aspw.client.features.api.MacroManager
import net.aspw.client.features.api.PacketManager
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.ModuleManager
import net.aspw.client.features.module.impl.other.BrandSpoofer
import net.aspw.client.features.module.impl.other.ThunderNotifier
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.features.module.impl.visual.SilentRotations
import net.aspw.client.features.module.impl.visual.TargetESP
import net.aspw.client.features.module.impl.visual.Trajectories
import net.aspw.client.utils.*
import net.aspw.client.utils.misc.sound.TipSoundManager
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.font.semi.Fonts

object Launch {

    // Client information
    const val CLIENT_BEST = "NightX"
    const val CLIENT_FOLDER = "NightX-Client"
    const val CLIENT_VERSION = "B121"
    const val CLIENT_CHAT = "§7[§5N§di§3g§bh§6t§aX§7] [§eInfo§7] §r"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var tipSoundManager: TipSoundManager

    lateinit var clickGui: ClickGui

    private var lastTick: Long = 0L

    private var javaVersion =
        System.getProperty("java.version").substring(6, minOf(9, System.getProperty("java.version").length))
            .toIntOrNull() ?: 0
    var useAltManager = javaVersion >= 181 && MinecraftInstance.mc.isJava64bit

    // Discord RPC
    lateinit var discordRPC: DiscordRPC

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Launching...")

        lastTick = System.currentTimeMillis()

        // Check update
        APIConnecter.checkStatus()
        APIConnecter.checkChangelogs()
        APIConnecter.checkBugs()
        APIConnecter.checkStaffList()

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(PacketManager())
        eventManager.registerListener(InventoryUtils())
        eventManager.registerListener(InventoryHelper)
        eventManager.registerListener(PacketUtils())
        eventManager.registerListener(SessionUtils())
        eventManager.registerListener(MacroManager)

        // Init Discord RPC
        discordRPC = DiscordRPC()

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Init SoundManager
        tipSoundManager = TipSoundManager()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Setup default states on first launch
        if (!fileManager.modulesConfig.hasConfig() || !fileManager.valuesConfig.hasConfig()) {
            ClientUtils.getLogger().info("Setting up default modules...")
            moduleManager.getModule(Interface::class.java)?.state = true
            moduleManager.getModule(SilentRotations::class.java)?.state = true
            moduleManager.getModule(BrandSpoofer::class.java)?.state = true
            moduleManager.getModule(TargetESP::class.java)?.state = true
            moduleManager.getModule(net.aspw.client.features.module.impl.other.DiscordRPC::class.java)?.state = true
            moduleManager.getModule(ThunderNotifier::class.java)?.state = true
            moduleManager.getModule(Trajectories::class.java)?.state = true
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig
        )

        clickGui = ClickGui()

        ClientUtils.getLogger().info("Launched!")

        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

        // Shutdown discord rpc
        discordRPC.shutdown()
    }

}