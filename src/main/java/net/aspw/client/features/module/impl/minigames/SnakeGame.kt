package net.aspw.client.features.module.impl.minigames

import net.aspw.client.event.EventTarget
import net.aspw.client.event.KeyEvent
import net.aspw.client.event.Render2DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.misc.RandomUtils
import net.aspw.client.util.newfont.FontLoaders
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "SnakeGame", spacedName = "Snake Game", description = "", category = ModuleCategory.MINIGAMES)
class SnakeGame : Module() {
    private var snake = mutableListOf(Position(0, 0))
    private var lastKey = 208
    private var food = Position(0, 0)
    private var score = 0

    override fun onDisable() {
        setupGame()
    }

    override fun onEnable() {
        setupGame()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        val key = event.key
        if ((key == 205 && lastKey != 203) || (key == 203 && lastKey != 205)
            || (key == 200 && lastKey != 208) || (key == 208 && lastKey != 200)
        ) {
            lastKey = key
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            if (snake[0].x == food.x && snake[0].y == food.y) {
                score += 1
                moveFood()
                snake.add(Position(snake[0].x, snake[0].y))
            }

            for (i in snake.size - 1 downTo 1) {
                snake[i].x = snake[i - 1].x
                snake[i].y = snake[i - 1].y
            }

            when (lastKey) {
                205 -> snake[0].x += 1
                203 -> snake[0].x -= 1
                200 -> snake[0].y -= 1
                208 -> snake[0].y += 1
            }

            for (i in 1 until snake.size) {
                if (snake[i].x == snake[0].x && snake[i].y == snake[0].y) {
                    setupGame()
                }
            }
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val resolution = ScaledResolution(mc)

        val width = resolution.scaledWidth
        val height = resolution.scaledHeight

        val startX = (width / 2 - fieldWidth / 2).toDouble()
        val startY = (height / 2 - fieldHeight / 2).toDouble()

        drawRect(startX, startY, startX + fieldWidth, startY + fieldHeight, Color(30, 0, 0, 0).rgb)

        for (index in snake.indices) {
            val snakeStartX = snake[index].x * blockSize + startX
            val snakeStartY = snake[index].y * blockSize + startY

            drawRect(snakeStartX, snakeStartY, snakeStartX + blockSize, snakeStartY + blockSize, Color(51, 153, 96).rgb)
        }

        if (snake[0].x * blockSize + startX >= startX + fieldWidth || snake[0].x * blockSize + startX < startX || snake[0].y * blockSize + startY < startY || snake[0].y * blockSize + startY >= startY + fieldHeight) {
            setupGame()
        }

        val foodX = food.x * blockSize + startX
        val foodY = food.y * blockSize + startY

        drawRect(foodX, foodY, foodX + blockSize, foodY + blockSize, Color(220, 20, 60).rgb)

        for (i in 0 until 18) {
            drawBorder(
                startX - i,
                startY - i,
                startX + fieldWidth + i,
                startY + fieldHeight + i,
                Color(0, 0, 0, 120).rgb
            )
        }

        FontLoaders.SF20.drawStringWithShadow(
            "Current Score: §a$score",
            startX,
            startY - 12.2,
            Color(255, 255, 255).rgb
        )
    }

    private fun setupGame() {
        snake = mutableListOf(Position(0, 0))
        moveFood()
        lastKey = 208
        score = 0
    }

    private fun moveFood() {
        val foodX = RandomUtils.nextInt(0, fieldWidth / blockSize)
        val foodY = RandomUtils.nextInt(0, fieldHeight / blockSize)
        food = Position(foodX, foodY)
    }

    private fun drawRect(paramXStart: Double, paramYStart: Double, paramXEnd: Double, paramYEnd: Double, color: Int) {
        val alpha = (color shr 24 and 0xFF) / 255.0F
        val red = (color shr 16 and 0xFF) / 255.0F
        val green = (color shr 8 and 0xFF) / 255.0F
        val blue = (color and 0xFF) / 255.0F

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)

        GL11.glPushMatrix()
        GL11.glColor4f(red, green, blue, alpha)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN)
        GL11.glVertex2d(paramXEnd, paramYStart)
        GL11.glVertex2d(paramXStart, paramYStart)
        GL11.glVertex2d(paramXStart, paramYEnd)
        GL11.glVertex2d(paramXEnd, paramYEnd)

        GL11.glEnd()
        GL11.glPopMatrix()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    }

    private fun drawBorder(paramXStart: Double, paramYStart: Double, paramXEnd: Double, paramYEnd: Double, color: Int) {
        val alpha = (color shr 24 and 0xFF) / 255.0f
        val red = (color shr 16 and 0xFF) / 255.0f
        val green = (color shr 8 and 0xFF) / 255.0f
        val blue = (color and 0xFF) / 255.0f

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)

        GlStateManager.pushMatrix()
        GL11.glColor4f(red, green, blue, alpha)
        GL11.glLineWidth(1f)
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex2d(paramXEnd, paramYStart)
        GL11.glVertex2d(paramXStart, paramYStart)
        GL11.glVertex2d(paramXStart, paramYEnd)
        GL11.glVertex2d(paramXEnd, paramYEnd)

        GL11.glEnd()
        GlStateManager.popMatrix()

        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    data class Position(var x: Int, var y: Int)

    companion object {
        private const val blockSize = 10
        private const val fieldWidth = 200
        private const val fieldHeight = 150
    }
}