package net.aspw.client

import net.aspw.client.config.FileManager
import net.aspw.client.event.ClientShutdownEvent
import net.aspw.client.event.EventManager
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.ModuleManager
import net.aspw.client.features.special.ClientSpoof
import net.aspw.client.features.special.MacroManager
import net.aspw.client.features.special.ModItems
import net.aspw.client.features.special.StackItems
import net.aspw.client.features.special.script.ScriptManager
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
    const val CLIENT_FOLDER = "Night-X"
    const val CLIENT_VERSION = "Developer"
    const val CLIENT_CREATOR = "CCBlueX, Exit-scammed, As_pw, Zywl"
    const val CLIENT_DISCORD = "https://nightx.api-minecraft.net/s/rgul30dkqw"
    const val CLIENT_FONTS = "https://nightx.api-minecraft.net/s/pe6o0ytkot"
    const val CLIENT_CHAT = "§c§l>> "

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
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

        try {
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.enemysConfig, fileManager.xrayConfig
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