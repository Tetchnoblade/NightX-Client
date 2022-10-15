package net.ccbluex.liquidbounce.features.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.client.*
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.cool.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    var shouldNotify: Boolean = false
    var toggleSoundMode = 0
    var toggleVolume = 0F

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("§c>>§f Loading modules...")

        registerModules(
            Patcher::class.java,
            AutoSword::class.java,
            BowAimbot::class.java,
            Aimbot::class.java,
            AutoBow::class.java,
            AutoSoup::class.java,
            FastBow::class.java,
            Criticals::class.java,
            KillAura::class.java,
            Velocity::class.java,
            Flight::class.java,
            HighJump::class.java,
            Inventory::class.java,
            NoSlow::class.java,
            Jesus::class.java,
            Strafe::class.java,
            Sprint::class.java,
            Teams::class.java,
            NoRotate::class.java,
            AntiBot::class.java,
            Stealer::class.java,
            Scaffold::class.java,
            FastBreak::class.java,
            FastPlace::class.java,
            ESP::class.java,
            Sneak::class.java,
            Speed::class.java,
            Tracers::class.java,
            NameTags::class.java,
            FastEat::class.java,
            Brightness::class.java,
            ItemESP::class.java,
            ChestESP::class.java,
            Bypass::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            NoWeb::class.java,
            NoBob::class.java,
            Spammer::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            Nick::class.java,
            NoHurt::class.java,
            XRay::class.java,
            Timer::class.java,
            Freecam::class.java,
            HitBox::class.java,
            PluginsChecker::class.java,
            LongJump::class.java,
            AutoClicker::class.java,
            BlockOverlay::class.java,
            NoFriends::class.java,
            Chams::class.java,
            Clip::class.java,
            Phase::class.java,
            Crasher::class.java,
            Fov::class.java,
            BlockAnimations::class.java,
            InventoryManager::class.java,
            ShowInvis::class.java,
            AntiBlind::class.java,
            Trails::class.java,
            KeepBreaking::class.java,
            ViewClip::class.java,
            Water::class.java,
            Reach::class.java,
            HUD::class.java,
            PackSpoofer::class.java,
            NoSlowBreak::class.java,
            PortalMenu::class.java,
            TimeChanger::class.java,
            EnchantColor::class.java,
            Cape::class.java,
            NoRender::class.java,
            DamageParticle::class.java,
            AntiVanish::class.java,
            Skeletal::class.java,
            AutoLogin::class.java,
            AuthBypass::class.java,
            Gapple::class.java,
            ColorMixer::class.java,
            Disabler::class.java,
            CustomDisabler::class.java,
            Crosshair::class.java,
            Rotate::class.java,
            AntiFall::class.java,
            AutoHypixel::class.java,
            TargetESP::class.java,
            TwoDTags::class.java,
            BanStats::class.java,
            AntiFireBall::class.java,
            KeepSprint::class.java,
            LookTP::class.java,
            Teleport::class.java,
            Sensor::class.java,
            BowLongJump::class.java,
            ConsoleSpammer::class.java,
            PointerESP::class.java,
            SafeWalk::class.java,
            NoAchievements::class.java,
            NoMouseInteract::class.java,
            AntiHunger::class.java,
            AirJump::class.java,
            AntiCactus::class.java,
            FastBridge::class.java,
            FastLadder::class.java,
            Parkour::class.java,
            Spider::class.java,
            FakeLag::class.java,
            PacketFixer::class.java,
            AutoPlay::class.java,
            AntiBan::class.java,
            NoInvClose::class.java,
            TPAura::class.java,
            AutoHeal::class.java,
            AntiAFK::class.java,
            AutoFish::class.java,
            Damage::class.java,
            Ghost::class.java,
            KeepContainer::class.java,
            AutoWalk::class.java,
            BlockWalk::class.java,
            IceSpeed::class.java,
            HorseJump::class.java,
            LiquidInteract::class.java,
            AutoBreak::class.java,
            CivBreak::class.java,
            Nuker::class.java,
            SuperheroFX::class.java,
            Gui::class.java,
            ResetVL::class.java,
            SpeedMine::class.java,
            Annoy::class.java,
            TPReach::class.java,
            Ridergod::class.java,
            HudEditor::class.java,
            ThunderNotifier::class.java,
            NoClip::class.java,
            AntiObsidian::class.java,
            TargetMobs::class.java,
            TargetPlayers::class.java,
            TargetAnimals::class.java,
            TargetInvisible::class.java,
            AutoArmor::class.java,
            Projectiles::class.java,
            RealBobbing::class.java
        )

        registerModule(Fucker)
        registerModule(StealAura)

        ClientUtils.getLogger().info("§c>>§f Successfully loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        module.onInitialize()
        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger()
                .error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun <T : Module> getModule(moduleClass: Class<T>): T? = moduleClassMap[moduleClass] as T?

    operator fun <T : Module> get(clazz: Class<T>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
