/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * Author: Drew
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer

class GWEN : SpeedMode("GWEN") {
    private val timer = MSTimer()
    private var stage = false

    override fun onUpdate() {}
    override fun onMotion() {}
    override fun onMove(event: MoveEvent) {}

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE && MovementUtils.isMoving()) {
            if (stage) {
                mc.timer.timerSpeed = 1.5F
                if (timer.hasTimePassed(700)) {
                    timer.reset()
                    stage = !stage
                }
            } else {
                mc.timer.timerSpeed = 0.8F
                if (timer.hasTimePassed(400)) {
                    timer.reset()
                    stage = !stage
                }
            }
        }
    }
}