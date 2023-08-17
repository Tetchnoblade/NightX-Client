package net.aspw.client.features.module

import net.aspw.client.Client
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
import net.aspw.client.features.module.impl.beta.TestModule
import net.aspw.client.features.module.impl.targets.*
import net.aspw.client.features.module.impl.visual.*
import net.aspw.client.util.ClientUtils
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
        Client.eventManager.registerListener(this)
    }

    fun registerBetaModules() {
        ClientUtils.getLogger().info("Loading beta modules...")

        registerModules(
            TestModule::class.java
        )
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("Loading modules...")

        registerModules(
            Jenisuke::class.java,
            Protect::class.java,
            KeepChest::class.java,
            BowAura::class.java,
            AimAssist::class.java,
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
            NoViewReset::class.java,
            AntiBots::class.java,
            ChestStealer::class.java,
            Scaffold::class.java,
            FastPlace::class.java,
            SilentSneak::class.java,
            Speed::class.java,
            Tracers::class.java,
            FastEat::class.java,
            FullBright::class.java,
            ItemESP::class.java,
            ChestESP::class.java,
            PingSpoofer::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            Spammer::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            NameProtect::class.java,
            Timer::class.java,
            Freecam::class.java,
            HitBox::class.java,
            Plugins::class.java,
            LongJump::class.java,
            AutoClicker::class.java,
            BlockOverlay::class.java,
            Phase::class.java,
            ServerCrasher::class.java,
            Animations::class.java,
            InvManager::class.java,
            AntiBlind::class.java,
            Trails::class.java,
            Reach::class.java,
            Hud::class.java,
            PackSpoofer::class.java,
            PortalMenu::class.java,
            WorldTime::class.java,
            EnchantColor::class.java,
            AutoAuth::class.java,
            AutoGapple::class.java,
            Disabler::class.java,
            Crosshair::class.java,
            AntiVoid::class.java,
            AntiFireBall::class.java,
            KeepSprint::class.java,
            ClickTP::class.java,
            ChinaHat::class.java,
            BowLongJump::class.java,
            PointerESP::class.java,
            SafeWalk::class.java,
            NoMouseIntersect::class.java,
            AntiHunger::class.java,
            AirJump::class.java,
            FastBridge::class.java,
            FastLadder::class.java,
            Parkour::class.java,
            Spider::class.java,
            FakeLag::class.java,
            GamePlay::class.java,
            AntiStaff::class.java,
            TPAura::class.java,
            AutoHeal::class.java,
            AntiAFK::class.java,
            AutoFish::class.java,
            GhostMode::class.java,
            HorseJump::class.java,
            LiquidInteract::class.java,
            Nuker::class.java,
            ResetVL::class.java,
            FastMine::class.java,
            Annoy::class.java,
            EntityDesync::class.java,
            ThunderNotifier::class.java,
            Mobs::class.java,
            Players::class.java,
            Animals::class.java,
            Invisible::class.java,
            AutoArmor::class.java,
            ViewBobbing::class.java,
            TargetStrafe::class.java,
            LagBack::class.java,
            Animals::class.java,
            Mobs::class.java,
            Players::class.java,
            ItemPhysics::class.java,
            Tweaks::class.java,
            Dead::class.java,
            Invisible::class.java,
            PotionSpoof::class.java,
            MoreParticles::class.java,
            CivBreak::class.java,
            PlayerEdit::class.java,
            ClientSpoof::class.java,
            KnockBackPlus::class.java,
            TriggerBot::class.java,
            FakePlayer::class.java,
            JumpCircle::class.java,
            AntiDesync::class.java,
            FreeLook::class.java,
            CameraNoClip::class.java,
            CustomModel::class.java,
            InfiniteDurability::class.java,
            EntityFlight::class.java,
            BedBreaker::class.java,
            ChestAura::class.java,
            BedWalker::class.java,
            Wings::class.java,
            SilentView::class.java,
            BackTrack::class.java,
            ChatFilter::class.java,
            AntiWaterPush::class.java,
            XRay::class.java,
            Cape::class.java,
            HudEditor::class.java,
            Gui::class.java,
            InfinitePitch::class.java,
            PacketDumper::class.java,
            HackerDetect::class.java,
            InfiniteChat::class.java,
            OptiFinePlus::class.java,
            EntityJump::class.java,
            ConsoleSpammer::class.java,
            ESP::class.java,
            GodBridge::class.java,
            AirPlace::class.java,
            AntiSuffocation::class.java,
            AttackFreeze::class.java,
            FakeGhostBlock::class.java
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
        Client.eventManager.registerListener(module)
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
        Client.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        Client.commandManager.registerCommand(ModuleCommand(module, values))
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
