package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.value.*
import net.aspw.nightx.visual.font.Fonts
import net.aspw.nightx.visual.hud.designer.GuiHudDesigner

@ModuleInfo(name = "Hud", category = ModuleCategory.RENDER, array = false)
class Hud : Module() {
    val tabHead = BoolValue("Tab-HeadOverlay", true)
    val animHotbarValue = BoolValue("HotbarAnimation", false)
    val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F, { animHotbarValue.get() })
    val blackHotbarValue = BoolValue("BlackHotbar", false)
    val fontChatValue = BoolValue("FontChat", false)
    val fontType = FontValue("Font", Fonts.fontSFUI40, { fontChatValue.get() })
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("ChatAnimation", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F, { chatAnimationValue.get() })
    private val toggleMessageValue = BoolValue("DisplayToggleMessage", false)
    private val toggleSoundValue = ListValue("ToggleSound", arrayOf("None", "Default", "Custom"), "None")
    private val toggleVolumeValue =
        IntegerValue("ToggleVolume", 90, 0, 100, { toggleSoundValue.get().equals("custom", true) })
    val guiButtonStyle =
        ListValue("Button-Style", arrayOf("Minecraft", "LiquidBounce", "Rounded", "LiquidBounce+"), "Minecraft")

    val containerBackground = BoolValue("Container-Background", true)
    val containerButton = ListValue("Container-Button", arrayOf("TopLeft", "TopRight", "Off"), "Off")
    val invEffectOffset = BoolValue("InvEffect-Offset", false)

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

        if (NightX.moduleManager.toggleVolume != toggleVolumeValue.get().toFloat())
            NightX.moduleManager.toggleVolume = toggleVolumeValue.get().toFloat()
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