package net.aspw.client.features.module.impl.movement.speeds.spectre;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.features.module.impl.player.Scaffold;
import net.aspw.client.util.MovementUtils;
import net.minecraft.util.MathHelper;

/**
 * The type Spectre on ground.
 */
public class SpectreOnGround extends SpeedMode {

    private int speedUp;

    /**
     * Instantiates a new Spectre on ground.
     */
    public SpectreOnGround() {
        super("SpectreOnGround");
    }

    @Override
    public void onMotion() {
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        final Scaffold scaffold = Client.moduleManager.getModule(Scaffold.class);

        if (!mc.thePlayer.isSneaking() && !scaffold.getState()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
        if (!MovementUtils.isMoving() || mc.thePlayer.movementInput.jump)
            return;

        if (speedUp >= 10) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionX = 0D;
                mc.thePlayer.motionZ = 0D;
                speedUp = 0;
            }
            return;
        }

        if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.isKeyDown()) {
            final float f = mc.thePlayer.rotationYaw * 0.017453292F;
            mc.thePlayer.motionX -= MathHelper.sin(f) * 0.145F;
            mc.thePlayer.motionZ += MathHelper.cos(f) * 0.145F;
            event.setX(mc.thePlayer.motionX);
            event.setY(0.005);
            event.setZ(mc.thePlayer.motionZ);

            speedUp++;
        }
    }
}
