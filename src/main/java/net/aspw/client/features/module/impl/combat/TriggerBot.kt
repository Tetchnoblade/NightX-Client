package net.aspw.client.features.module.modules.combat

import net.aspw.client.event.EventTarget
import net.aspw.client.event.Render3DEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.Entityutil.isSelected
import net.aspw.client.util.timing.Timeutil.randomClickDelay
import net.aspw.client.value.IntegerValue
import net.minecraft.client.settings.KeyBinding

@ModuleInfo(
    name = "TriggerBot",
    spacedName = "Trigger Bot",
    description = "",
    category = ModuleCategory.COMBAT
)
object Trigger : Module("Trigger", ModuleCategory.COMBAT) {

    private val maxCPSValue: IntegerValue =
        object : IntegerValue("MaxCPS", 8, 1..20) {
            override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtLeast(minCPS)

            override fun onChanged(oldValue: Int, newValue: Int) {
                delay = randomClickDelay(minCPS, get())
            }
        }
    private val maxCPS by maxCPSValue

    private val minCPS: Int by
        object : IntegerValue("MinCPS", 5, 1..20) {
            override fun onChange(oldValue: Int, newValue: Int) = newValue.coerceAtMost(maxCPS)

            override fun onChanged(oldValue: Int, newValue: Int) {
                delay = randomClickDelay(get(), maxCPS)
            }

            override fun isSupported() = !maxCPSValue.isMinimal()
        }

    private var delay = randomClickDelay(minCPS, maxCPS)
    private var lastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {
        val objectMouseOver = mc.objectMouseOver

        if (
            objectMouseOver != null &&
                System.currentTimeMillis() - lastSwing >= delay &&
                isSelected(objectMouseOver.entityHit, true)
        ) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click handling

            lastSwing = System.currentTimeMillis()
            delay = randomClickDelay(minCPS, maxCPS)
        }
    }
}
