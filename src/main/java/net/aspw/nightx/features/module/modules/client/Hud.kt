package net.aspw.nightx.features.module.modules.client

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.FloatValue
import net.aspw.nightx.value.FontValue
import net.aspw.nightx.value.ListValue
import net.aspw.nightx.visual.font.Fonts
import net.aspw.nightx.visual.hud.designer.GuiHudDesigner

@ModuleInfo(name = "Hud", category = ModuleCategory.CLIENT, array = false)
class Hud : Module() {
    val nof5Crosshair = BoolValue("No-ThirdPerson-Crosshair", true)
    val animHotbarValue = BoolValue("Hotbar-Animation", false)
    val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F, { animHotbarValue.get() })
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    val fontChatValue = BoolValue("FontChat", false)
    val fontType = FontValue("Font", Fonts.fontSFUI40, { fontChatValue.get() })
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animation", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F, { chatAnimationValue.get() })
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val guiButtonStyle =
        ListValue("Button-Style", arrayOf("Minecraft", "LiquidBounce", "Rounded", "LiquidBounce+"), "Minecraft")

    val containerBackground = BoolValue("Gui-Background", true)
    val containerButton = ListValue("Gui-Button", arrayOf("TopLeft", "TopRight", "Off"), "Off")
    val invEffectOffset = BoolValue("InventoryEffect-Moveable", false)

    private var hotBarX = 0F

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner) return
        NightX.hud.render(false)
    }

    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {
        if (NightX.moduleManager.shouldNotify != toggleMessageValue.get())
            NightX.moduleManager.shouldNotify = toggleMessageValue.get()

        if (NightX.moduleManager.toggleSoundMode != toggleSoundValue.values.indexOf(toggleSoundValue.get()))
            NightX.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())

        if (NightX.moduleManager.toggleVolume != 90f)
            NightX.moduleManager.toggleVolume = 90f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        NightX.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        NightX.hud.handleKey('a', event.key)
    }

    fun getAnimPos(pos: Float): Float {
        if (state && animHotbarValue.get()) hotBarX =
            AnimationUtils.animate(pos, hotBarX, animHotbarSpeedValue.get() * RenderUtils.deltaTime.toFloat())
        else hotBarX = pos

        return hotBarX
    }

    init {
        state = true
    }
}