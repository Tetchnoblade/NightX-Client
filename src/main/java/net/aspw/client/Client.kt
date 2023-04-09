package net.aspw.client

import net.aspw.client.config.FileManager
import net.aspw.client.event.ClientShutdownEvent
import net.aspw.client.event.EventManager
import net.aspw.client.features.api.ClientSpoof
import net.aspw.client.features.api.MacroManager
import net.aspw.client.features.api.ModItems
import net.aspw.client.features.api.StackItems
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.ModuleManager
import net.aspw.client.utils.*
import net.aspw.client.utils.ClassUtils.hasForge
import net.aspw.client.utils.misc.sound.TipSoundManager
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.HUD
import net.aspw.client.visual.hud.HUD.Companion.createDefault
import net.minecraft.util.ResourceLocation

object Client {

    // Client information
    const val CLIENT_BEST = "NightX"
    const val CLIENT_COLORED = "§lN§fightX"
    const val CLIENT_FOLDER = "NightX"
    const val CLIENT_VERSION = "Beta B47"
    const val CLIENT_CREATOR = "CCBlueX, Exit-scammed, As_pw, Zywl, potatolvgist"
    const val CLIENT_CONFIGS = "https://nightx.api-minecraft.net/s/zf45j6uzog"
    const val CLIENT_DISCORD = "https://nightx.api-minecraft.net/s/rgul30dkqw"
    const val CLIENT_YOUTUBE = "https://nightx.api-minecraft.net/s/xg3l0nmlo3"
    const val CLIENT_GITHUB = "https://nightx.api-minecraft.net/s/qxb8132mu9"
    const val CLIENT_FONTS = "https://nightx.api-minecraft.net/s/pe6o0ytkot"
    const val CLIENT_CHAT = "§c§l>> "

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var tipSoundManager: TipSoundManager

    // Hud
    lateinit var hud: HUD

    // Menu Background
    var background: ResourceLocation? = null

    private var lastTick: Long = 0L

    var playTimeStart: Long = 0

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Authenticating...")
        lastTick = System.currentTimeMillis()

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(ClientSpoof())
        eventManager.registerListener(InventoryUtils())
        eventManager.registerListener(InventoryHelper)
        eventManager.registerListener(PacketUtils())
        eventManager.registerListener(SessionUtils())
        eventManager.registerListener(MacroManager)

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Init SoundManager
        tipSoundManager = TipSoundManager()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig
        )

        // Creative items
        if (hasForge()) {
            ModItems()
            StackItems()
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        ClientUtils.getLogger().info("Successfully loaded NightX in ${System.currentTimeMillis() - lastTick}ms.")

        // Set is starting status
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
    }

}