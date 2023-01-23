package net.aspw.nightx.visual.client

import net.aspw.nightx.NightX
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.misc.MiscUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.aspw.nightx.visual.client.altmanager.GuiAltManager
import net.aspw.nightx.visual.font.Fonts
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    val kawaiiLogo = ResourceLocation("client/kawaii.png")

    var slideX: Float = 0F
    var fade: Float = 0F

    var sliderX: Float = 0F
    var sliderDarkX: Float = 0F

    var lastAnimTick: Long = 0L
    var alrUpdate = false

    var lastXPos = 0F

    companion object;

    override fun initGui() {
        slideX = 0F
        fade = 0F
        sliderX = 0F
        sliderDarkX = 0F
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!alrUpdate) {
            lastAnimTick = System.currentTimeMillis()
            alrUpdate = true
        }
        val creditInfo = "Welcome, §a${mc.session.username}"
        drawBackground(0)
        GL11.glPushMatrix()
        Fonts.fontSFUI40.drawStringWithShadow(
            NightX.CLIENT_BEST + " Client (#" + NightX.CLIENT_VERSION + ")",
            8F,
            height - 12F,
            -1
        )
        Fonts.fontSFUI40.drawStringWithShadow(
            creditInfo,
            width - 3F - Fonts.fontSFUI40.getStringWidth(creditInfo),
            height - 12F,
            -1
        )
        RenderUtils.drawImage2(kawaiiLogo, width / 1.04F - 49.04F, height / 1.02F - 89.02F, 80, 80)
        GlStateManager.enableAlpha()
        renderBar(mouseX, mouseY, partialTicks)
        GL11.glPopMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isMouseHover(2F, height - 38F, 28F, height - 28F, mouseX, mouseY));

        val staticX = width / 2F - 120F
        val staticY = height / 2F + 20F
        for ((index, icon) in ImageButton.values().withIndex()) {
            if (isMouseHover(
                    staticX + 40F * index,
                    staticY,
                    staticX + 40F * (index + 1),
                    staticY + 20F,
                    mouseX,
                    mouseY
                )
            )
                when (index) {
                    0 -> mc.displayGuiScreen(GuiSelectWorld(this))
                    1 -> mc.displayGuiScreen(GuiMultiplayer(this))
                    2 -> mc.displayGuiScreen(GuiAltManager(this))
                    3 -> mc.displayGuiScreen(GuiOptions(this, this.mc.gameSettings))
                    4 -> MiscUtils.showURL(NightX.CLIENT_DISCORD)
                    5 -> mc.shutdown()
                }

        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    fun renderBar(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val staticX = width / 2F - 120F
        val staticY = height / 2F + 20F

        RenderUtils.drawRoundedRect(
            staticX,
            staticY,
            staticX + 240F,
            staticY + 45F,
            0F,
            (Color(0, 0, 0, 120)).rgb
        )

        var index: Int = 0
        var shouldAnimate = false
        var displayString: String? = null
        var moveX = 0F
        for (icon in ImageButton.values()) {
            if (isMouseHover(
                    staticX + 40F * index,
                    staticY,
                    staticX + 40F * (index + 1),
                    staticY + 20F,
                    mouseX,
                    mouseY
                )
            ) {
                shouldAnimate = true
                displayString = icon.buttonName
                moveX = staticX + 40F * index
            }
            index++
        }

        if (displayString != null)
            Fonts.fontSFUI40.drawCenteredString(displayString, width / 2F, staticY + 30F, -1)
        else
            Fonts.fontSFUI40.drawCenteredString("Aspw-w/NightX-Client", width / 2F, staticY + 30F, -1)

        if (shouldAnimate) {
            slideX = if (fade == 0F)
                moveX
            else
                AnimationUtils.animate(moveX, slideX, 10F * (1F - partialTicks))

            lastXPos = moveX

            fade += 10F
            if (fade >= 100F) fade = 100F
        } else {
            fade -= 10F
            if (fade <= 0F) fade = 0F

            slideX = AnimationUtils.animate(lastXPos, slideX, 10F * (1F - partialTicks))
        }

        if (fade != 0F)
            RenderUtils.drawRoundedRect(
                slideX,
                staticY,
                slideX + 40F,
                staticY + 20F,
                0F,
                (Color(50, 50, 50, 120)).rgb
            )

        index = 0
        GlStateManager.disableAlpha()
        for (i in ImageButton.values()) {
            RenderUtils.drawImage2(i.texture, staticX + 40F * index + 11F, staticY + 1F, 18, 18)
            index++
        }
        GlStateManager.enableAlpha()
    }

    fun isMouseHover(x: Float, y: Float, x2: Float, y2: Float, mouseX: Int, mouseY: Int): Boolean =
        mouseX >= x && mouseX < x2 && mouseY >= y && mouseY < y2

    enum class ImageButton(val buttonName: String, val texture: ResourceLocation) {
        Single("Single Player", ResourceLocation("client/menu/singleplayer.png")),
        Multi("Multi Player", ResourceLocation("client/menu/multiplayer.png")),
        Alts("Alt Manager", ResourceLocation("client/menu/alt.png")),
        Settings("Options", ResourceLocation("client/menu/settings.png")),
        Discord("Discord", ResourceLocation("client/menu/discord.png")),
        Exit("Exit", ResourceLocation("client/menu/exit.png"))
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}