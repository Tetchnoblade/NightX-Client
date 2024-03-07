package net.aspw.client.visual.client.clickgui.smooth.buttons

import net.aspw.client.Launch
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.utils.geom.Rectangle
import net.aspw.client.utils.render.RenderUtils.drawBorderedRect
import net.aspw.client.utils.render.RenderUtils.drawRect
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.BACKGROUND_CATEGORY
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.FONT
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.PANEL_HEIGHT
import net.aspw.client.visual.client.clickgui.smooth.SmoothConstants.PANEL_WIDTH
import net.aspw.client.visual.client.clickgui.smooth.drawHeightCenteredString
import net.aspw.client.visual.font.semi.Fonts
import java.awt.Color

class CategoryPanel(x: Float, y: Float, var category: ModuleCategory, var color: Color) :
    Button(x, y, PANEL_WIDTH, PANEL_HEIGHT) {
    var open = true
    var moduleButtons = ArrayList<ModuleButton>()
    val name = category.displayName
    private var dragged = false
    private var mouseX2 = 0
    private var mouseY2 = 0

    init {
        val startY = y + height
        for ((count, mod) in Launch.moduleManager.modules.filter {
            it.category.displayName.equals(
                this.category.displayName,
                true
            )
        }.withIndex()) {
            moduleButtons.add(ModuleButton(x, startY + height * count, width, height, mod, color))
        }
    }

    override fun drawPanel(mouseX: Int, mouseY: Int): Rectangle {
        if (dragged) {
            x = (mouseX2 + mouseX).toFloat()
            y = (mouseY2 + mouseY).toFloat()
        }
        drawRect(x, y, x + width, y + height, BACKGROUND_CATEGORY)

        val markColor = if (open) Color(255, 255, 255, 200).rgb else Color(255, 255, 255, 80).rgb

        if (open) {
            Fonts.marks.drawStringWithShadow(
                "d",
                x + width - 16,
                y + height - 14,
                markColor
            )
        } else {
            Fonts.marks.drawStringWithShadow(
                "d",
                x + width - 16,
                y + height - 14,
                markColor
            )
        }

        val xPos = 74

        when (category.displayName.lowercase()) {
            "combat" -> Fonts.icons.drawStringWithShadow("J", x + xPos, y + 4, markColor)
            "movement" -> Fonts.icons.drawStringWithShadow("G", x + xPos, y + 4, markColor)
            "player" -> Fonts.icons.drawStringWithShadow("F", x + xPos, y + 4, markColor)
            "exploit" -> Fonts.icons.drawStringWithShadow("A", x + xPos, y + 4, markColor)
            "other" -> Fonts.icons.drawStringWithShadow("B", x + xPos, y + 4, markColor)
            "visual" -> Fonts.icons.drawStringWithShadow("H", x + xPos, y + 4, markColor)
            "targets" -> Fonts.icons.drawStringWithShadow("I", x + xPos, y + 4, markColor)
        }

        FONT.drawHeightCenteredString("§l" + category.displayName, x + 3, y + 1 + height / 2, -0x1)

        var used = 0f
        if (open) {
            val startY = y + height
            for (moduleButton in moduleButtons) {
                moduleButton.x = x
                moduleButton.y = startY + used
                val box = moduleButton.drawPanel(mouseX, mouseY)
                used += box.height
            }
        }

        drawBorderedRect(x, y, x + width, y + height + used, 1f, color.rgb, Color(10, 10, 10, 20).rgb)

        return Rectangle()
    }

    override fun mouseAction(mouseX: Int, mouseY: Int, click: Boolean, button: Int) {
        if (isHovered(mouseX, mouseY)) {
            if (click) {
                if (button == 0) {
                    dragged = true
                    mouseX2 = (x - mouseX).toInt()
                    mouseY2 = (y - mouseY).toInt()
                } else {
                    open = !open
                }
            }
        }
        if (!click) dragged = false
    }
}
