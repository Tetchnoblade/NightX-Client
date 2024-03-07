package net.aspw.client.visual.client

import net.aspw.client.Launch
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.Access
import net.aspw.client.utils.misc.MiscUtils
import net.aspw.client.utils.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.GuiModList
import org.lwjgl.input.Keyboard

class GuiInfo(private val prevGui: GuiScreen) : GuiScreen() {

    override fun initGui() {
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 44, "Open Website"))
        buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 68, "Join Discord"))
        buttonList.add(GuiButton(3, width / 2 - 100, height / 4 + 92, "Reload Data"))
        buttonList.add(GuiButton(4, width / 2 - 100, height / 4 + 116, "Mod List"))
        buttonList.add(GuiButton(5, width / 2 - 100, height / 4 + 140, "Done"))
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            1 -> MiscUtils.showURL(Launch.CLIENT_WEBSITE)
            2 -> MiscUtils.showURL(Access.discord)
            3 -> {
                Access.checkStatus()
                Access.checkLatestVersion()
                Access.checkStaffList()
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                }
            }

            4 -> {
                mc.displayGuiScreen(GuiModList(this))
            }

            5 -> mc.displayGuiScreen(prevGui)
        }
    }
}