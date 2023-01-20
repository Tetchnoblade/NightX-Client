package net.aspw.nightx.features.module

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.KeyEvent
import net.aspw.nightx.event.Listenable
import net.aspw.nightx.features.module.modules.client.*
import net.aspw.nightx.features.module.modules.combat.*
import net.aspw.nightx.features.module.modules.exploit.*
import net.aspw.nightx.features.module.modules.misc.*
import net.aspw.nightx.features.module.modules.movement.*
import net.aspw.nightx.features.module.modules.player.*
import net.aspw.nightx.features.module.modules.render.*
import net.aspw.nightx.features.module.modules.targets.*
import net.aspw.nightx.features.module.modules.world.*
import net.aspw.nightx.features.module.modules.world.Timer
import net.aspw.nightx.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    var shouldNotify: Boolean = false
    var toggleSoundMode = 0
    var toggleVolume = 0F

    init {
        NightX.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("Loading modules...")

        registerModules(
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
            FastPlace::class.java,
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
            AntiWeb::class.java,
            NoBob::class.java,
            Spammer::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            StreamerMode::class.java,
            NoHurt::class.java,
            XRay::class.java,
            Timer::class.java,
            Freecam::class.java,
            Hitboxes::class.java,
            PluginsChecker::class.java,
            LongJump::class.java,
            AutoClicker::class.java,
            BlockOverlay::class.java,
            Chams::class.java,
            Phase::class.java,
            Crasher::class.java,
            Fov::class.java,
            Animations::class.java,
            InventoryManager::class.java,
            ShowInvis::class.java,
            AntiNausea::class.java,
            Trails::class.java,
            ViewClip::class.java,
            Reach::class.java,
            Hud::class.java,
            PackSpoofer::class.java,
            PortalMenu::class.java,
            WorldTime::class.java,
            EnchantColor::class.java,
            Cape::class.java,
            NoRender::class.java,
            DamageParticle::class.java,
            AntiVanish::class.java,
            AutoLogin::class.java,
            AuthBypass::class.java,
            Gapple::class.java,
            Disabler::class.java,
            CSDisabler::class.java,
            Crosshair::class.java,
            Rotate::class.java,
            AntiFall::class.java,
            TargetESP::class.java,
            TwoDTags::class.java,
            BanNotifier::class.java,
            AntiFireBall::class.java,
            KeepSprint::class.java,
            LookTP::class.java,
            Sensor::class.java,
            BowLongJump::class.java,
            ConsoleSpammer::class.java,
            PointerESP::class.java,
            SafeWalk::class.java,
            NoMouseIntersect::class.java,
            AntiHunger::class.java,
            DoubleJump::class.java,
            AntiCactus::class.java,
            FastBridge::class.java,
            Terrain::class.java,
            Parkour::class.java,
            Spider::class.java,
            FakeLag::class.java,
            PacketFixer::class.java,
            AutoPlay::class.java,
            AntiStaff::class.java,
            NoInvClose::class.java,
            TPAura::class.java,
            AutoHeal::class.java,
            AntiAFK::class.java,
            AutoFish::class.java,
            Damage::class.java,
            GodMode::class.java,
            AutoWalk::class.java,
            AutoBackstab::class.java,
            IceSpeed::class.java,
            HorseJump::class.java,
            LiquidInteract::class.java,
            AutoMine::class.java,
            Nuker::class.java,
            SuperheroFX::class.java,
            Gui::class.java,
            ResetVL::class.java,
            FastMine::class.java,
            Annoy::class.java,
            EntityDesync::class.java,
            HudEditor::class.java,
            ThunderNotifier::class.java,
            NoClip::class.java,
            AntiObsidian::class.java,
            Mobs::class.java,
            Players::class.java,
            Animals::class.java,
            Invisible::class.java,
            AutoArmor::class.java,
            Trajectories::class.java,
            RealBobbing::class.java,
            TargetStrafe::class.java,
            Bhop::class.java,
            VanillaFlight::class.java,
            Wings::class.java,
            NoC0Fs::class.java,
            SilentView::class.java,
            LagBack::class.java,
            Animals::class.java,
            Invis::class.java,
            Mobs::class.java,
            Players::class.java,
            ItemPhysics::class.java,
            AutoJump::class.java,
            Tweaks::class.java,
            HackerDetector::class.java,
            Dead::class.java,
            Invisible::class.java,
            PotionSpoof::class.java,
            TickTimer::class.java,
            HitParticles::class.java,
            CivBreak::class.java,
            PlayerEdit::class.java,
            ClientSpoof::class.java,
            Knockback::class.java,
            TriggerBot::class.java,
            FakePlayer::class.java,
            MurderDetector::class.java,
            JumpCircle::class.java
        )

        registerModule(Fucker)
        registerModule(StealAura)

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
        NightX.eventManager.registerListener(module)
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
        NightX.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        NightX.commandManager.registerCommand(ModuleCommand(module, values))
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
