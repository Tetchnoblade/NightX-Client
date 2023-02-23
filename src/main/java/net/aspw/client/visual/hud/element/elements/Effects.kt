package net.aspw.client.visual.hud.element.elements

import net.aspw.client.value.BoolValue
import net.aspw.client.value.FontValue
import net.aspw.client.visual.font.AWTFontRenderer.Companion.assumeNonVolatile
import net.aspw.client.visual.font.Fonts
import net.aspw.client.visual.hud.element.Border
import net.aspw.client.visual.hud.element.Element
import net.aspw.client.visual.hud.element.ElementInfo
import net.aspw.client.visual.hud.element.Side
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion

/**
 * CustomHUD effects element
 *
 * Shows a list of active potion effects
 */
@ElementInfo(name = "Effects")
class Effects(
    x: Double = 1.0, y: Double = 1.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

    private val anotherStyle = BoolValue("New", true)
    private val fontValue = FontValue("Font", Fonts.fontSFUI40)
    private val shadow = BoolValue("Shadow", true)

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        val fontRenderer = fontValue.get()

        var y = 0F
        var width = 0F

        assumeNonVolatile = true

        for (effect in mc.thePlayer.activePotionEffects) {
            if (side.vertical == Side.Vertical.DOWN)
                y -= fontRenderer.FONT_HEIGHT + if (anotherStyle.get()) 1F else 0F

            val potion = Potion.potionTypes[effect.potionID]

            val number = when {
                effect.amplifier == 1 -> "II"
                effect.amplifier == 2 -> "III"
                effect.amplifier == 3 -> "IV"
                effect.amplifier == 4 -> "V"
                effect.amplifier == 5 -> "VI"
                effect.amplifier == 6 -> "VII"
                effect.amplifier == 7 -> "VIII"
                effect.amplifier == 8 -> "IX"
                effect.amplifier == 9 -> "X"
                effect.amplifier > 10 -> "X+"
                else -> "I"
            }

            val duration = if (effect.isPotionDurationMax) 30 else effect.duration / 20
            val name = if (anotherStyle.get())
                "${I18n.format(potion.name)} $number ${if (duration < 15) "§c" else if (duration < 30) "§6" else "§7"}${
                    Potion.getDurationString(
                        effect
                    )
                }"
            else
                "${I18n.format(potion.name)} $number§f: §7${Potion.getDurationString(effect)}"
            val stringWidth = fontRenderer.getStringWidth(name).toFloat()

            if (side.horizontal == Side.Horizontal.RIGHT) {
                if (width > -stringWidth)
                    width = -stringWidth
            } else {
                if (width < stringWidth)
                    width = stringWidth
            }

            when (side.horizontal) {
                Side.Horizontal.RIGHT -> fontRenderer.drawString(
                    name,
                    -stringWidth,
                    y + if (side.vertical == Side.Vertical.UP) -fontRenderer.FONT_HEIGHT.toFloat() else 0F,
                    potion.liquidColor,
                    shadow.get()
                )

                Side.Horizontal.LEFT, Side.Horizontal.MIDDLE -> fontRenderer.drawString(
                    name,
                    0F,
                    y + if (side.vertical == Side.Vertical.UP) -fontRenderer.FONT_HEIGHT.toFloat() else 0F,
                    potion.liquidColor,
                    shadow.get()
                )
            }

            if (side.vertical == Side.Vertical.UP)
                y += fontRenderer.FONT_HEIGHT + if (anotherStyle.get()) 1F else 0F
        }

        assumeNonVolatile = false

        if (width == 0F)
            width = if (side.horizontal == Side.Horizontal.RIGHT) -40F else 40F

        if (y == 0F) // alr checked above
            y =
                if (side.vertical == Side.Vertical.UP) fontRenderer.FONT_HEIGHT.toFloat() else -fontRenderer.FONT_HEIGHT.toFloat()

        return Border(0F, 0F, width, y)
    }
}