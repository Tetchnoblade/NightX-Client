package net.aspw.client.visual.client.altmanager.menus

import net.aspw.client.auth.account.MinecraftAccount
import net.aspw.client.utils.MinecraftInstance
import net.aspw.client.visual.client.altmanager.GuiAltManager.Companion.login
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution

class GuiLoginProgress(
    minecraftAccount: MinecraftAccount,
    success: () -> Unit,
    error: (Exception) -> Unit,
    done: () -> Unit
) : GuiScreen() {

    init {
        login(minecraftAccount, success, error, done)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        ScaledResolution(MinecraftInstance.mc)
        drawDefaultBackground()
        drawCenteredString(mc.fontRendererObj, "Logging in...", width / 2, height / 2 - 60, 16777215)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}