package net.aspw.nightx.features.module.modules.render

import net.aspw.nightx.NightX
import net.aspw.nightx.event.EventTarget
import net.aspw.nightx.event.PacketEvent
import net.aspw.nightx.features.module.Module
import net.aspw.nightx.features.module.ModuleCategory
import net.aspw.nightx.features.module.ModuleInfo
import net.aspw.nightx.features.module.modules.client.Hud
import net.aspw.nightx.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "LagBack", spacedName = "Lag Back", category = ModuleCategory.RENDER, array = false)
class LagBack : Module() {
    private val longJumpValue = BoolValue("LongJump", value = true)
    private val speedValue = BoolValue("Speed", value = true)
    private val bowLongJumpValue = BoolValue("BowLongJump", value = true)
    private val valueList = arrayOf(longJumpValue, speedValue, bowLongJumpValue)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) {
            for (i in valueList) {
                val module = NightX.moduleManager.getModule(i.name)
                if (module!!.state) {
                    module.state = false
                    NightX.hud.addNotification(
                        net.aspw.nightx.visual.hud.element.elements.Notification(
                            "LagBack ${module.name}.",
                            net.aspw.nightx.visual.hud.element.elements.Notification.Type.WARNING
                        )
                    )
                    if (NightX.moduleManager.getModule(Hud::class.java)?.flagSoundValue!!.get()) {
                        NightX.tipSoundManager.popSound.asyncPlay(90f)
                    }
                }
            }
        }
    }

    init {
        state = true
    }
}