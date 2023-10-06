package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.AnimationUtils
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.client.GuiTeleportation
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.tab.NewUi
import net.aspw.client.visual.font.semi.Fonts
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.network.play.server.S3APacketTabComplete
import net.minecraft.network.play.server.S45PacketTitle

@ModuleInfo(name = "Interface", description = "", category = ModuleCategory.VISUAL, array = false)
class Interface : Module() {
    private val clientNameValue = TextValue("ClientName", "N:ightX")
    val nof5crossHair = BoolValue("NoF5-Crosshair", true)
    val gcdfix = BoolValue("GCD-Fix", true)
    val animHotbarValue = BoolValue("Hotbar-Animation", false)
    private val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F) { animHotbarValue.get() }
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    private val noInvClose = BoolValue("NoInvClose", true)
    private val noTitle = BoolValue("NoTitle", false)
    private val antiTabComplete = BoolValue("AntiTabComplete", false)
    val customFov = BoolValue("CustomFov", false)
    val customFovModifier = FloatValue("Fov", 1.3F, 0.8F, 1.5F) { customFov.get() }
    val fontChatValue = BoolValue("FontChat", false)
    val fontType = FontValue("Font", Fonts.fontSFUI37) { fontChatValue.get() }
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animation", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F) { chatAnimationValue.get() }
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val swingSoundValue = BoolValue("Swing-Sound", false)
    val containerBackground = BoolValue("Gui-Background", true)
    val invEffectOffset = BoolValue("InventoryEffect-Moveable", false)

    private var hotBarX = 0F

    var rainbow = ""
    var white = ""

    private fun slashName() {
        val input = clientNameValue.get()
        val splitInput = input.split(":")

        rainbow = splitInput[0]
        white = splitInput[1]
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        Client.hud.render(false)
        slashName()
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
        if (noInvClose.get() && event.packet is S2EPacketCloseWindow && (mc.currentScreen is GuiInventory || mc.currentScreen is NewUi || mc.currentScreen is ClickGui || mc.currentScreen is GuiChat || mc.currentScreen is GuiTeleportation)) {
            event.cancelEvent()
        }
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
        hotBarX = if (state && animHotbarValue.get()) AnimationUtils.animate(
            pos,
            hotBarX,
            animHotbarSpeedValue.get() * RenderUtils.deltaTime.toFloat()
        )
        else pos

        return hotBarX
    }

    init {
        state = true
    }
}