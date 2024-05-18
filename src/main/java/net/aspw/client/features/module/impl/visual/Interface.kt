package net.aspw.client.features.module.impl.visual

import net.aspw.client.Launch
import net.aspw.client.event.EventTarget
import net.aspw.client.event.PacketEvent
import net.aspw.client.event.Render2DEvent
import net.aspw.client.event.TickEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.features.module.impl.combat.KillAura
import net.aspw.client.features.module.impl.combat.KillAuraRecode
import net.aspw.client.features.module.impl.combat.TPAura
import net.aspw.client.utils.APIConnecter
import net.aspw.client.utils.AnimationUtils
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
    private val arrayListValue = BoolValue("ArrayList", true)
    private val arrayListSpeedValue = FloatValue("ArrayList-AnimationSpeed", 0.3F, 0F, 0.6F) { arrayListValue.get() }
    private val targetHudValue = BoolValue("TargetHud", true)
    private val targetHudSpeedValue = FloatValue("TargetHud-AnimationSpeed", 3F, 0F, 6F) { targetHudValue.get() }
    private val targetHudXPosValue = FloatValue("TargetHud-XPos", 0F, -300F, 300F) { targetHudValue.get() }
    private val targetHudYPosValue = FloatValue("TargetHud-YPos", 0F, -300F, 300F) { targetHudValue.get() }
    private val cFontValue = BoolValue("C-Font", true)
    val itemVisualSpoofsValue = BoolValue("ItemVisualSpoof", true)
    val noAchievements = BoolValue("No-Achievements", true)
    val animHotbarValue = BoolValue("Hotbar-Animation", false)
    private val animHotbarSpeedValue = FloatValue("Hotbar-AnimationSpeed", 0.03F, 0.01F, 0.2F) { animHotbarValue.get() }
    val blackHotbarValue = BoolValue("Black-Hotbar", false)
    private val noInvClose = BoolValue("NoInvClose", true)
    private val noTitle = BoolValue("NoTitle", false)
    private val antiTabComplete = BoolValue("AntiTabComplete", false)
    val customFov = BoolValue("CustomFov", false)
    val customFovModifier = FloatValue("Fov", 1.4F, 1F, 1.8F) { customFov.get() }
    val chatRectValue = BoolValue("ChatRect", true)
    val chatAnimationValue = BoolValue("Chat-Animation", true)
    val chatAnimationSpeedValue = FloatValue("Chat-AnimationSpeed", 0.06F, 0.01F, 0.5F) { chatAnimationValue.get() }
    private val toggleMessageValue = BoolValue("Toggle-Notification", false)
    private val toggleSoundValue = ListValue("Toggle-Sound", arrayOf("None", "Default", "Custom"), "None")
    val flagSoundValue = BoolValue("Pop-Sound", true)
    val swingSoundValue = BoolValue("Swing-Sound", false)

    private var hotBarX = 0F
    private var easingHealth = 0F

    private var modules = emptyList<Module>()
    private var sortedModules = emptyList<Module>()

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (watermarkValue.get()) {
            val inputString = clientNameValue.get()
            val connectChecks = if (!APIConnecter.canConnect) " - Disconnected" else ""
            var firstChar = ""
            var restOfString = ""
            if (inputString != "") {
                firstChar = inputString[0].toString()
                restOfString = inputString.substring(1)
            }
            val showName = "$firstChar§r§f$restOfString$connectChecks"
            if (cFontValue.get())
                FontLoaders.SF20.drawStringWithShadow(showName, 2.0, 3.0, RenderUtils.skyRainbow(0, 0.5f, 1f).rgb)
            else Fonts.minecraftFont.drawStringWithShadow(showName, 2.0f, 3.0f, RenderUtils.skyRainbow(0, 0.5f, 1f).rgb)
        }

        if (arrayListValue.get()) {
            val counter = intArrayOf(0)
            val delta = RenderUtils.deltaTime
            var inx = 0
            for (module in sortedModules) {
                if (module.array && (module.state || module.slide != 0F)) {
                    val displayString = getModName(module)

                    val width = if (cFontValue.get()) FontLoaders.SF20.getStringWidth(displayString) else Fonts.minecraftFont.getStringWidth(displayString)

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

                if (cFontValue.get())
                    FontLoaders.SF20.drawStringWithShadow(displayString, xPos.toDouble(), module.arrayY + textY.toDouble(), RenderUtils.skyRainbow(index * 50, 0.6f, 1f).rgb)
                else Fonts.minecraftFont.drawStringWithShadow(displayString, xPos, module.arrayY + textY, RenderUtils.skyRainbow(index * 50, 0.6f, 1f).rgb)
            }
            GlStateManager.resetColor()
            modules = Launch.moduleManager.modules
                .filter { it.array && it.slide > 0 }
                .sortedBy { if (cFontValue.get()) -FontLoaders.SF20.getStringWidth(getModName(it)) else -Fonts.minecraftFont.getStringWidth(getModName(it)) }
            sortedModules =
                Launch.moduleManager.modules.sortedBy { if (cFontValue.get()) -FontLoaders.SF20.getStringWidth(getModName(it)) else -Fonts.minecraftFont.getStringWidth(getModName(it)) }.toList()
        }

        if (targetHudValue.get()) {
            val xPos = (ScaledResolution(mc).scaledWidth / 2) - 214f + targetHudXPosValue.get()
            val yPos = (ScaledResolution(mc).scaledHeight / 2) - 90f + targetHudYPosValue.get()
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

                if (healthColor > 255)
                    healthColor = 255

                RenderUtils.drawRect(
                    xPos + 36F,
                    yPos + 26.5F,
                    xPos + (36F + (easingHealth / entity.maxHealth).coerceIn(
                        0F,
                        entity.maxHealth
                    ) * (healthLength + 74F)),
                    yPos + 36F,
                    Color(245, healthColor, 1).rgb
                )
                Fonts.minecraftFont.drawStringWithShadow(
                    healthString,
                    xPos + 64.5F,
                    yPos + 27F,
                    Color(245, healthColor, 1).rgb
                )

                RenderUtils.newDrawRect(xPos - 1, yPos + 3, xPos + 33F, yPos + 37F, Color(0, 0, 0).rgb)

                if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
                    drawHead(
                        mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin,
                        xPos.toInt(),
                        yPos.toInt() + 4
                    )
                else if (cFontValue.get()) {
                    FontLoaders.SF21.drawStringWithShadow(
                        "No",
                        (xPos + 9).toDouble(),
                        (yPos + 7F).toDouble(),
                        Color(120, 120, 120).rgb
                    )
                    FontLoaders.SF21.drawStringWithShadow(
                        "Image",
                        (xPos + 2).toDouble(),
                        (yPos + 16F).toDouble(),
                        Color(120, 120, 120).rgb
                    )
                    FontLoaders.SF21.drawStringWithShadow(
                        "Found",
                        (xPos + 1).toDouble(),
                        (yPos + 25F).toDouble(),
                        Color(120, 120, 120).rgb
                    )
                } else {
                    Fonts.minecraftFont.drawStringWithShadow(
                        "No",
                        xPos + 9,
                        yPos + 7F,
                        Color(120, 120, 120).rgb
                    )
                    Fonts.minecraftFont.drawStringWithShadow(
                        "Image",
                        xPos + 2,
                        yPos + 16F,
                        Color(120, 120, 120).rgb
                    )
                    Fonts.minecraftFont.drawStringWithShadow(
                        "Found",
                        xPos + 1,
                        yPos + 25F,
                        Color(120, 120, 120).rgb
                    )
                }

                updateAnim(entity.health)

                if (cFontValue.get()) {
                    FontLoaders.SF21.drawStringWithShadow(
                        entity.name,
                        (xPos + 36F).toDouble(),
                        (yPos + 4F).toDouble(),
                        Color(255, 255, 255).rgb
                    )
                    FontLoaders.SF21.drawStringWithShadow(
                        mc.thePlayer.getDistanceToEntity(entity).toInt().toString() + " blocks away",
                        (xPos + 36F).toDouble(),
                        (yPos + 15F).toDouble(),
                        Color(255, 255, 255).rgb
                    )
                } else {
                    Fonts.minecraftFont.drawStringWithShadow(
                        entity.name,
                        xPos + 36F,
                        yPos + 4F,
                        Color(255, 255, 255).rgb
                    )
                    Fonts.minecraftFont.drawStringWithShadow(
                        mc.thePlayer.getDistanceToEntity(entity).toInt().toString() + " blocks away",
                        xPos + 36F,
                        yPos + 15F,
                        Color(255, 255, 255).rgb
                    )
                }
            } else if (easingHealth != 0F) easingHealth = 0F
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
        easingHealth += ((targetHealth - easingHealth) / 2.0F.pow(10.0F - targetHudSpeedValue.get())) * 2
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