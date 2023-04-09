package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.AnimationUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.FontValue
import net.aspw.client.value.ListValue
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.designer.GuiHudDesigner

@ModuleInfo(name = "Hud", category = ModuleCategory.VISUAL, array = false)
class Hud : Module() {
    val nof5crossHair = BoolValue("NoF5-Crosshair", true)
    val f5Animation = BoolValue("F5-Animation", false)
    val animHotbarValue = BoolValue("Hotbar-Animation", false)
    val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F, { animHotbarValue.get() })
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    val fontChatValue = BoolValue("FontChat", false)
    val fontType = FontValue("Font", Fonts.fontSFUI37, { fontChatValue.get() })
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animation", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F, { chatAnimationValue.get() })
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val guiButtonStyle =
        ListValue("Button-Style", arrayOf("Minecraft", "LiquidBounce", "Rounded", "LiquidBounce+"), "Minecraft")

    val containerBackground = BoolValue("Gui-Background", true)
    val invEffectOffset = BoolValue("InventoryEffect-Moveable", false)

    private var hotBarX = 0F

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (mc.currentScreen is GuiHudDesigner) return
        Client.hud.render(false)
    }

    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {
        if (Client.moduleManager.shouldNotify != toggleMessageValue.get())
            Client.moduleManager.shouldNotify = toggleMessageValue.get()

        if (Client.moduleManager.toggleSoundMode != toggleSoundValue.values.indexOf(toggleSoundValue.get()))
            Client.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())

        if (Client.moduleManager.toggleVolume != 90f)
            Client.moduleManager.toggleVolume = 90f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        Client.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        Client.hud.handleKey('a', event.key)
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