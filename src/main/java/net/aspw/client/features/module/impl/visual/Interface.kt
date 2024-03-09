package net.aspw.client.features.module.impl.visual

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render2DEvent
import net.aspw.client.event.TickEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.Access
import net.aspw.client.utils.AnimationUtils
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import net.aspw.client.value.TextValue
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.smooth.SmoothClickGui
import net.aspw.client.visual.client.clickgui.tab.NewUi
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.network.play.server.S3APacketTabComplete
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(name = "Interface", category = ModuleCategory.VISUAL, array = false)
class Interface : Module() {
    private val watermarkValue = BoolValue("WaterMark", true)
    private val clientNameValue = TextValue("ClientName", "NightX") { watermarkValue.get() }
    private val watermarkFpsValue = BoolValue("WaterMark-ShowFPS", true)
    private val arrayListValue = BoolValue("ArrayList", true)
    private val arrayListSpeedValue = FloatValue("ArrayList-AnimationSpeed", 0.3F, 0F, 0.6F) { arrayListValue.get() }
    val nof5crossHair = BoolValue("NoF5-Crosshair", true)
    val tpDebugValue = BoolValue("TP-Debug", false)
    val animHotbarValue = BoolValue("Hotbar-Animation", false)
    private val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F) { animHotbarValue.get() }
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    private val noInvClose = BoolValue("NoInvClose", true)
    private val noTitle = BoolValue("NoTitle", false)
    private val antiTabComplete = BoolValue("AntiTabComplete", false)
    val customFov = BoolValue("CustomFov", false)
    val customFovModifier = FloatValue("Fov", 1.4F, 1F, 1.8F) { customFov.get() }
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animation", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F) { chatAnimationValue.get() }
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val swingSoundValue = BoolValue("Swing-Sound", false)

    private var hotBarX = 0F

    private var modules = emptyList<Module>()
    private var sortedModules = emptyList<Module>()
    private val fontRenderer = FontLoaders.SF20

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (watermarkValue.get()) {
            val inputString = clientNameValue.get()
            val connectChecks = if (!Access.canConnect) " - Disconnected" else ""
            val fpsChecks = if (watermarkFpsValue.get()) " [" + Minecraft.getDebugFPS() + " FPS]" else ""
            var firstChar = ""
            var restOfString = ""
            if (inputString != "") {
                firstChar = inputString[0].toString()
                restOfString = inputString.substring(1)
            }
            val showName = "$firstChar§r§f$restOfString$fpsChecks$connectChecks"
            fontRenderer.drawStringWithShadow(
                showName,
                2.0,
                3.0,
                RenderUtils.skyRainbow(0, 0.5f, 1f).rgb
            )
        }

        if (arrayListValue.get()) {
            val counter = intArrayOf(0)
            val delta = RenderUtils.deltaTime
            var inx = 0
            for (module in sortedModules) {
                if (module.array && (module.state || module.slide != 0F)) {
                    val displayString = getModName(module)

                    val width = fontRenderer.getStringWidth(displayString)

                    if (module.state) {
                        if (module.slide < width) {
                            module.slide += arrayListSpeedValue.get() * delta
                            module.slideStep = delta / 1.2F
                        }
                    } else if (module.slide > 0) {
                        module.slide -= arrayListSpeedValue.get() * delta
                        module.slideStep = delta / 1.2F
                    }

                    module.slide = module.slide.coerceIn(0F, width.toFloat())
                    module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
                }

                val yPos = 10.24f * inx

                if (module.array && module.slide > 0F) {
                    module.arrayY = yPos
                    inx++
                }
            }
            val textY = 2.2f
            modules.forEachIndexed { index, module ->
                val displayString = getModName(module)

                val xPos = ScaledResolution(mc).scaledWidth - module.slide - 2

                counter[0] = counter[0] - 1

                fontRenderer.drawStringWithShadow(
                    displayString,
                    xPos.toDouble(),
                    module.arrayY + textY.toDouble(),
                    ColorUtils.LiquidSlowly(System.nanoTime(), index * 25, 0.35f, 1f).rgb
                )
            }
            GlStateManager.resetColor()
            modules = Launch.moduleManager.modules
                .filter { it.array && it.slide > 0 }
                .sortedBy { -fontRenderer.getStringWidth(getModName(it)) }
            sortedModules =
                Launch.moduleManager.modules.sortedBy { -fontRenderer.getStringWidth(getModName(it)) }.toList()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (noTitle.get() && event.packet is S45PacketTitle) {
            event.cancelEvent()
        }

        if (antiTabComplete.get() && (event.packet is C14PacketTabComplete || event.packet is S3APacketTabComplete)) {
            event.cancelEvent()
        }

        if (mc.theWorld == null || mc.thePlayer == null) return
        if (noInvClose.get() && event.packet is S2EPacketCloseWindow && (mc.currentScreen is GuiInventory || mc.currentScreen is NewUi || mc.currentScreen is ClickGui || mc.currentScreen is SmoothClickGui || mc.currentScreen is GuiChat)) {
            event.cancelEvent()
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {
        if (Launch.moduleManager.shouldNotify != toggleMessageValue.get())
            Launch.moduleManager.shouldNotify = toggleMessageValue.get()

        if (Launch.moduleManager.toggleSoundMode != toggleSoundValue.values.indexOf(toggleSoundValue.get()))
            Launch.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())

        if (Launch.moduleManager.toggleVolume != 83f)
            Launch.moduleManager.toggleVolume = 83f
    }

    private fun getModName(mod: Module): String {
        return mod.spacedName + getModTag(mod)
    }

    private fun getModTag(m: Module): String {
        if (m.tag == null) return ""
        var returnTag = " §7"
        returnTag += m.tag
        return returnTag
    }

    fun getAnimPos(pos: Float): Float {
        hotBarX = if (state && animHotbarValue.get()) AnimationUtils.animate(
            pos,
            hotBarX,
            animHotbarSpeedValue.get() * RenderUtils.deltaTime.toFloat()
        )
        else pos

        return hotBarX
    }
}