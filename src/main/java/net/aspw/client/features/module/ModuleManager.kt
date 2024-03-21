package net.aspw.client.features.module

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.KeyEvent
import net.aspw.client.event.Listenable
import net.aspw.client.features.command.impl.ModuleCommand
import net.aspw.client.features.module.impl.combat.*
import net.aspw.client.features.module.impl.exploit.*
import net.aspw.client.features.module.impl.movement.*
import net.aspw.client.features.module.impl.other.*
import net.aspw.client.features.module.impl.player.*
import net.aspw.client.features.module.impl.player.Timer
import net.aspw.client.features.module.impl.targets.*
import net.aspw.client.features.module.impl.visual.*
import net.aspw.client.utils.ClientUtils
import java.util.*

class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    var shouldNotify: Boolean = false
    var toggleSoundMode = 0
    var toggleVolume = 0F
    var popSoundPower = 90F
    var swingSoundPower = 75F

    init {
        Launch.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        registerModules(
            Protect::class.java,
            BowAura::class.java,
            AutoProjectile::class.java,
            FastBow::class.java,
            Criticals::class.java,
            KillAura::class.java,
            AntiVelocity::class.java,
            Flight::class.java,
            HighJump::class.java,
            InvMove::class.java,
            NoSlow::class.java,
            Jesus::class.java,
            Sprint::class.java,
            AntiTeams::class.java,
            AntiBots::class.java,
            Stealer::class.java,
            Scaffold::class.java,
            FastPlace::class.java,
            SilentSneak::class.java,
            Speed::class.java,
            Tracers::class.java,
            FastEat::class.java,
            FullBright::class.java,
            PingSpoofer::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            StreamerMode::class.java,
            Timer::class.java,
            Freecam::class.java,
            HitBox::class.java,
            Plugins::class.java,
            LongJump::class.java,
            AutoClicker::class.java,
            Phase::class.java,
            Crasher::class.java,
            Animations::class.java,
            VisualAbilities::class.java,
            Interface::class.java,
            PackSpoofer::class.java,
            PortalMenu::class.java,
            WorldTime::class.java,
            EnchantColor::class.java,
            AutoAuth::class.java,
            AutoGapple::class.java,
            LegitScaffold::class.java,
            Disabler::class.java,
            AntiVoid::class.java,
            AntiFireBall::class.java,
            KeepSprint::class.java,
            LookTP::class.java,
            BowJump::class.java,
            SafeWalk::class.java,
            NoMouseIntersect::class.java,
            FastLadder::class.java,
            AutoParkour::class.java,
            Spider::class.java,
            FakeLag::class.java,
            GamePlay::class.java,
            StaffProtection::class.java,
            TPAura::class.java,
            AutoHeal::class.java,
            AntiAFK::class.java,
            AutoFish::class.java,
            GhostMode::class.java,
            PerfectHorseJump::class.java,
            LiquidInteract::class.java,
            Nuker::class.java,
            FastMine::class.java,
            Annoy::class.java,
            ThunderNotifier::class.java,
            Mobs::class.java,
            Players::class.java,
            Animals::class.java,
            Invisible::class.java,
            TargetStrafe::class.java,
            Animals::class.java,
            Mobs::class.java,
            Players::class.java,
            ItemPhysics::class.java,
            Tweaks::class.java,
            Dead::class.java,
            Invisible::class.java,
            CivBreak::class.java,
            BrandSpoofer::class.java,
            WTap::class.java,
            CustomModel::class.java,
            InfiniteDurability::class.java,
            Breaker::class.java,
            XRay::class.java,
            Cape::class.java,
            Gui::class.java,
            VehicleJump::class.java,
            ConsoleSpammer::class.java,
            AntiSuffocation::class.java,
            SnakeGame::class.java,
            ESP::class.java,
            ExtendedPosition::class.java,
            InfiniteReach::class.java,
            MotionBlur::class.java,
            AntiFrozen::class.java,
            Trajectories::class.java,
            WTap::class.java,
            BackTrack::class.java,
            TriggerBot::class.java,
            NoJumpDelay::class.java,
            InvManager::class.java,
            DiscordRPC::class.java,
            MurdererDetector::class.java,
            SilentRotations::class.java,
            FireReducer::class.java,
            NoBlockPush::class.java,
            KillAuraRecode::class.java,
            NoHurtCam::class.java,
            DrinkingAlert::class.java,
            AutoGetter::class.java
        )

        ClientUtils.getLogger().info("Successfully loaded modules")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        module.onInitialize()
        generateCommand(module)
        Launch.eventManager.registerListener(module)
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
        Launch.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        Launch.commandManager.registerCommand(ModuleCommand(module, values))
    }

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
