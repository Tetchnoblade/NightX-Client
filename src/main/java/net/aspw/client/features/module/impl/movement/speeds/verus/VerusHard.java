package net.aspw.client.features.module.impl.movement.speeds.verus;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Speed;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.utils.MovementUtils;

public class VerusHard extends SpeedMode {

    public VerusHard() {
        super("VerusHard");
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
        super.onDisable();

        final Scaffold scaffold = Client.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState())
            MovementUtils.strafe(0.2f);
    }

    @Override
    public void onMotion() {
        final Speed speed = Client.moduleManager.getModule(Speed.class);
        if (speed == null)
            return;

        if (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown())
            return;

        mc.timer.timerSpeed = speed.verusTimer.get();

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            if (mc.thePlayer.isSprinting()) {
                MovementUtils.strafe(MovementUtils.getSpeed() + 0.2F);
            }
        }

        MovementUtils.strafe(Math.max((float) MovementUtils.getBaseMoveSpeed(), MovementUtils.getSpeed())); // no sprint = faster - verus, since 2018
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
