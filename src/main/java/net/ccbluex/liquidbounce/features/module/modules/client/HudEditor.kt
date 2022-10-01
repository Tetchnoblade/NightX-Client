package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner

@ModuleInfo(
    name = "HudEditor",
    description = "",
    category = ModuleCategory.CLIENT,
    forceNoSound = true,
    onlyEnable = true,
    array = false
)
class HudEditor : Module() {
    override fun onEnable() {
        mc.displayGuiScreen(GuiHudDesigner())
    }
}