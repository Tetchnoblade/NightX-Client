package net.aspw.client

import net.aspw.client.config.FileManager
import net.aspw.client.event.ClientShutdownEvent
import net.aspw.client.event.EventManager
import net.aspw.client.features.api.CombatManager
import net.aspw.client.features.api.DiscordRPC
import net.aspw.client.features.api.MacroManager
import net.aspw.client.features.api.PacketManager
import net.aspw.client.features.api.inventory.EnchantItems
import net.aspw.client.features.api.inventory.ModItems
import net.aspw.client.features.api.inventory.StackItems
import net.aspw.client.features.command.CommandManager
import net.aspw.client.features.module.ModuleManager
import net.aspw.client.features.module.impl.other.BrandSpoofer
import net.aspw.client.features.module.impl.other.ThunderNotifier
import net.aspw.client.features.module.impl.visual.*
import net.aspw.client.protocol.ProtocolBase
import net.aspw.client.protocol.ProtocolMod
import net.aspw.client.utils.*
import net.aspw.client.utils.ClassUtils.hasForge
import net.aspw.client.utils.misc.sound.TipSoundManager
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.font.semi.Fonts
import net.minecraft.util.ResourceLocation

object Launch {

    // Client information
    const val CLIENT_BEST = "NightX"
    const val CLIENT_FOLDER = "NightX-Client"
    const val CLIENT_VERSION = "B96"
    const val CLIENT_PROTOCOL_RANGE = "1.8 to 24w09a"
    const val CLIENT_CREATOR = "As_pw"
    const val CLIENT_WEBSITE = "https://aspw-w.github.io/AspieAPI/NightX"
    const val CLIENT_CHAT = "§c$CLIENT_BEST: §r"
    const val CLIENT_STATUS = "$CLIENT_WEBSITE/database/data.txt"
    const val CLIENT_CONFIGLIST = "$CLIENT_WEBSITE/configs/str/list.txt"
    const val CLIENT_CONFIGS = "$CLIENT_WEBSITE/configs/"
    const val CLIENT_LATEST = "$CLIENT_WEBSITE/database/version.txt"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager

    lateinit var clickGui: ClickGui

    // Menu Background
    var background: ResourceLocation? = null

    private var lastTick: Long = 0L

    private val allowJavaRange = 181..Int.MAX_VALUE
    private var javaVersion = System.getProperty("java.version")
    var useAltManager = javaVersion in "1.8.0_$allowJavaRange" && MinecraftInstance.mc.isJava64bit

    // Discord RPC
    lateinit var discordRPC: DiscordRPC

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Launching...")
        lastTick = System.currentTimeMillis()

        ProtocolBase.init(ProtocolMod.PLATFORM)

        // Check update
        Access.checkStatus()
        Access.checkLatestVersion()
        Access.checkStaffList()

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

        // Setup default states on first launch
        if (!fileManager.modulesConfig.hasConfig() || !fileManager.valuesConfig.hasConfig()) {
            ClientUtils.getLogger().info("Setting up default modules...")
            moduleManager.getModule(Interface::class.java)?.state = true
            moduleManager.getModule(BetterView::class.java)?.state = true
            moduleManager.getModule(BrandSpoofer::class.java)?.state = true
            moduleManager.getModule(net.aspw.client.features.module.impl.other.DiscordRPC::class.java)?.state = true
            moduleManager.getModule(ItemPhysics::class.java)?.state = true
            moduleManager.getModule(ThunderNotifier::class.java)?.state = true
            moduleManager.getModule(Trajectories::class.java)?.state = true
            moduleManager.getModule(MoreParticles::class.java)?.state = true
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