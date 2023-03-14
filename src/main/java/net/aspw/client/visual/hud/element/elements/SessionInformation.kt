package net.aspw.client.visual.hud.element.elements

import net.aspw.client.Client
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.FontValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import java.awt.Color

@ElementInfo(name = "SessionInformation", disableScale = true, priority = 1)
class SessionInformation(
    x: Double = 5.0, y: Double = 19.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)
) : Element(x, y, scale, side) {
    private val radiusValue = FloatValue("Radius", 4.25f, 0f, 10f)
    private val bgredValue = IntegerValue("Background Red", 255, 0, 255)
    private val bggreenValue = IntegerValue("Background Green", 180, 0, 255)
    private val bgblueValue = IntegerValue("Background Blue", 255, 0, 255)
    private val bgalphaValue = IntegerValue("Background Alpha", 120, 0, 255)

    private val lineValue = BoolValue("Line", true)
    private val redValue = IntegerValue("Line-Red-1", 255, 0, 255)
    private val greenValue = IntegerValue("Line-Green-1", 255, 0, 255)
    private val blueValue = IntegerValue("Line-Blue-1", 255, 0, 255)
    private val colorRedValue2 = IntegerValue("Line-Red-2", 0, 0, 255)
    private val colorGreenValue2 = IntegerValue("Line-Green-2", 111, 0, 255)
    private val colorBlueValue2 = IntegerValue("Line-Blue-2", 255, 0, 255)

    private val fontValue = FontValue("Font", Fonts.fontSFUI40)

    override fun drawElement(): Border {
        val fontRenderer = fontValue.get()

        val y2 = fontRenderer.FONT_HEIGHT * 3 + 11.0
        val x2 = 140.0

        val durationInMillis: Long = System.currentTimeMillis() - Client.playTimeStart
        val second = durationInMillis / 1000 % 60
        val minute = durationInMillis / (1000 * 60) % 60
        val hour = durationInMillis / (1000 * 60 * 60) % 24
        var time: String
        time = String.format("%02dh %02dm %02ds", hour, minute, second)

        RenderUtils.drawRoundedRect(
            -2f,
            -2f,
            x2.toFloat(),
            y2.toFloat(),
            radiusValue.get(),
            Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()).rgb
        )
        if (lineValue.get()) {
            RenderUtils.drawGradientSideways(
                2.44,
                fontRenderer.FONT_HEIGHT + 2.5 + 0.0,
                138.0 + -2.44,
                fontRenderer.FONT_HEIGHT + 2.5 + 1.16,
                Color(redValue.get(), greenValue.get(), blueValue.get()).rgb,
                Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get()).rgb
            )
        }
        val username = mc.thePlayer.name
        fontRenderer.drawStringWithShadow("Session Information", x2.toFloat() / 5f, 3f, Color.WHITE.rgb)
        fontRenderer.drawStringWithShadow("Play Time: $time", 2f, fontRenderer.FONT_HEIGHT + 8f, Color.WHITE.rgb)
        fontRenderer.drawStringWithShadow(
            "Current ID: $username",
            2f,
            fontRenderer.FONT_HEIGHT * 2 + 8f,
            Color.WHITE.rgb
        )
        return Border(-2f, -2f, x2.toFloat(), y2.toFloat())
    }
}