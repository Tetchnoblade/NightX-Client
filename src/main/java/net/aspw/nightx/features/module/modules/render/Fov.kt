package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.value.FloatValue

@ModuleInfo(name = "Fov", category = ModuleCategory.RENDER, array = false)
class Fov : Module() {
    val fovValue = FloatValue("FOV", 1.4f, 0f, 1.5f, "x")
}