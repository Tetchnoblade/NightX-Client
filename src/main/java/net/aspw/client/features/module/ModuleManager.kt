package net.aspw.client.features.module

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.KeyEvent
import net.aspw.client.event.Listenable
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

    init {
        Client.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("Loading modules...")

        registerModules(
            BowAim::class.java,
            AimAssist::class.java,
            AutoProjectile::class.java,
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
            SilentSneak::class.java,
            Speed::class.java,
            Tracers::class.java,
            FastUse::class.java,
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
            Plugins::class.java,
            LongJump::class.java,
            AutoClicker::class.java,
            BlockOverlay::class.java,
            Chams::class.java,
            Phase::class.java,
            Crasher::class.java,
            Animations::class.java,
            InventoryManager::class.java,
            ShowInvis::class.java,
            NoEffect::class.java,
            Trails::class.java,
            Reach::class.java,
            Hud::class.java,
            PackSpoofer::class.java,
            PortalMenu::class.java,
            WorldTime::class.java,
            EnchantColor::class.java,
            Cape::class.java,
            AntiVanish::class.java,
            AutoLogin::class.java,
            Gapple::class.java,
            Disabler::class.java,
            Crosshair::class.java,
            AntiFall::class.java,
            TargetESP::class.java,
            ESP::class.java,
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
            TPAura::class.java,
            AutoHeal::class.java,
            AntiAFK::class.java,
            AutoFish::class.java,
            GodMode::class.java,
            AutoWalk::class.java,
            AutoBackstab::class.java,
            IceSpeed::class.java,
            HorseJump::class.java,
            LiquidInteract::class.java,
            AutoMine::class.java,
            Nuker::class.java,
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
            SilentView::class.java,
            LagBack::class.java,
            Animals::class.java,
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
            MoreParticles::class.java,
            CivBreak::class.java,
            PlayerEdit::class.java,
            ClientSpoof::class.java,
            Knockback::class.java,
            TriggerBot::class.java,
            FakePlayer::class.java,
            JumpCircle::class.java,
            NoZeroZeroThree::class.java,
            Disabler2::class.java,
            FreeLook::class.java,
            ViewClip::class.java,
            CustomModel::class.java,
            AntiTabComplete::class.java,
            InfiniteDurability::class.java,
            MultiCombo::class.java,
            NoTitle::class.java,
            EntityFlight::class.java,
            Tickbase::class.java,
            BedBreaker::class.java,
            StealAura::class.java,
            BedWalker::class.java,
            Wings::class.java
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
