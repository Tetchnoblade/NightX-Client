package net.aspw.client.features.module.impl.movement.speeds.verus;

import net.aspw.client.Launch;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.utils.MovementUtils;
import net.minecraft.potion.Potion;

/**
 * The type Verus low hop.
 */
public class VerusLowHop extends SpeedMode {

    /**
     * Instantiates a new Verus low hop.
     */
    public VerusLowHop() {
        super("VerusLowHop");
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = Launch.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState())
            MovementUtils.strafe(0.2f);
    }

    @Override
    public void onMove(MoveEvent event) {
        if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava() && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0;
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MovementUtils.strafe(0.69F);
                    } else {
                        MovementUtils.strafe(0.61F);
                    }
                    event.setY(0.41999998688698);
                }
                MovementUtils.strafe();
            }
        }
    }
}
