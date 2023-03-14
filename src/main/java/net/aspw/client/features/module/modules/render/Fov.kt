package net.aspw.client.features.module.modules.render

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue

@ModuleInfo(name = "Fov", category = ModuleCategory.RENDER, array = false)
class Fov : Module() {
    val fovValue = FloatValue("FOV", 1.4f, 0f, 1.5f, "x")
}