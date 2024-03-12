package net.aspw.client.features.module.impl.visual

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render2DEvent
import net.aspw.client.event.TickEvent
import net.aspw.client.features.api.PacketManager
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.combat.KillAuraRecode
import net.aspw.client.features.module.impl.combat.TPAura
import net.aspw.client.protocol.ProtocolBase
import net.aspw.client.utils.Access
import net.aspw.client.utils.AnimationUtils
import net.aspw.client.utils.CooldownHelper
import net.aspw.client.utils.render.ColorUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.ListValue
import net.aspw.client.value.TextValue
import net.aspw.client.visual.client.clickgui.dropdown.ClickGui
import net.aspw.client.visual.client.clickgui.smooth.SmoothClickGui
import net.aspw.client.visual.client.clickgui.tab.NewUi
import net.aspw.client.visual.font.semi.Fonts
import net.aspw.client.visual.font.smooth.FontLoaders
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.network.play.client.C14PacketTabComplete
import net.minecraft.network.play.server.S2EPacketCloseWindow
import net.minecraft.network.play.server.S3APacketTabComplete
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

@ModuleInfo(name = "Interface", category = ModuleCategory.VISUAL, array = false)
class Interface : Module() {
    private val watermarkValue = BoolValue("WaterMark", true)
    private val clientNameValue = TextValue("ClientName", "NightX") { watermarkValue.get() }
    private val watermarkFpsValue = BoolValue("WaterMark-ShowFPS", true) { watermarkValue.get() }
    private val watermarkProtocolValue = BoolValue("WaterMark-ShowProtocol", true) { watermarkValue.get() }
    private val arrayListValue = BoolValue("ArrayList", true)
    private val arrayListSpeedValue = FloatValue("ArrayList-AnimationSpeed", 0.3F, 0F, 0.6F) { arrayListValue.get() }
    private val targetHudValue = BoolValue("TargetHud", true)
    private val targetHudSpeedValue = FloatValue("TargetHud-AnimationSpeed", 3F, 0F, 6F) { targetHudValue.get() }
    private val targetHudXPosValue = FloatValue("TargetHud-XPos", 0F, -300F, 300F) { targetHudValue.get() }
    private val targetHudYPosValue = FloatValue("TargetHud-YPos", 0F, -300F, 300F) { targetHudValue.get() }
    private val informationValue = BoolValue("Information", true)
    private val cooldownValue = BoolValue("Cooldown", true)
    val noAchievement = BoolValue("No-Achievements", true)
    val nof5crossHair = BoolValue("NoF5-Crosshair", true)
    val tpDebugValue = BoolValue("TP-Debug", false)
    val animHotbarValue = BoolValue("Hotbar-Animations", false)
    private val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F) { animHotbarValue.get() }
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    private val noInvClose = BoolValue("NoInvClose", true)
    private val noTitle = BoolValue("NoTitle", false)
    private val antiTabComplete = BoolValue("AntiTabComplete", false)
    val customFov = BoolValue("CustomFov", false)
    val customFovModifier = FloatValue("Fov", 1.4F, 1F, 1.8F) { customFov.get() }
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animations", true)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F) { chatAnimationValue.get() }
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val swingSoundValue = BoolValue("Swing-Sound", false)

    private var hotBarX = 0F
    private var easingHealth = 0F

    private var modules = emptyList<Module>()
    private var sortedModules = emptyList<Module>()
    private val fontRenderer = FontLoaders.SF20

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (watermarkValue.get()) {
            val inputString = clientNameValue.get()
            val connectChecks = if (!Access.canConnect) " - Disconnected" else ""
            val fpsChecks = if (watermarkFpsValue.get()) " [" + Minecraft.getDebugFPS() + " FPS]" else ""
            val protocolChecks =
                if (watermarkProtocolValue.get()) " [version: " + (if (!mc.isIntegratedServerRunning) ProtocolBase.getManager().targetVersion.name else "1.8.x") + "]" else ""
            var firstChar = ""
            var restOfString = ""
            if (inputString != "") {
                firstChar = inputString[0].toString()
                restOfString = inputString.substring(1)
            }
            val showName = "$firstChar§r§f$restOfString$fpsChecks$protocolChecks$connectChecks"
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

        if (targetHudValue.get()) {
            val xPos = (ScaledResolution(mc).scaledWidth / 2) - 54f + targetHudXPosValue.get()
            val yPos = (ScaledResolution(mc).scaledHeight / 2) + 10f + targetHudYPosValue.get()
            val font = Fonts.minecraftFont
            val killAura = Launch.moduleManager.getModule(KillAura::class.java)
            val tpAura = Launch.moduleManager.getModule(TPAura::class.java)
            val killAuraRecode = Launch.moduleManager.getModule(KillAuraRecode::class.java)
            val decimalFormat = DecimalFormat("##0.0", DecimalFormatSymbols(Locale.ENGLISH))
            val entity =
                if (killAura?.state!! && killAura.currentTarget != null) killAura.currentTarget!! else if (tpAura?.state!! && tpAura.lastTarget != null) tpAura.lastTarget!! else if (killAuraRecode?.state!! && killAuraRecode.lastTarget != null) killAuraRecode.lastTarget!! else mc.thePlayer!!
            val healthString = decimalFormat.format(entity.health)

            if (easingHealth < 0 || easingHealth > entity.maxHealth || abs(easingHealth - entity.health) < 0.01)
                easingHealth = entity.health

            updateAnim(entity.health)

            if (entity != mc.thePlayer || entity == mc.thePlayer && mc.currentScreen is GuiChat) {
                RenderUtils.drawRect(xPos - 3F, yPos + 1F, xPos + 114F, yPos + 39.5F, Color(0, 0, 0, 120).rgb)

                var healthColor = 91
                repeat(8) {
                    healthColor += entity.health.toInt()
                }

                val healthLength = (entity.health / entity.maxHealth).coerceIn(0F, 1F)
                RenderUtils.drawRect(
                    xPos + 36F,
                    yPos + 26.5F,
                    xPos + (36F + (easingHealth / entity.maxHealth).coerceIn(
                        0F,
                        entity.maxHealth
                    ) * (healthLength + 74F)),
                    yPos + 36F,
                    Color(
                        245,
                        healthColor,
                        1
                    ).rgb
                )

                RenderUtils.newDrawRect(xPos - 1, yPos + 3, xPos + 33F, yPos + 37F, Color(150, 150, 150).rgb)
                RenderUtils.newDrawRect(xPos - 1, yPos + 3, xPos + 33F, yPos + 37F, Color(0, 0, 0).rgb)

                if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
                    drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin, xPos.toInt(), yPos.toInt() + 4)

                updateAnim(entity.health)

                font.drawStringWithShadow(entity.name, xPos + 36F, yPos + 4F, Color(255, 255, 255).rgb)
                font.drawStringWithShadow(
                    mc.thePlayer.getDistanceToEntity(entity).toInt().toString() + " blocks away",
                    xPos + 36F,
                    yPos + 15F,
                    Color(255, 255, 255).rgb
                )

                font.drawStringWithShadow(
                    healthString,
                    xPos + 64.5F,
                    yPos + 27F,
                    Color(
                        245,
                        healthColor,
                        1
                    ).rgb
                )
            } else if (easingHealth != 0F) easingHealth = 0F
        }

        if (informationValue.get()) {
            val xPos = ScaledResolution(mc).scaledWidth
            val yPos = ScaledResolution(mc).scaledHeight

            fontRenderer.drawStringWithShadow(
                "Ping: " + mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID).responseTime + "ms",
                (xPos - 4f - fontRenderer.getStringWidth("Ping: " + mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID).responseTime + "ms")).toDouble(),
                (yPos - 34f).toDouble(),
                Color.WHITE.rgb
            )
            fontRenderer.drawStringWithShadow(
                "Packets Sent: " + PacketManager.sendPacketCounts,
                (xPos - 4f - fontRenderer.getStringWidth("Packets Sent: " + PacketManager.sendPacketCounts)).toDouble(),
                (yPos - 23f).toDouble(),
                Color.WHITE.rgb
            )
            fontRenderer.drawStringWithShadow(
                "Packets Received: " + PacketManager.receivePacketCounts,
                (xPos - 4f - fontRenderer.getStringWidth("Packets Received: " + PacketManager.receivePacketCounts)).toDouble(),
                (yPos - 12f).toDouble(),
                Color.WHITE.rgb
            )
        }

        if (cooldownValue.get()) {
            val xPos = (ScaledResolution(mc).scaledWidth / 2f) - 130f
            val yPos = ScaledResolution(mc).scaledHeight
            val progress = CooldownHelper.getAttackCooldownProgress()

            RenderUtils.newDrawRect(xPos - 26f, yPos - 13f, xPos + 26f, yPos - 8f, Color(150, 150, 150).rgb)
            RenderUtils.newDrawRect(xPos - 26f, yPos - 13f, xPos + 26f, yPos - 8f, Color(0, 0, 0).rgb)

            if (progress < 1.0 && !mc.isIntegratedServerRunning) {
                RenderUtils.drawRect(xPos - 25f, yPos - 12f, xPos + 25f, yPos - 9f, Color(0, 0, 0, 150).rgb)
                RenderUtils.drawRect(
                    xPos - 25f,
                    yPos - 12f,
                    xPos + 25f - 50f * progress.toFloat(),
                    yPos - 9f,
                    Color(255, 255, 255, 200).rgb
                )
            } else RenderUtils.drawRect(xPos - 25f, yPos - 12f, xPos + 25f, yPos - 9f, Color(255, 255, 255, 200).rgb)
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

    private fun drawHead(skin: ResourceLocation, x: Int = 2, y: Int = 2) {
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(
            x, y, 8F, 8F, 8, 8, 32, 32,
            64F, 64F
        )
        glDepthMask(true)
        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
    }

    private fun updateAnim(targetHealth: Float) {
        easingHealth += ((targetHealth - easingHealth) / 2.0F.pow(10.0F - targetHudSpeedValue.get())) * RenderUtils.deltaTime
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