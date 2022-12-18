package net.aspw.nightx.features.module.modules.client

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.visual.hud.designer.GuiHudDesigner

@ModuleInfo(
    name = "HudEditor",
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