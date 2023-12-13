package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.protocol.ProtocolBase
import net.aspw.client.util.AnimationUtils
import net.aspw.client.util.network.Access
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.value.*
import net.aspw.client.visual.client.GuiTeleportation
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.tab.NewUi
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.network.play.server.S3APacketTabComplete
import net.minecraft.network.play.server.S45PacketTitle
import java.awt.Color

@ModuleInfo(name = "Interface", description = "", category = ModuleCategory.VISUAL, array = false)
class Interface : Module() {
    private val watermarkValue = BoolValue("WaterMark", true)
    private val clientNameValue = TextValue("ClientName", "Night-X") { watermarkValue.get() }
    val nof5crossHair = BoolValue("NoF5-Crosshair", true)
    val animHotbarValue = BoolValue("Hotbar-Animation", false)
    private val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F) { animHotbarValue.get() }
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    private val noInvClose = BoolValue("NoInvClose", true)
    private val noTitle = BoolValue("NoTitle", false)
    private val antiTabComplete = BoolValue("AntiTabComplete", false)
    val customFov = BoolValue("CustomFov", false)
    val customFovModifier = FloatValue("Fov", 1.4F, 0.8F, 1.5F) { customFov.get() }
    val fontChatValue = BoolValue("FontChat", false)
    val fontType = FontValue("Font", Fonts.fontSFUI37) { fontChatValue.get() }
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animation", false)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F) { chatAnimationValue.get() }
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val swingSoundValue = BoolValue("Swing-Sound", false)

    private var hotBarX = 0F

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        Client.hud.render(false)
        if (watermarkValue.get()) {
            val inputString = clientNameValue.get()
            val firstChar = inputString[0]
            val restOfString = inputString.substring(1)
            val showName =
                if (Access.canConnect) "$firstChar§d$restOfString" + " §b[" + Client.CLIENT_VERSION + "] | FPS: " + Minecraft.getDebugFPS() + " | version: " + ProtocolBase.getManager().targetVersion.getName() else "$firstChar§d$restOfString" + " §b[" + Client.CLIENT_VERSION + "] | FPS: " + Minecraft.getDebugFPS() + " | version: " + ProtocolBase.getManager().targetVersion.getName() + " | Disconnected"
            FontLoaders.SF20.drawStringWithShadow(
                showName,
                2.0,
                3.0,
                Color(169, 0, 170).rgb
            )
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

        if (Client.moduleManager.toggleVolume != 83f)
            Client.moduleManager.toggleVolume = 83f
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
}