package net.aspw.nightx.features.module.modules.player

import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.UpdateEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.BoolValue
import net.minecraft.client.gui.GuiGameOver

@ModuleInfo(name = "Respawn", category = ModuleCategory.PLAYER)
class Respawn : Module() {

    private val instantValue = BoolValue("Instant", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (if (instantValue.get()) mc.thePlayer.health == 0F || mc.thePlayer.isDead else mc.currentScreen is GuiGameOver
                    && (mc.currentScreen as GuiGameOver).enableButtonsTimer >= 20
        ) {
            mc.thePlayer.respawnPlayer()
            mc.displayGuiScreen(null)
        }
    }
}