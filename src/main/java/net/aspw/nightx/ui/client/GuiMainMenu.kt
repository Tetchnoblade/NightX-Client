package net.aspw.nightx.ui.client

import net.aspw.nightx.NightX
import net.aspw.nightx.ui.client.altmanager.GuiAltManager
import net.aspw.nightx.ui.font.Fonts
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.utils.misc.MiscUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.GuiModList
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.concurrent.thread

class GuiMainMenu : GuiScreen(), GuiYesNoCallback {

    var slideX: Float = 0F
    var fade: Float = 0F

    var sliderX: Float = 0F
    var sliderDarkX: Float = 0F

    var lastAnimTick: Long = 0L
    var alrUpdate = false

    var lastXPos = 0F

    var extendedModMode = false
    var extendedBackgroundMode = false

    companion object {
    }

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
            NightX.CLIENT_BEST + " Client",
            2F,
            height - 12F,
            -1
        )
        Fonts.fontSFUI40.drawStringWithShadow(
            creditInfo,
            width - 3F - Fonts.fontSFUI40.getStringWidth(creditInfo),
            height - 12F,
            -1
        )
        GlStateManager.enableAlpha()
        renderBar(mouseX, mouseY, partialTicks)
        GL11.glPopMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isMouseHover(2F, height - 38F, 28F, height - 28F, mouseX, mouseY));

        val staticX = width / 2F - 120F
        val staticY = height / 2F + 20F
        var index: Int = 0
        for (icon in if (extendedModMode) ExtendedImageButton.values() else ImageButton.values()) {
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
                    0 -> if (extendedBackgroundMode) extendedBackgroundMode =
                        false else if (extendedModMode) extendedModMode = false else mc.displayGuiScreen(
                        GuiSelectWorld(
                            this
                        )
                    )

                    1 -> if (extendedBackgroundMode) GuiBackground.enabled =
                        !GuiBackground.enabled else if (extendedModMode) mc.displayGuiScreen(GuiModList(this)) else mc.displayGuiScreen(
                        GuiMultiplayer(this)
                    )

                    2 -> if (extendedModMode) mc.displayGuiScreen(GuiScripts(this)) else mc.displayGuiScreen(
                        GuiAltManager(this)
                    )

                    3 -> if (extendedBackgroundMode) {
                        val file = MiscUtils.openFileChooser() ?: return
                        if (file.isDirectory) return

                    } else if (extendedModMode) {
                        val rpc = NightX.clientRichPresence
                        rpc.showRichPresenceValue = when (val state = !rpc.showRichPresenceValue) {
                            false -> {
                                rpc.shutdown()
                                false
                            }

                            true -> {
                                var value = true
                                thread {
                                    value = try {
                                        rpc.setup()
                                        true
                                    } catch (throwable: Throwable) {
                                        ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
                                        false
                                    }
                                }
                                value
                            }
                        }
                    } else mc.displayGuiScreen(GuiOptions(this, this.mc.gameSettings))

                    4 -> if (extendedBackgroundMode) {
                        NightX.background = null
                    } else if (extendedModMode) extendedBackgroundMode = true else extendedModMode = true

                    5 -> mc.shutdown()
                }

            index++
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
        if (extendedModMode) {
            if (extendedBackgroundMode)
                for (icon in ExtendedBackgroundButton.values()) {
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
                        displayString = if (icon == ExtendedBackgroundButton.Enabled)
                            "Background: ${if (GuiBackground.enabled) "§aEnabled" else "§cDisabled"}"
                        else
                            icon.buttonName
                        moveX = staticX + 40F * index
                    }
                    index++
                }
            else
                for (icon in ExtendedImageButton.values()) {
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
                        displayString =
                            if (icon == ExtendedImageButton.DiscordRPC) "${icon.buttonName}: ${if (NightX.clientRichPresence.showRichPresenceValue) "§aEnabled" else "§cDisabled"}" else icon.buttonName
                        moveX = staticX + 40F * index
                    }
                    index++
                }
        } else
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
            Fonts.fontSFUI40.drawCenteredString("Build: §a" + NightX.CLIENT_VERSION, width / 2F, staticY + 30F, -1)

        if (shouldAnimate) {
            if (fade == 0F)
                slideX = moveX
            else
                slideX = AnimationUtils.animate(moveX, slideX, 10F * (1F - partialTicks))

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
        if (extendedModMode) {
            if (extendedBackgroundMode)
                for (i in ExtendedBackgroundButton.values()) {
                    RenderUtils.drawImage2(i.texture, staticX + 40F * index + 11F, staticY + 1F, 18, 18)
                    index++
                }
            else
                for (i in ExtendedImageButton.values()) {
                    RenderUtils.drawImage2(i.texture, staticX + 40F * index + 11F, staticY + 1F, 18, 18)
                    index++
                }
        } else
            for (i in ImageButton.values()) {
                RenderUtils.drawImage2(i.texture, staticX + 40F * index + 11F, staticY + 1F, 18, 18)
                index++
            }
        GlStateManager.enableAlpha()
    }

    fun isMouseHover(x: Float, y: Float, x2: Float, y2: Float, mouseX: Int, mouseY: Int): Boolean =
        mouseX >= x && mouseX < x2 && mouseY >= y && mouseY < y2

    enum class ImageButton(val buttonName: String, val texture: ResourceLocation) {
        Single("Single Player", ResourceLocation("nightx/menu/singleplayer.png")),
        Multi("Multi Player", ResourceLocation("nightx/menu/multiplayer.png")),
        Alts("Alt Manager", ResourceLocation("nightx/menu/alt.png")),
        Settings("Options", ResourceLocation("nightx/menu/settings.png")),
        Mods("Other", ResourceLocation("nightx/menu/mods.png")),
        Exit("Quit", ResourceLocation("nightx/menu/exit.png"))
    }

    enum class ExtendedImageButton(val buttonName: String, val texture: ResourceLocation) {
        Back("Done", ResourceLocation("nightx/clickgui/back.png")),
        Mods("Mods", ResourceLocation("nightx/menu/mods.png")),
        Scripts("Scripts", ResourceLocation("nightx/clickgui/docs.png")),
        DiscordRPC("Discord RPC", ResourceLocation("nightx/menu/discord.png")),
        Background("Gui", ResourceLocation("nightx/menu/wallpaper.png")),
        Exit("Quit", ResourceLocation("nightx/menu/exit.png"))
    }

    enum class ExtendedBackgroundButton(val buttonName: String, val texture: ResourceLocation) {
        Back("Done", ResourceLocation("nightx/clickgui/back.png")),
        Enabled("Background", ResourceLocation("nightx/notification/checkmark.png")),
        Scripts("Scripts", ResourceLocation("nightx/clickgui/docs.png")),
        Upload("Upload", ResourceLocation("nightx/clickgui/import.png")),
        Reload("Reload", ResourceLocation("nightx/clickgui/reload.png")),
        Exit("Quit", ResourceLocation("nightx/menu/exit.png"))
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}
}