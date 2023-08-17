package net.aspw.client.visual.client

import net.aspw.client.protocol.ViaPatcher
import net.aspw.client.util.render.RenderUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

class GuiProtocolFixer(private val prevGui: GuiScreen) : GuiScreen() {
    private lateinit var ladderFix: GuiButton
    private lateinit var lilyPadFix: GuiButton
    private lateinit var livingBaseFix: GuiButton
    private lateinit var entityFix: GuiButton
    private lateinit var killAuraFix: GuiButton

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        buttonList.add(GuiButton(5, width / 2 - 100, height / 4 - 32, "").also { killAuraFix = it })
        buttonList.add(GuiButton(4, width / 2 - 100, height / 4, "").also { entityFix = it })
        buttonList.add(GuiButton(3, width / 2 - 100, height / 4 + 32, "").also { livingBaseFix = it })
        buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 64, "").also { lilyPadFix = it })
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 96, "").also { ladderFix = it })
        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 144, "Done"))
        updateButtonStat()
    }

    private fun updateButtonStat() {
        ladderFix.displayString = "Fix Ladder: §a" + if (ViaPatcher.ladderFix) "§aEnabled" else "§cDisabled"
        lilyPadFix.displayString = "Fix LilyPad: §a" + if (ViaPatcher.lilyPadFix) "§aEnabled" else "§cDisabled"
        livingBaseFix.displayString = "Fix LivingBase: §a" + if (ViaPatcher.livingBaseFix) "§aEnabled" else "§cDisabled"
        entityFix.displayString = "Fix Entity: §a" + if (ViaPatcher.entityFix) "§aEnabled" else "§cDisabled"
        killAuraFix.displayString = "Fix KillAura: §a" + if (ViaPatcher.killAuraFix) "§aEnabled" else "§cDisabled"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        RenderUtils.drawImage(
            ResourceLocation("client/background/portal.png"), 0, 0,
            width, height
        )
        this.drawCenteredString(mc.fontRendererObj, "Protocol Fixer", width / 2, 12, 0xffffff)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(prevGui)
            1 -> ViaPatcher.ladderFix = !ViaPatcher.ladderFix
            2 -> ViaPatcher.lilyPadFix = !ViaPatcher.lilyPadFix
            3 -> ViaPatcher.livingBaseFix = !ViaPatcher.livingBaseFix
            4 -> ViaPatcher.entityFix = !ViaPatcher.entityFix
            5 -> ViaPatcher.killAuraFix = !ViaPatcher.killAuraFix
        }
        updateButtonStat()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}