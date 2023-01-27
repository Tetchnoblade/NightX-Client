package net.aspw.nightx

import net.aspw.nightx.config.FileManager
import net.aspw.nightx.discordrpc.ClientRichPresence
import net.aspw.nightx.event.ClientShutdownEvent
import net.aspw.nightx.event.EventManager
import net.aspw.nightx.features.command.CommandManager
import net.aspw.nightx.features.module.ModuleManager
import net.aspw.nightx.features.special.ClientSpoof
import net.aspw.nightx.features.special.MacroManager
import net.aspw.nightx.features.special.ModItems
import net.aspw.nightx.features.special.StackItems
import net.aspw.nightx.features.special.script.ScriptManager
import net.aspw.nightx.utils.*
import net.aspw.nightx.utils.ClassUtils.hasForge
import net.aspw.nightx.utils.misc.sound.TipSoundManager
import net.aspw.nightx.visual.font.Fonts
import net.aspw.nightx.visual.hud.HUD
import net.aspw.nightx.visual.hud.HUD.Companion.createDefault
import net.minecraft.util.ResourceLocation
import kotlin.concurrent.thread

object NightX {

    // Client information
    const val CLIENT_BEST = "NightX"
    const val CLIENT_COLORED = "§lN§fightX"
    const val CLIENT_FOLDER = ".nightx"
    const val CLIENT_VERSION = "Developer"
    const val CLIENT_CREATOR = "CCBlueX, Exit-scammed, As_pw, Zywl"
    const val CLIENT_CONFIGS = "https://nightx.api-minecraft.net/s/zf45j6uzog"
    const val CLIENT_SCRIPTS = "https://nightx.api-minecraft.net/s/pqbhqshhqz"
    const val CLIENT_DISCORD = "https://nightx.api-minecraft.net/s/rgul30dkqw"
    const val CLIENT_FONTS = "https://nightx.api-minecraft.net/s/icwf3t8op4"
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

    // Discord RPC
    lateinit var clientRichPresence: ClientRichPresence

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

        // Init Discord RPC
        clientRichPresence = ClientRichPresence()

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

        // Setup Discord RPC
        if (clientRichPresence.showRichPresenceValue) {
            thread {
                try {
                    clientRichPresence.setup()
                } catch (throwable: Throwable) {
                    ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
                }
            }
        }

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

        // Shutdown discord rpc
        clientRichPresence.shutdown()
    }

}