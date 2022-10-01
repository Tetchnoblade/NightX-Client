package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*

@ModuleInfo(name = "HUD", description = "", category = ModuleCategory.RENDER, array = false)
class HUD : Module() {
    val tabHead = BoolValue("Tab-HeadOverlay", false)
    val animHotbarValue = BoolValue("AnimatedHotbar", false)
    val blackHotbarValue = BoolValue("BlackHotbar", false)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    val fontChatValue = BoolValue("FontChat", false)
    val cmdBorderValue = BoolValue("CommandChatBorder", false)
    val fontType = FontValue("Font", Fonts.font40, { fontChatValue.get() })
    val chatRectValue = BoolValue("ChatRect", true)
    val chatCombineValue = BoolValue("ChatCombine", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.04F, 0.01F, 4.0F)
    private val toggleMessageValue = BoolValue("DisplayToggleMessage", false)
    private val toggleSoundValue = ListValue("ToggleSound", arrayOf("None", "Default", "Custom"), "None")
    private val toggleVolumeValue =
        IntegerValue("ToggleVolume", 94, 0, 100, { toggleSoundValue.get().equals("custom", true) })
    val guiButtonStyle =
        ListValue("Button-Style", arrayOf("Minecraft", "LiquidBounce", "Rounded", "LiquidBounce+"), "Minecraft")

    val containerBackground = BoolValue("Container-Background", true)
    val containerButton = ListValue("Container-Button", arrayOf("TopLeft", "TopRight", "Off"), "Off")
    val invEffectOffset = BoolValue("InvEffect-Offset", false)
    val domainValue = TextValue("Scoreboard-Domain", ".hud scoreboard-domain NightX-Client")

    private var hotBarX = 0F

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner) return
        LiquidBounce.hud.render(false)
    }

    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {
        if (LiquidBounce.moduleManager.shouldNotify != toggleMessageValue.get())
            LiquidBounce.moduleManager.shouldNotify = toggleMessageValue.get()

        if (LiquidBounce.moduleManager.toggleSoundMode != toggleSoundValue.values.indexOf(toggleSoundValue.get()))
            LiquidBounce.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())

        if (LiquidBounce.moduleManager.toggleVolume != toggleVolumeValue.get().toFloat())
            LiquidBounce.moduleManager.toggleVolume = toggleVolumeValue.get().toFloat()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
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