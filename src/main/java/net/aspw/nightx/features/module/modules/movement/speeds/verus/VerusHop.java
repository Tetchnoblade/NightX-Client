/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.aspw.nightx.features.module.modules.movement.speeds.verus;

import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.utils.MovementUtils;

public class VerusHop extends SpeedMode {

    public VerusHop() {
        super("VerusHop");
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
        if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    MovementUtils.strafe(0.48F);
                }
                MovementUtils.strafe();
            }
        }
    }

    @Override
    public void onDisable() {
        if (!mc.thePlayer.isSneaking())
            MovementUtils.strafe(0.3f);
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
