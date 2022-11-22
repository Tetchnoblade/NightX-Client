package net.aspw.nightx

import net.aspw.nightx.discord.ClientRichPresence
import net.aspw.nightx.event.ClientShutdownEvent
import net.aspw.nightx.event.EventManager
import net.aspw.nightx.features.command.CommandManager
import net.aspw.nightx.features.module.ModuleManager
import net.aspw.nightx.features.special.AntiForge
import net.aspw.nightx.features.special.BungeeCordSpoof
import net.aspw.nightx.features.special.MacroManager
import net.aspw.nightx.file.FileManager
import net.aspw.nightx.script.ScriptManager
import net.aspw.nightx.script.remapper.Remapper.loadSrg
import net.aspw.nightx.tabs.BlocksTab
import net.aspw.nightx.tabs.ExploitsTab
import net.aspw.nightx.ui.client.hud.HUD
import net.aspw.nightx.ui.client.hud.HUD.Companion.createDefault
import net.aspw.nightx.ui.font.Fonts
import net.aspw.nightx.utils.*
import net.aspw.nightx.utils.ClassUtils.hasForge
import net.aspw.nightx.utils.misc.sound.TipSoundManager
import net.minecraft.util.ResourceLocation
import kotlin.concurrent.thread

object NightX {

    // Client information
    const val CLIENT_BEST = "NightX"
    const val CLIENT_FOLDER = ".nightx"
    const val CLIENT_VERSION = "Development"
    const val CLIENT_CREATOR = "CCBlueX, exit-scammed, As_pw, Zywl"
    const val CLIENT_CONFIGS = "https://sites.google.com/view/nightx-client"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager

    lateinit var tipSoundManager: TipSoundManager

    // HUD & ClickGUI
    lateinit var hud: HUD

    // Menu Background
    var background: ResourceLocation? = null

    // Discord RPC
    lateinit var clientRichPresence: ClientRichPresence

    var lastTick: Long = 0L

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
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
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

        // Remapper
        try {
            loadSrg()

            // ScriptManager
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

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
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