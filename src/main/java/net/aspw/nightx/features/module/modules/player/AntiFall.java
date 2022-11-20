package net.aspw.nightx.features.module.modules.player;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.*;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.features.module.modules.movement.Flight;
import net.aspw.nightx.features.module.modules.world.Scaffold;
import net.aspw.nightx.utils.MovementUtils;
import net.aspw.nightx.utils.PacketUtils;
import net.aspw.nightx.utils.block.BlockUtils;
import net.aspw.nightx.utils.misc.NewFallingPlayer;
import net.aspw.nightx.utils.misc.RandomUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "AntiFall", spacedName = "Anti Fall", category = ModuleCategory.PLAYER)
public class AntiFall extends Module {

    public final ListValue voidDetectionAlgorithm = new ListValue("Detect-Method", new String[]{"Collision", "Predict"}, "Collision");
    public final ListValue setBackModeValue = new ListValue("SetBack-Mode", new String[]{"Teleport", "FlyFlag", "IllegalPacket", "IllegalTeleport", "StopMotion", "Position", "Edit", "SpoofBack"}, "FlyFlag");
    public final IntegerValue maxFallDistSimulateValue = new IntegerValue("Predict-CheckFallDistance", 255, 0, 255, "m", () -> voidDetectionAlgorithm.get().equalsIgnoreCase("predict"));
    public final IntegerValue maxFindRangeValue = new IntegerValue("Predict-MaxFindRange", 60, 0, 255, "m", () -> voidDetectionAlgorithm.get().equalsIgnoreCase("predict"));
    public final IntegerValue illegalDupeValue = new IntegerValue("Illegal-Dupe", 1, 1, 5, "x", () -> setBackModeValue.get().toLowerCase().contains("illegal"));
    public final FloatValue setBackFallDistValue = new FloatValue("Max-FallDistance", 5F, 0F, 255F, "m");
    public final BoolValue resetFallDistanceValue = new BoolValue("Reset-FallDistance", false);
    public final BoolValue renderTraceValue = new BoolValue("Render-Trace", false);
    public final BoolValue scaffoldValue = new BoolValue("AutoScaffold", false);
    public final BoolValue noFlyValue = new BoolValue("NoFlight", true);
    private final LinkedList<double[]> positions = new LinkedList<>();
    private BlockPos detectedLocation = BlockPos.ORIGIN;
    private double lastX = 0;
    private double lastY = 0;
    private double lastZ = 0;
    private double lastFound = 0;
    private boolean shouldRender, shouldStopMotion, shouldEdit = false;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight.class).getState())
            return;

        detectedLocation = null;

        if (voidDetectionAlgorithm.get().equalsIgnoreCase("collision")) {
            if (mc.thePlayer.onGround && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) instanceof BlockAir)) {
                lastX = mc.thePlayer.prevPosX;
                lastY = mc.thePlayer.prevPosY;
                lastZ = mc.thePlayer.prevPosZ;
            }

            shouldRender = renderTraceValue.get() && !MovementUtils.isBlockUnder();

            shouldStopMotion = false;
            shouldEdit = false;
            if (!MovementUtils.isBlockUnder()) {
                if (mc.thePlayer.fallDistance >= setBackFallDistValue.get()) {
                    shouldStopMotion = true;
                    switch (setBackModeValue.get()) {
                        case "IllegalTeleport":
                            mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                        case "IllegalPacket":
                            for (int i = 0; i < illegalDupeValue.get(); i++)
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E+159, mc.thePlayer.posZ, false));
                            break;
                        case "Teleport":
                            mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                            break;
                        case "FlyFlag":
                            mc.thePlayer.motionY = 0F;
                            break;
                        case "StopMotion":
                            float oldFallDist = mc.thePlayer.fallDistance;
                            mc.thePlayer.motionY = 0F;
                            mc.thePlayer.fallDistance = oldFallDist;
                            break;
                        case "Position":
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextDouble(6D, 10D), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                            break;
                        case "Edit":
                        case "SpoofBack":
                            shouldEdit = true;
                            break;
                    }
                    if (resetFallDistanceValue.get() && !setBackModeValue.get().equalsIgnoreCase("StopMotion"))
                        mc.thePlayer.fallDistance = 0;

                    if (scaffoldValue.get() && !NightX.moduleManager.getModule(Scaffold.class).getState())
                        NightX.moduleManager.getModule(Scaffold.class).setState(true);
                }
            }
        } else {
            if (mc.thePlayer.onGround && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)) instanceof BlockAir)) {
                lastX = mc.thePlayer.prevPosX;
                lastY = mc.thePlayer.prevPosY;
                lastZ = mc.thePlayer.prevPosZ;
            }

            shouldStopMotion = false;
            shouldEdit = false;
            shouldRender = false;

            if (!mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater()) {
                NewFallingPlayer NewFallingPlayer = new NewFallingPlayer(mc.thePlayer);

                try {
                    detectedLocation = NewFallingPlayer.findCollision(maxFindRangeValue.get());
                } catch (Exception e) {
                    // do nothing. i hate errors
                }

                if (detectedLocation != null && Math.abs(mc.thePlayer.posY - detectedLocation.getY()) +
                        mc.thePlayer.fallDistance <= maxFallDistSimulateValue.get()) {
                    lastFound = mc.thePlayer.fallDistance;
                }

                shouldRender = renderTraceValue.get() && detectedLocation == null;

                if (mc.thePlayer.fallDistance - lastFound > setBackFallDistValue.get()) {
                    shouldStopMotion = true;
                    switch (setBackModeValue.get()) {
                        case "IllegalTeleport":
                            mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                        case "IllegalPacket":
                            for (int i = 0; i < illegalDupeValue.get(); i++)
                                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1E+159, mc.thePlayer.posZ, false));
                            break;
                        case "Teleport":
                            mc.thePlayer.setPositionAndUpdate(lastX, lastY, lastZ);
                            break;
                        case "FlyFlag":
                            mc.thePlayer.motionY = 0F;
                            break;
                        case "StopMotion":
                            float oldFallDist = mc.thePlayer.fallDistance;
                            mc.thePlayer.motionY = 0F;
                            mc.thePlayer.fallDistance = oldFallDist;
                            break;
                        case "Position":
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + RandomUtils.nextDouble(6D, 10D), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                            break;
                        case "Edit":
                        case "SpoofBack":
                            shouldEdit = true;
                            break;
                    }
                    if (resetFallDistanceValue.get() && !setBackModeValue.get().equalsIgnoreCase("StopMotion"))
                        mc.thePlayer.fallDistance = 0;

                    if (scaffoldValue.get() && !NightX.moduleManager.getModule(Scaffold.class).getState())
                        NightX.moduleManager.getModule(Scaffold.class).setState(true);

                    /*if (towerValue.get() && !LiquidBounce.moduleManager.getModule(Tower.class).getState())
                        LiquidBounce.moduleManager.getModule(Tower.class).setState(true);*/
                }
            }
        }

        if (shouldRender) synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }
        else synchronized (positions) {
            positions.clear();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight.class).getState())
            return;

        if (setBackModeValue.get().equalsIgnoreCase("StopMotion") && event.getPacket() instanceof S08PacketPlayerPosLook)
            mc.thePlayer.fallDistance = 0;

        if (setBackModeValue.get().equalsIgnoreCase("Edit") && shouldEdit && event.getPacket() instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
            packetPlayer.y += 100D;
            shouldEdit = false;
        }

        if (setBackModeValue.get().equalsIgnoreCase("SpoofBack") && shouldEdit && event.getPacket() instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
            packetPlayer.x = lastX;
            packetPlayer.y = lastY;
            packetPlayer.z = lastZ;
            packetPlayer.setMoving(false);
            shouldEdit = false;
        }
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight.class).getState())
            return;

        if (setBackModeValue.get().equalsIgnoreCase("StopMotion") && shouldStopMotion) {
            event.zero();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (noFlyValue.get() && NightX.moduleManager.getModule(Flight.class).getState())
            return;

        if (shouldRender) synchronized (positions) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            mc.entityRenderer.disableLightmap();
            glLineWidth(1F);
            glBegin(GL_LINE_STRIP);
            glColor4f(1F, 1F, 0.1F, 1F);
            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;

            for (final double[] pos : positions)
                glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);

            glColor4d(1, 1, 1, 1);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }

    @Override
    public void onDisable() {
        reset();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    private void reset() {
        detectedLocation = null;
        lastX = lastY = lastZ = lastFound = 0;
        shouldStopMotion = shouldRender = false;
        synchronized (positions) {
            positions.clear();
        }
    }

}