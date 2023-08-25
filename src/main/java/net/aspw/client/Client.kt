package net.aspw.client

import net.aspw.client.config.FileManager
import net.aspw.client.event.ClientShutdownEvent
import net.aspw.client.event.EventManager
import net.aspw.client.features.api.*
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.ModuleManager
import net.aspw.client.script.ScriptManager
import net.aspw.client.script.remapper.Remapper
import net.aspw.client.util.*
import net.aspw.client.util.ClassUtils.hasForge
import net.aspw.client.util.connection.CheckConnection
import net.aspw.client.util.misc.sound.TipSoundManager
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.HUD
import net.aspw.client.visual.hud.HUD.Companion.createDefault
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.Display
import kotlin.concurrent.thread

object Client {

    // Client information
    const val isBeta = true
    const val CLIENT_BEST = "NightX"
    const val CLIENT_FOLDER = "NightX-Reloaded"
    const val CLIENT_VERSION = "Beta B63 v3"
    const val CLIENT_CREATOR = "As_pw, outaokura"
    const val CLIENT_BASE = "https://nightx.skidded.host/s/"
    const val CLIENT_WEBSITE = "i4vvayd263"
    const val CLIENT_CONFIG = "c7iqnyfsn0"
    // Old Auth System
    // const val CLIENT_USER = "Username:Password:HWID:UID"
    const val CLIENT_SRG = "6bl91v1egh"
    const val CLIENT_CHAT = "§c§l>> §r"
    val CLIENT_STATUS =
        if (!isBeta) "y34fvk1y3d" else "yw4o9ber48"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var scriptManager: ScriptManager

    // Hud
    lateinit var hud: HUD

    lateinit var clickGui: ClickGui

    // Menu Background
    var background: ResourceLocation? = null

    private var lastTick: Long = 0L

    var playTimeStart: Long = 0

    // Discord RPC
    lateinit var discordRPC: DiscordRPC

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Authenticating...")
        lastTick = System.currentTimeMillis()

        // Check update
        CheckConnection.checkStatus()

        // Old Auth System
        // Check HWID
        //getCurrentHWID()

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

        // Remapper
        try {
            Remapper.loadSrg()

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
        if (CheckConnection.isLatest && CheckConnection.isAvailable) {
            fileManager.loadConfigs(
                fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
                fileManager.friendsConfig
            )
        }

        clickGui = ClickGui()

        // Creative items
        if (hasForge()) {
            ModItems()
            StackItems()
            EnchantItems()
        }

        // Set HUD
        if (CheckConnection.isLatest && CheckConnection.isAvailable) {
            hud = createDefault()
            fileManager.loadConfig(fileManager.hudConfig)
        }

        // Setup Discord RPC
        if (CheckConnection.isLatest && CheckConnection.isAvailable && discordRPC.showRichPresenceValue) {
            thread {
                try {
                    discordRPC.setup()
                } catch (throwable: Throwable) {
                    ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
                }
            }
        }

        ClientUtils.getLogger().info("Successfully loaded $CLIENT_BEST in ${System.currentTimeMillis() - lastTick}ms.")

        if (CheckConnection.isAvailable) {
            if (CheckConnection.isLatest)
                Display.setTitle("$CLIENT_BEST Client - $CLIENT_VERSION")
            else Display.setTitle("Outdated! Please Update on $CLIENT_BASE$CLIENT_WEBSITE (your current version is $CLIENT_VERSION)")
        } else {
            Display.setTitle("Temporary unavailable!")
        }

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