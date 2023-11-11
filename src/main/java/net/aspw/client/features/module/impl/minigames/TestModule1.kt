package net.aspw.client.features.module.impl.minigames

import net.aspw.client.event.*
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo

@ModuleInfo(name = "TestModule1", spacedName = "Test Module 1", description = "", category = ModuleCategory.MINIGAMES)
class TestModule1 : Module() {
    override val tag: String
        get() = "Test"

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
    }

    @EventTarget
    fun onClickBlock(event: ClickBlockEvent) {
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
    }
}