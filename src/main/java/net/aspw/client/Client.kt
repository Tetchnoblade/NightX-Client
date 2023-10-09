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
import net.aspw.client.util.misc.sound.TipSoundManager
import net.aspw.client.util.network.CheckConnection
import net.aspw.client.value.ListValue
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.HUD
import net.aspw.client.visual.hud.HUD.Companion.createDefault
import net.minecraft.util.ResourceLocation
import kotlin.concurrent.thread

object Client {

    // Client information
    val clientVersion = ListValue("ClientVersion", arrayOf("Release", "Beta", "Developer"), "Developer")
    const val CLIENT_BEST = "NightX"
    const val CLIENT_FOLDER = "NightX-Reloaded"
    const val CLIENT_VERSION = "Developer B70"
    const val CLIENT_CREATOR = "As_pw, outaokura"
    const val CLIENT_WEBSITE = "https://aspw-w.github.io/NightX-Web"
    const val CLIENT_CONFIG = "$CLIENT_WEBSITE/data/configs.txt"
    const val CLIENT_SRG = "$CLIENT_WEBSITE/data/srg.txt"
    const val CLIENT_ANNOUNCEMENT = "$CLIENT_WEBSITE/data/announcement.txt"
    const val CLIENT_CONTRIBUTORS = "$CLIENT_WEBSITE/data/contributors.json"
    const val CLIENT_INFORMATION = "https://api.github.com/repos/Aspw-w/NightX-Client/stats/contributors"
    const val CLIENT_CHAT = "§c§l>> §r"
    val CLIENT_STATUS = when (clientVersion.get()) {
        "Release" -> "$CLIENT_WEBSITE/data/release.txt"
        "Beta" -> "$CLIENT_WEBSITE/data/beta.txt"
        "Developer" -> "$CLIENT_WEBSITE/data/dev.txt"
        else -> null
    }
    val CLIENT_LATEST = when (clientVersion.get()) {
        "Release" -> "$CLIENT_WEBSITE/data/release-latest.txt"
        "Beta" -> "$CLIENT_WEBSITE/data/beta-latest.txt"
        "Developer" -> "$CLIENT_WEBSITE/data/dev-latest.txt"
        else -> null
    }
    // Old Auth System
    // const val CLIENT_USER = "Username:Password:HWID:UID"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager
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
        CheckConnection.getAnnouncement()
        CheckConnection.getContributors()
        CheckConnection.getRealContributors()

        // Get srg file
        CheckConnection.getSRG()

        // Old Auth System
        // Check HWID
        //getCurrentHWID()

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()
        combatManager = CombatManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(PacketManager())
        eventManager.registerListener(InventoryUtils())
        eventManager.registerListener(InventoryHelper)
        eventManager.registerListener(PacketUtils())
        eventManager.registerListener(SessionUtils())
        eventManager.registerListener(MacroManager)
        eventManager.registerListener(combatManager)

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
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig
        )

        clickGui = ClickGui()

        // Creative items
        if (hasForge()) {
            ModItems()
            StackItems()
            EnchantItems()
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Setup Discord RPC
        if (discordRPC.showRichPresenceValue) {
            thread {
                try {
                    discordRPC.setup()
                } catch (throwable: Throwable) {
                    ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
                }
            }
        }

        ClientUtils.getLogger().info("Loaded!")

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