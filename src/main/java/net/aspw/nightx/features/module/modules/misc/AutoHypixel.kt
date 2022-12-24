package net.aspw.nightx.features.module.modules.misc

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.event.Render2DEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.utils.render.Stencil
import net.aspw.nightx.utils.timer.MSTimer
import net.aspw.nightx.value.BoolValue
import net.aspw.nightx.value.IntegerValue
import net.aspw.nightx.value.ListValue
import net.aspw.nightx.value.TextValue
import net.aspw.nightx.visual.font.Fonts
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.util.MathHelper
import java.awt.Color
import java.text.DecimalFormat
import java.util.*

@ModuleInfo(name = "AutoHypixel", spacedName = "Auto Hypixel", category = ModuleCategory.MISC)
class AutoHypixel : Module() {
    private val delayValue = IntegerValue("Delay", 3000, 0, 5000, "ms")
    private val autoGGValue = BoolValue("Auto-GG", true)
    private val ggMessageValue = TextValue("GG-Message", "GG") { autoGGValue.get() }
    private val checkValue = BoolValue("CheckGameMode", false)
    private val antiSnipeValue = BoolValue("AntiSnipe", false)
    private val renderValue = BoolValue("Render", true)
    private val modeValue = ListValue("Mode", arrayOf("Solo", "Teams", "Ranked", "Mega"), "Solo")
    private val soloTeamsValue = ListValue("Solo/Teams-Mode", arrayOf("Normal", "Insane"), "Insane") {
        modeValue.get().equals("solo", ignoreCase = true) || modeValue.get().equals("teams", ignoreCase = true)
    }
    private val megaValue = ListValue("Mega-Mode", arrayOf("Normal", "Doubles"), "Normal") {
        modeValue.get().equals("mega", ignoreCase = true)
    }
    private val timer = MSTimer()
    private val dFormat = DecimalFormat("0.0")
    private val strings = arrayOf(
        "1st Killer -",
        "1st Place -",
        "died! Want to play again? Click here!",
        "won! Want to play again? Click here!",
        "- Damage Dealt -",
        "1st -",
        "Winning Team -",
        "Winners:",
        "Winner:",
        "Winning Team:",
        " win the game!",
        "1st Place:",
        "Last team standing!",
        "Winner #1 (",
        "Top Survivors",
        "Winners -"
    )
    var shouldChangeGame = false
    var useOtherWord = false
    private var posY = -20f
    override fun onEnable() {
        shouldChangeGame = false
        timer.reset()
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (checkValue.get() && !gameMode.lowercase(Locale.getDefault()).contains("skywars")) return
        val sc = ScaledResolution(mc)
        val middleX = sc.scaledWidth / 2f
        val detail = "Next game in " + dFormat.format(
            (timer.hasTimeLeft(delayValue.get().toLong()).toFloat() / 1000f).toDouble()
        ) + "s..."
        val middleWidth = Fonts.fontSFUI40.getStringWidth(detail) / 2f
        val strength =
            MathHelper.clamp_float(timer.hasTimeLeft(delayValue.get().toLong()).toFloat() / delayValue.get(), 0f, 1f)
        val wid = strength * (5f + middleWidth) * 2f
        posY = AnimationUtils.animate(if (shouldChangeGame) 10f else -20f, posY, 0.25f * 0.05f * RenderUtils.deltaTime)
        if (!renderValue.get() || posY < -15) return
        Stencil.write(true)
        RenderUtils.drawRoundedRect(
            middleX - 5f - middleWidth,
            posY,
            middleX + 5f + middleWidth,
            posY + 15f,
            3f,
            -0x60000000
        )
        Stencil.erase(true)
        RenderUtils.drawRect(
            middleX - 5f - middleWidth,
            posY,
            middleX - 5f - middleWidth + wid,
            posY + 15f,
            Color(0.4f, 0.8f, 0.4f, 0.35f).rgb
        )
        Stencil.dispose()
        GlStateManager.resetColor()
        Fonts.fontSFUI40.drawString(detail, middleX - middleWidth - 1f, posY + 4f, -1)
    }

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        if ((!checkValue.get() || gameMode.lowercase(Locale.getDefault())
                .contains("skywars")) && shouldChangeGame && timer.hasTimePassed(delayValue.get().toLong())
        ) {
            mc.thePlayer.sendChatMessage(
                "/play " + modeValue.get().lowercase(Locale.getDefault()) + if (modeValue.get()
                        .equals("ranked", ignoreCase = true)
                ) "_normal" else if (modeValue.get().equals("mega", ignoreCase = true)) "_" + megaValue.get().lowercase(
                    Locale.getDefault()
                ) else "_" + soloTeamsValue.get().lowercase(Locale.getDefault())
            )
            shouldChangeGame = false
        }
        if (!shouldChangeGame) timer.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S02PacketChat) {
            val chat = event.packet
            if (chat.chatComponent != null) {
                if (antiSnipeValue.get() && chat.chatComponent.unformattedText.contains("Sending you to")) {
                    event.cancelEvent()
                    return
                }
                for (s in strings) if (chat.chatComponent.unformattedText.contains(s)) {
                    if (autoGGValue.get() && chat.chatComponent.unformattedText.contains(strings[3])) mc.thePlayer.sendChatMessage(
                        ggMessageValue.get()
                    )
                    shouldChangeGame = true
                    break
                }
            }
        }
    }

    companion object {
        @JvmField
        var gameMode = "NONE"
    }
}