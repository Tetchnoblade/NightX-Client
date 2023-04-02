package net.aspw.client.features.command.impl

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.Listenable
import net.aspw.client.event.Render2DEvent
import net.aspw.client.event.UpdateEvent
import net.aspw.client.features.command.Command
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.render.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

class TacoCommand : Command("taco", emptyArray()), Listenable {
    private var toggle = false
    private var image = 0
    private var running = 0f
    private val tacoTextures = arrayOf(
        ResourceLocation("client/taco/1.png"),
        ResourceLocation("client/taco/2.png"),
        ResourceLocation("client/taco/3.png"),
        ResourceLocation("client/taco/4.png")
    )

    init {
        Client.eventManager.registerListener(this)
    }

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        toggle = !toggle
        ClientUtils.displayChatMessage(if (toggle) Client.CLIENT_CHAT + "§aTaco Enabled! :)" else Client.CLIENT_CHAT + "§cTaco Disabled! :(")
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!toggle)
            return

        running += 0.15f * RenderUtils.deltaTime
        val scaledResolution = ScaledResolution(mc)
        RenderUtils.drawImage(tacoTextures[image], running.toInt(), scaledResolution.scaledHeight - 60, 64, 32)
        if (scaledResolution.scaledWidth <= running)
            running = -64f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!toggle) {
            image = 0
            return
        }

        image++
        if (image >= tacoTextures.size) image = 0
    }

    override fun handleEvents() = true

    override fun tabComplete(args: Array<String>): List<String> {
        return listOf("TACO")
    }
}