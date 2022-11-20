
package net.aspw.nightx.ui.client.altmanager.menus

import me.liuli.elixir.account.MinecraftAccount
import net.aspw.nightx.ui.client.altmanager.GuiAltManager.Companion.login
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
        val scaledResolution = ScaledResolution(Minecraft.getMinecraft())

        drawDefaultBackground()
        //RenderUtils.drawLoadingCircle((scaledResolution.scaledWidth / 2).toFloat(), (scaledResolution.scaledHeight / 4 + 70).toFloat())
        drawCenteredString(fontRendererObj, "Logging in to account...", width / 2, height / 2 - 60, 16777215)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}