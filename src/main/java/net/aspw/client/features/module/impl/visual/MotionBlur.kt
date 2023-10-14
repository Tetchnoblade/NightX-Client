package net.aspw.client.features.module.impl.visual

import net.aspw.client.event.EventTarget
import net.aspw.client.event.TickEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.IntegerValue
import net.minecraft.util.ResourceLocation

@ModuleInfo(
    name = "MotionBlur",
    spacedName = "Motion Blur",
    description = "",
    category = ModuleCategory.VISUAL
)
class MotionBlur : Module() {
    private val blurAmount = IntegerValue("Amount", 7, 1, 10)

    override fun onDisable() {
        if (mc.entityRenderer.isShaderActive) mc.entityRenderer.stopUseShader()
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        try {
            if (mc.thePlayer != null) {
                if (mc.entityRenderer.shaderGroup == null) mc.entityRenderer.loadShader(
                    ResourceLocation(
                        "minecraft",
                        "shaders/post/motion_blur.json"
                    )
                )
                val uniform = 1f - (blurAmount.get() / 10f).coerceAtMost(0.9f)
                if (mc.entityRenderer.shaderGroup != null) {
                    mc.entityRenderer.shaderGroup.listShaders[0].shaderManager.getShaderUniform("Phosphor")
                        .set(uniform, 0f, 0f)
                }
            }
        } catch (a: Exception) {
            a.printStackTrace()
        }
    }
}