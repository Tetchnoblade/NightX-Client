package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.NightX
import net.aspw.nightx.event.*
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.ui.client.hud.designer.GuiHudDesigner
import net.aspw.nightx.ui.font.Fonts
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.value.*

@ModuleInfo(name = "HUD", category = ModuleCategory.RENDER, array = false)
class HUD : Module() {
    val tabHead = BoolValue("Tab-HeadOverlay", true)
    val animHotbarValue = BoolValue("AnimatedHotbar", false)
    val blackHotbarValue = BoolValue("BlackHotbar", false)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    val fontChatValue = BoolValue("FontChat", false)
    val cmdBorderValue = BoolValue("CommandChatBorder", false)
    val fontType = FontValue("Font", Fonts.fontSFUI40, { fontChatValue.get() })
    val chatRectValue = BoolValue("ChatRect", true)
    val chatCombineValue = BoolValue("ChatCombine", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 10.0F, 0.01F, 10.0F)
    private val toggleMessageValue = BoolValue("DisplayToggleMessage", false)
    private val toggleSoundValue = ListValue("ToggleSound", arrayOf("None", "Default", "Custom"), "None")
    private val toggleVolumeValue =
        IntegerValue("ToggleVolume", 94, 0, 100, { toggleSoundValue.get().equals("custom", true) })
    val guiButtonStyle =
        ListValue("Button-Style", arrayOf("Minecraft", "LiquidBounce", "Rounded", "LiquidBounce+", "Test"), "Minecraft")

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
            AnimationUtils.animate(pos, hotBarX, 0.02F * RenderUtils.deltaTime.toFloat())
        else hotBarX = pos

        return hotBarX
    }

    init {
        state = true
    }
}