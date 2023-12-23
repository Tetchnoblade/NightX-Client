package net.aspw.client

import net.aspw.client.config.FileManager
import net.aspw.client.event.ClientShutdownEvent
import net.aspw.client.event.EventManager
import net.aspw.client.features.api.*
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.ModuleManager
import net.aspw.client.features.module.impl.other.HackerDetect
import net.aspw.client.features.module.impl.other.ThunderNotifier
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.features.module.impl.visual.MoreParticles
import net.aspw.client.features.module.impl.visual.SilentView
import net.aspw.client.features.module.impl.visual.Trajectories
import net.aspw.client.util.*
import net.aspw.client.util.ClassUtils.hasForge
import net.aspw.client.util.misc.sound.TipSoundManager
import net.aspw.client.util.network.Access
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.hud.HUD
import net.aspw.client.visual.hud.HUD.Companion.createDefault
import net.minecraft.util.ResourceLocation
import kotlin.concurrent.thread

object Client {

    // Client information
    private const val isInDev = true

    const val CLIENT_BEST = "NightX"
    const val CLIENT_FOLDER = "NightWare"
    const val CLIENT_VERSION = "Beta B77"
    const val CLIENT_CREATOR = "As_pw"
    const val CLIENT_WEBSITE = "https://aspw-w.github.io/NightX-Web"
    const val CLIENT_ANNOUNCEMENT = "$CLIENT_WEBSITE/database/announcement.txt"
    const val CLIENT_CHAT = "§b§l>> §r"
    const val CLIENT_STATUS = "$CLIENT_WEBSITE/database/data.txt"
    const val CLIENT_CONFIGLIST = "$CLIENT_WEBSITE/configs/string/list.txt"
    const val CLIENT_CONFIGS = "$CLIENT_WEBSITE/configs/"
    val CLIENT_LATEST =
        if (!isInDev) "$CLIENT_WEBSITE/database/latestversion.txt" else "$CLIENT_WEBSITE/database/betaversion.txt"
    //const val CLIENT_USERS = "$CLIENT_WEBSITE/users/list.txt"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager

    // Hud
    lateinit var hud: HUD

    lateinit var clickGui: ClickGui

    // Menu Background
    var background: ResourceLocation? = null

    private var lastTick: Long = 0L

    var playTimeStart: Long = 0

    // Discord RPC
    private lateinit var discordRPC: DiscordRPC

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Authenticating...")
        lastTick = System.currentTimeMillis()

        // Check update
        Access.checkStatus()
        Access.checkLatestVersion()
        Access.getAnnouncement()
        Access.checkStaffList()

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

        // Setup default states
        if (!fileManager.modulesConfig.hasConfig() || !fileManager.valuesConfig.hasConfig()) {
            ClientUtils.getLogger().info("Setting up default modules...")
            moduleManager.getModule(Interface::class.java)?.state = true
            moduleManager.getModule(SilentView::class.java)?.state = true
            moduleManager.getModule(ThunderNotifier::class.java)?.state = true
            moduleManager.getModule(Trajectories::class.java)?.state = true
            moduleManager.getModule(MoreParticles::class.java)?.state = true
            moduleManager.getModule(HackerDetect::class.java)?.state = true
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