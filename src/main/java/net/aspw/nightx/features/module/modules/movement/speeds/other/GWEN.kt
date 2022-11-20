package net.aspw.nightx.features.module.modules.movement.speeds.other

import net.aspw.nightx.event.EventState
import net.aspw.nightx.event.MotionEvent
import net.aspw.nightx.event.MoveEvent
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode
import net.aspw.nightx.utils.MovementUtils
import net.aspw.nightx.utils.timer.MSTimer

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