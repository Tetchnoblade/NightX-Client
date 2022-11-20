package net.aspw.nightx.features.command.commands

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.Listenable
import net.aspw.nightx.event.Render2DEvent
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.command.Command
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.utils.render.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

class TacoCommand : Command("taco", emptyArray()), Listenable {
    private var toggle = false
    private var image = 0
    private var running = 0f
    private val tacoTextures = arrayOf(
        ResourceLocation("nightx/taco/1.png"),
        ResourceLocation("nightx/taco/2.png"),
        ResourceLocation("nightx/taco/3.png"),
        ResourceLocation("nightx/taco/4.png"),
        ResourceLocation("nightx/taco/5.png"),
        ResourceLocation("nightx/taco/6.png"),
        ResourceLocation("nightx/taco/7.png"),
        ResourceLocation("nightx/taco/8.png"),
        ResourceLocation("nightx/taco/9.png"),
        ResourceLocation("nightx/taco/10.png"),
        ResourceLocation("nightx/taco/11.png"),
        ResourceLocation("nightx/taco/12.png")
    )

    init {
        NightX.eventManager.registerListener(this)
    }

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        toggle = !toggle
        ClientUtils.displayChatMessage(if (toggle) "§c>> §aTaco Enabled! :)" else "§c>> §cTaco Disabled! :(")
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