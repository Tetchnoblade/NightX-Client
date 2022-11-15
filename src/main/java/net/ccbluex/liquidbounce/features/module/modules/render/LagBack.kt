package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "LagBack", spacedName = "Lag Back", category = ModuleCategory.RENDER, array = false)
class LagBack : Module() {
    private val longJumpValue = BoolValue("LongJump", value = true)
    private val speedValue = BoolValue("Speed", value = true)
    private val bowLongJumpValue = BoolValue("BowLongJump", value = true)
    private val valueList = arrayOf(longJumpValue,speedValue,bowLongJumpValue)
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(event.packet is S08PacketPlayerPosLook) {
            for(i in valueList) {
                val module = LiquidBounce.moduleManager.getModule(i.name);
                if(module!!.state) {
                    module.state = false
                    LiquidBounce.hud.addNotification(
                        net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification(
                            "LagBack ${module.name}.",
                            net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification.Type.WARNING
                        )
                    )
                }
            }
        }
    }
    init {
        state = true
    }
}