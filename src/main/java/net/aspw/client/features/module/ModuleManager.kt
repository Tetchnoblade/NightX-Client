package net.aspw.client.features.module

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.KeyEvent
import net.aspw.client.event.Listenable
import net.aspw.client.features.command.impl.ModuleCommand
import net.aspw.client.features.module.impl.combat.AntiFireBall
import net.aspw.client.features.module.impl.combat.AntiVelocity
import net.aspw.client.features.module.impl.combat.AutoClicker
import net.aspw.client.features.module.impl.combat.AutoGapple
import net.aspw.client.features.module.impl.combat.AutoHeal
import net.aspw.client.features.module.impl.combat.AutoProjectile
import net.aspw.client.features.module.impl.combat.BackTrack
import net.aspw.client.features.module.impl.combat.BowAura
import net.aspw.client.features.module.impl.combat.Criticals
import net.aspw.client.features.module.impl.combat.FastBow
import net.aspw.client.features.module.impl.combat.HitBox
import net.aspw.client.features.module.impl.combat.KeepSprint
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.combat.KillAuraRecode
import net.aspw.client.features.module.impl.combat.TPAura
import net.aspw.client.features.module.impl.combat.TriggerBot
import net.aspw.client.features.module.impl.combat.WTap
import net.aspw.client.features.module.impl.exploit.ConsoleSpammer
import net.aspw.client.features.module.impl.exploit.Crasher
import net.aspw.client.features.module.impl.exploit.Disabler
import net.aspw.client.features.module.impl.exploit.ExtendedPosition
import net.aspw.client.features.module.impl.exploit.FakeLag
import net.aspw.client.features.module.impl.exploit.FireReducer
import net.aspw.client.features.module.impl.exploit.GhostMode
import net.aspw.client.features.module.impl.exploit.InfiniteDurability
import net.aspw.client.features.module.impl.exploit.LiquidInteract
import net.aspw.client.features.module.impl.exploit.NoBlockPush
import net.aspw.client.features.module.impl.exploit.NoMouseIntersect
import net.aspw.client.features.module.impl.exploit.PingSpoofer
import net.aspw.client.features.module.impl.exploit.Plugins
import net.aspw.client.features.module.impl.exploit.PortalMenu
import net.aspw.client.features.module.impl.exploit.Regen
import net.aspw.client.features.module.impl.movement.AntiAFK
import net.aspw.client.features.module.impl.movement.AntiVoid
import net.aspw.client.features.module.impl.movement.AutoParkour
import net.aspw.client.features.module.impl.movement.FastLadder
import net.aspw.client.features.module.impl.movement.Flight
import net.aspw.client.features.module.impl.movement.InvMove
import net.aspw.client.features.module.impl.movement.Jesus
import net.aspw.client.features.module.impl.movement.LongJump
import net.aspw.client.features.module.impl.movement.NoFall
import net.aspw.client.features.module.impl.movement.NoJumpDelay
import net.aspw.client.features.module.impl.movement.NoSlow
import net.aspw.client.features.module.impl.movement.PerfectHorseJump
import net.aspw.client.features.module.impl.movement.Protect
import net.aspw.client.features.module.impl.movement.SafeWalk
import net.aspw.client.features.module.impl.movement.SilentSneak
import net.aspw.client.features.module.impl.movement.Speed
import net.aspw.client.features.module.impl.movement.Sprint
import net.aspw.client.features.module.impl.movement.VehicleJump
import net.aspw.client.features.module.impl.other.Annoy
import net.aspw.client.features.module.impl.other.AntiFrozen
import net.aspw.client.features.module.impl.other.AntiSuffocation
import net.aspw.client.features.module.impl.other.AutoAuth
import net.aspw.client.features.module.impl.other.BrandSpoofer
import net.aspw.client.features.module.impl.other.DiscordRPC
import net.aspw.client.features.module.impl.other.DrinkingAlert
import net.aspw.client.features.module.impl.other.EnchantColor
import net.aspw.client.features.module.impl.other.FastMine
import net.aspw.client.features.module.impl.other.FastPlace
import net.aspw.client.features.module.impl.other.GamePlay
import net.aspw.client.features.module.impl.other.InfiniteReach
import net.aspw.client.features.module.impl.other.LookTP
import net.aspw.client.features.module.impl.other.MurdererDetector
import net.aspw.client.features.module.impl.other.PackSpoofer
import net.aspw.client.features.module.impl.other.SnakeGame
import net.aspw.client.features.module.impl.other.StaffProtection
import net.aspw.client.features.module.impl.other.ThunderNotifier
import net.aspw.client.features.module.impl.other.Tweaks
import net.aspw.client.features.module.impl.other.WorldTime
import net.aspw.client.features.module.impl.player.AutoFish
import net.aspw.client.features.module.impl.player.AutoRespawn
import net.aspw.client.features.module.impl.player.AutoTool
import net.aspw.client.features.module.impl.player.Blink
import net.aspw.client.features.module.impl.player.BowJump
import net.aspw.client.features.module.impl.player.Breaker
import net.aspw.client.features.module.impl.player.CivBreak
import net.aspw.client.features.module.impl.player.FastEat
import net.aspw.client.features.module.impl.player.Freecam
import net.aspw.client.features.module.impl.player.HighJump
import net.aspw.client.features.module.impl.player.InvManager
import net.aspw.client.features.module.impl.player.LegitScaffold
import net.aspw.client.features.module.impl.player.Nuker
import net.aspw.client.features.module.impl.player.Phase
import net.aspw.client.features.module.impl.player.Scaffold
import net.aspw.client.features.module.impl.player.Spider
import net.aspw.client.features.module.impl.player.Stealer
import net.aspw.client.features.module.impl.player.Step
import net.aspw.client.features.module.impl.player.TargetStrafe
import net.aspw.client.features.module.impl.player.Timer
import net.aspw.client.features.module.impl.targets.Animals
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.features.module.impl.targets.AntiTeams
import net.aspw.client.features.module.impl.targets.Dead
import net.aspw.client.features.module.impl.targets.Invisible
import net.aspw.client.features.module.impl.targets.Mobs
import net.aspw.client.features.module.impl.targets.Players
import net.aspw.client.features.module.impl.visual.Animations
import net.aspw.client.features.module.impl.visual.BetterView
import net.aspw.client.features.module.impl.visual.Cape
import net.aspw.client.features.module.impl.visual.CustomModel
import net.aspw.client.features.module.impl.visual.ESP
import net.aspw.client.features.module.impl.visual.FullBright
import net.aspw.client.features.module.impl.visual.Gui
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.features.module.impl.visual.ItemPhysics
import net.aspw.client.features.module.impl.visual.MoreParticles
import net.aspw.client.features.module.impl.visual.MotionBlur
import net.aspw.client.features.module.impl.visual.NoHurtCam
import net.aspw.client.features.module.impl.visual.StreamerMode
import net.aspw.client.features.module.impl.visual.Tracers
import net.aspw.client.features.module.impl.visual.Trajectories
import net.aspw.client.features.module.impl.visual.VisualAbilities
import net.aspw.client.features.module.impl.visual.XRay
import net.aspw.client.utils.ClientUtils
import java.util.TreeSet

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
        ClientUtils.getLogger().info("Loading modules...")

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
            MoreParticles::class.java,
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
            BetterView::class.java,
            FireReducer::class.java,
            NoBlockPush::class.java,
            KillAuraRecode::class.java,
            NoHurtCam::class.java,
            DrinkingAlert::class.java
        )

        ClientUtils.getLogger().info("Successfully loaded ${modules.size} modules.")
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
