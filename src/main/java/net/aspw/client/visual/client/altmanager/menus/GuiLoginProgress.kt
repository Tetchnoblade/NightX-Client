package net.aspw.client.visual.client.altmanager.menus

import me.liuli.elixir.account.MinecraftAccount
import net.aspw.client.visual.client.altmanager.GuiAltManager.Companion.login
import net.minecraft.client.Minecraft
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
        ScaledResolution(Minecraft.getMinecraft())

        drawDefaultBackground()
        drawCenteredString(fontRendererObj, "Logging in...", width / 2, height / 2 - 60, 16777215)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}