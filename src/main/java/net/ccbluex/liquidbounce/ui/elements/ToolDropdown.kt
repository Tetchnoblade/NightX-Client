package net.ccbluex.liquidbounce.ui.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager.resetColor
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object ToolDropdown {

    private var fullHeight = 0F
    private var dropState = false

    private val expandIcon = ResourceLocation("liquidbounce+/expand.png")

    @JvmStatic
    fun handleDraw(button: GuiButton) {
        val gray = Color(100, 100, 100).rgb
        val bWidth = button.buttonWidth.toFloat()

        glPushMatrix()
        glTranslatef(
            button.xPosition.toFloat() + button.buttonWidth.toFloat() - 10F,
            button.yPosition.toFloat() + 10F,
            0F
        )
        if (button.isMouseOver)
            glTranslatef(0F, if (dropState) -1F else 1F, 0F)
        glPushMatrix()
        glRotatef(180F * (fullHeight / 100F), 0F, 0F, 1F)
        RenderUtils.drawImage(expandIcon, -4, -4, 8, 8)
        glPopMatrix()
        glPopMatrix()
        resetColor()

        if (!dropState && fullHeight == 0F) return
        fullHeight =
            AnimationUtils.animate(if (dropState) 100F else 0F, fullHeight, 0.01F * RenderUtils.deltaTime.toFloat())

        glPushMatrix()
        RenderUtils.makeScissorBox(
            button.xPosition.toFloat(),
            button.yPosition.toFloat() + 20F,
            button.xPosition.toFloat() + bWidth,
            button.yPosition.toFloat() + 20F + fullHeight
        )
        glEnable(GL_SCISSOR_TEST)
        glPushMatrix()
        glTranslatef(button.xPosition.toFloat(), button.yPosition.toFloat() + 20F - (100F - fullHeight), 0F)
        RenderUtils.newDrawRect(0F, 0F, bWidth, 100F, Color(24, 24, 24).rgb)
        Fonts.minecraftFont.drawString("Vanilla-Spoof", 4, 7, -1)
        Fonts.minecraftFont.drawString("FML-Spoof", 4, 27, if (AntiForge.enabled) -1 else gray)
        Fonts.minecraftFont.drawString("Proxy-Spoof", 4, 47, if (AntiForge.enabled) -1 else gray)
        Fonts.minecraftFont.drawString("Payloads-Spoof", 4, 67, if (AntiForge.enabled) -1 else gray)
        Fonts.minecraftFont.drawString("Bungee-Exploit", 4, 87, -1)
        drawToggleSwitch(bWidth - 24F, 5F, 20F, 10F, AntiForge.enabled)
        drawCheckbox(bWidth - 14F, 25F, 10F, AntiForge.blockFML)
        drawCheckbox(bWidth - 14F, 45F, 10F, AntiForge.blockProxyPacket)
        drawCheckbox(bWidth - 14F, 65F, 10F, AntiForge.blockPayloadPackets)
        drawToggleSwitch(bWidth - 24F, 85F, 20F, 10F, BungeeCordSpoof.enabled)
        glPopMatrix()
        glDisable(GL_SCISSOR_TEST)
        glPopMatrix()
    }

    @JvmStatic
    fun handleClick(mouseX: Int, mouseY: Int, button: GuiButton): Boolean {
        val bX = button.xPosition.toFloat()
        val bY = button.yPosition.toFloat()
        val bWidth = button.buttonWidth.toFloat()
        if (dropState && isMouseOver(mouseX, mouseY, bX, bY + 20F, bWidth, fullHeight)) {
            when {
                isMouseOver(mouseX, mouseY, bX, bY + 20F, bWidth, 20F) -> AntiForge.enabled = !AntiForge.enabled
                isMouseOver(mouseX, mouseY, bX, bY + 40F, bWidth, 20F) -> AntiForge.blockFML = !AntiForge.blockFML
                isMouseOver(mouseX, mouseY, bX, bY + 60F, bWidth, 20F) -> AntiForge.blockProxyPacket =
                    !AntiForge.blockProxyPacket

                isMouseOver(mouseX, mouseY, bX, bY + 80F, bWidth, 20F) -> AntiForge.blockPayloadPackets =
                    !AntiForge.blockPayloadPackets

                isMouseOver(mouseX, mouseY, bX, bY + 100F, bWidth, 20F) -> BungeeCordSpoof.enabled =
                    !BungeeCordSpoof.enabled
            }
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig)
            return true
        }
        return false
    }

    private fun isMouseOver(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, height: Float) =
        mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height

    @JvmStatic
    fun toggleState() {
        dropState = !dropState
    }

    fun drawToggleSwitch(x: Float, y: Float, width: Float, height: Float, state: Boolean) {
        val borderColor = if (state) Color(0, 140, 255).rgb else Color(160, 160, 160).rgb
        val mainColor = if (state) borderColor else Color(24, 24, 24).rgb
        RenderUtils.originalRoundedRect(
            x - 0.5F,
            y - 0.5F,
            x + width + 0.5F,
            y + height + 0.5F,
            (height + 1F) / 2F,
            borderColor
        )
        RenderUtils.originalRoundedRect(x, y, x + width, y + height, height / 2F, mainColor)
        if (state)
            RenderUtils.drawFilledCircle(
                x + width - 2F - (height - 4F) / 2F,
                y + 2F + (height - 4F) / 2F,
                (height - 4F) / 2F,
                Color(24, 24, 24)
            )
        else
            RenderUtils.drawFilledCircle(
                x + 2F + (height - 4F) / 2F,
                y + 2F + (height - 4F) / 2F,
                (height - 4F) / 2F,
                Color(160, 160, 160)
            )
    }

    fun drawCheckbox(x: Float, y: Float, width: Float, state: Boolean) {
        val borderColor = if (state) Color(0, 140, 255).rgb else Color(160, 160, 160).rgb
        val mainColor = if (state) borderColor else Color(24, 24, 24).rgb
        RenderUtils.originalRoundedRect(x - 0.5F, y - 0.5F, x + width + 0.5F, y + width + 0.5F, 3F, borderColor)
        RenderUtils.originalRoundedRect(x, y, x + width, y + width, 3F, mainColor)
        if (state) {
            glColor4f(0.094F, 0.094F, 0.094F, 1F)
            RenderUtils.drawLine(x + width / 4F, y + width / 2F, x + width / 2.15F, y + width / 4F * 3F, 2F)
            RenderUtils.drawLine(x + width / 2.15F, y + width / 4F * 3F, x + width / 3.95F * 3F, y + width / 3F, 2F)
            resetColor()
            glColor4f(1F, 1F, 1F, 1F)
        }
    }

}