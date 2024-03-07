package net.aspw.client.visual.font.smooth

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.Font

class CFontRenderer(font: Font?, antiAlias: Boolean, fractionalMetrics: Boolean) :
    CFont(font, antiAlias, fractionalMetrics) {
    protected var boldChars = arrayOfNulls<CharData>(256)
    protected var italicChars = arrayOfNulls<CharData>(256)
    protected var boldItalicChars = arrayOfNulls<CharData>(256)
    private val colorCode = IntArray(32)
    protected var texBold: DynamicTexture? = null
    protected var texItalic: DynamicTexture? = null
    protected var texItalicBold: DynamicTexture? = null

    init {
        setupMinecraftColorcodes()
        setupBoldItalicIDs()
    }

    fun drawString(text: String?, x: Float, y: Float, color: Int): Float {
        return this.drawString(text, x.toDouble(), y.toDouble(), color, false)
    }

    fun drawStringWithShadow(text: String?, x: Double, y: Double, color: Int): Float {
        val shadowWidth = drawString(text, x + 0.5, y + 0.5, color, true)
        return Math.max(shadowWidth, drawString(text, x, y, color, false))
    }

    fun drawCenteredString(text: String, x: Float, y: Float, color: Int): Float {
        return this.drawString(text, x - (getStringWidth(text) / 2).toFloat() - 1, y, color)
    }

    fun drawCenteredStringWithShadow(text: String, x: Float, y: Float, color: Int): Float {
        return drawStringWithShadow(
            text,
            (x - (getStringWidth(text) / 2).toFloat() - 1).toDouble(),
            y.toDouble(),
            color
        )
    }

    fun drawString(text: String?, x: Double, y: Double, color: Int, shadow: Boolean): Float {
        var x = x
        var y = y
        var color = color
        --x
        if (text == null) {
            return 0.0f
        }
        if (color == 553648127) {
            color = 16777215
        }
        if (color and -0x4000000 == 0x0) {
            color = color or -0x1000000
        }
        if (shadow) {
            color = color and 0xFCFCFC shr 2 or (color and Color(20, 20, 20, 200).rgb)
        }
        var currentData = charData
        val alpha = (color shr 24 and 0xFF) / 255.0f
        var bold = false
        var italic = false
        var strikethrough = false
        var underline = false
        x *= 2.0
        y = (y - 3.0) * 2.0
        GL11.glPushMatrix()
        GlStateManager.scale(0.5, 0.5, 0.5)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.color(
            (color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f, (color and 0xFF) / 255.0f,
            alpha
        )
        val size = text.length
        GlStateManager.enableTexture2D()
        GlStateManager.bindTexture(tex.glTextureId)
        GL11.glBindTexture(3553, tex.glTextureId)
        var i = 0
        while (i < size) {
            val character = text[i]
            if (character.toString() == "\u00a7") {
                var colorIndex = 21
                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text[i + 1])
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (colorIndex < 16) {
                    bold = false
                    italic = false
                    underline = false
                    strikethrough = false
                    GlStateManager.bindTexture(tex.glTextureId)
                    currentData = charData
                    if (colorIndex < 0) {
                        colorIndex = 15
                    }
                    if (shadow) {
                        colorIndex += 16
                    }
                    val colorcode = colorCode[colorIndex]
                    GlStateManager.color(
                        (colorcode shr 16 and 0xFF) / 255.0f, (colorcode shr 8 and 0xFF) / 255.0f,
                        (colorcode and 0xFF) / 255.0f, alpha
                    )
                } else if (colorIndex == 17) {
                    bold = true
                    currentData = if (italic) {
                        GlStateManager.bindTexture(texItalicBold!!.glTextureId)
                        boldItalicChars
                    } else {
                        GlStateManager.bindTexture(texBold!!.glTextureId)
                        boldChars
                    }
                } else if (colorIndex == 18) {
                    strikethrough = true
                } else if (colorIndex == 19) {
                    underline = true
                } else if (colorIndex == 20) {
                    italic = true
                    currentData = if (bold) {
                        GlStateManager.bindTexture(texItalicBold!!.glTextureId)
                        boldItalicChars
                    } else {
                        GlStateManager.bindTexture(texItalic!!.glTextureId)
                        italicChars
                    }
                } else if (colorIndex == 21) {
                    bold = false
                    italic = false
                    underline = false
                    strikethrough = false
                    GlStateManager.color(
                        (color shr 16 and 0xFF) / 255.0f, (color shr 8 and 0xFF) / 255.0f,
                        (color and 0xFF) / 255.0f, alpha
                    )
                    GlStateManager.bindTexture(tex.glTextureId)
                    currentData = charData
                }
                ++i
            } else if (character.code < currentData.size) {
                GL11.glBegin(4)
                drawChar(currentData, character, x.toFloat(), y.toFloat())
                GL11.glEnd()
                if (strikethrough) {
                    drawLine(
                        x,
                        y + currentData[character.code]!!.height / 2.0f,
                        x + currentData[character.code]!!.width - 8.0,
                        y + currentData[character.code]!!.height / 2.0f,
                        1.0f
                    )
                }
                if (underline) {
                    drawLine(
                        x,
                        y + currentData[character.code]!!.height - 2.0,
                        x + currentData[character.code]!!.width - 8.0,
                        y + currentData[character.code]!!.height - 2.0,
                        1.0f
                    )
                }
                x += (currentData[character.code]!!.width - 9 + charOffset).toDouble()
            }
            ++i
        }
        GL11.glPopMatrix()
        return x.toFloat() / 2.0f
    }

    override fun getStringWidth(text: String): Int {
        if (text == null) {
            return 0
        }
        var width = 0
        var currentData = charData
        var bold = false
        var italic = false
        val size = text.length
        var i = 0
        while (i < size) {
            val character = text[i]
            if (character == '\u00a7' && i < size) {
                val colorIndex = "0123456789abcdefklmnor".indexOf(character)
                if (colorIndex < 16) {
                    bold = false
                    italic = false
                } else if (colorIndex == 17) {
                    bold = true
                    currentData = if (italic) boldItalicChars else boldChars
                } else if (colorIndex == 20) {
                    italic = true
                    currentData = if (bold) boldItalicChars else italicChars
                } else if (colorIndex == 21) {
                    bold = false
                    italic = false
                    currentData = charData
                }
                ++i
            } else if (character.code < currentData.size && character >= '\u0000') {
                width += currentData[character.code]!!.width - 9 + charOffset
            }
            ++i
        }
        return width / 2
    }

    override fun setFont(font: Font) {
        super.setFont(font)
        setupBoldItalicIDs()
    }

    override fun setAntiAlias(antiAlias: Boolean) {
        super.setAntiAlias(antiAlias)
        setupBoldItalicIDs()
    }

    override fun setFractionalMetrics(fractionalMetrics: Boolean) {
        super.setFractionalMetrics(fractionalMetrics)
        setupBoldItalicIDs()
    }

    private fun setupBoldItalicIDs() {
        texBold = setupTexture(
            font.deriveFont(1), antiAlias, fractionalMetrics,
            boldChars
        )
        texItalic = setupTexture(
            font.deriveFont(2), antiAlias, fractionalMetrics,
            italicChars
        )
    }

    private fun drawLine(x: Double, y: Double, x1: Double, y1: Double, width: Float) {
        GL11.glDisable(3553)
        GL11.glLineWidth(width)
        GL11.glBegin(1)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x1, y1)
        GL11.glEnd()
        GL11.glEnable(3553)
    }

    private fun setupMinecraftColorcodes() {
        var index = 0
        while (index < 32) {
            val noClue = (index shr 3 and 1) * 85
            var red = (index shr 2 and 1) * 170 + noClue
            var green = (index shr 1 and 1) * 170 + noClue
            var blue = (index shr 0 and 1) * 170 + noClue
            if (index == 6) {
                red += 85
            }
            if (index >= 16) {
                red /= 4
                green /= 4
                blue /= 4
            }
            colorCode[index] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
            ++index
        }
    }
}