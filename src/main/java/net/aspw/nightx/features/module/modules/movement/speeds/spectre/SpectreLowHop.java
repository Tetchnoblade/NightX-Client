package net.aspw.nightx.features.module.modules.movement.speeds.spectre;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.features.module.modules.world.Scaffold;
import net.aspw.nightx.utils.MovementUtils;

public class SpectreLowHop extends SpeedMode {

    public SpectreLowHop() {
        super("SpectreLowHop");
    }

    @Override
    public void onMotion() {
        if (!MovementUtils.isMoving() || mc.thePlayer.movementInput.jump)
            return;

        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(1.1F);
            mc.thePlayer.motionY = 0.15D;
            return;
        }

        MovementUtils.strafe();
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = NightX.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState())
            MovementUtils.strafe(0.3f);
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
