package net.aspw.client.features.api;

import net.aspw.client.Launch;
import net.aspw.client.event.*;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.combat.KillAuraRecode;
import net.aspw.client.features.module.impl.combat.TPAura;
import net.aspw.client.features.module.impl.other.BrandSpoofer;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.SilentRotations;
import net.aspw.client.utils.MinecraftInstance;
import net.aspw.client.utils.RotationUtils;
import net.aspw.client.utils.pathfinder.MainPathFinder;
import net.aspw.client.utils.pathfinder.Vec3;
import net.aspw.client.utils.render.RenderUtils;
import net.aspw.client.utils.timer.MSTimer;
import net.aspw.client.visual.font.smooth.FontLoaders;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class PacketManager extends MinecraftInstance implements Listenable {

    private static final MSTimer packetCountTimer = new MSTimer();
    public static int swing;
    public static boolean isVisualBlocking = false;
    public static int sendPacketCounts;
    public static int receivePacketCounts;
    private int preSend = 0;
    private int preReceive = 0;
    public static int lastTpX = 0;
    public static int lastTpY = 0;
    public static int lastTpZ = 0;
    public static boolean isRouteTracking = false;
    public static double routeX;
    public static double routeY;
    public static double routeZ;
    private final LinkedList<Vec3> routePositions = new LinkedList<>();
    private static final MSTimer routeTimer = new MSTimer();

    private void findPos(double targetX, double targetY, double targetZ) {
        new Thread(() -> {
            routePositions.clear();
            ArrayList<Vec3> path = MainPathFinder.computePath(
                    new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                    new Vec3(targetX, targetY, targetZ)
            );
            routePositions.addAll(path);
        }).start();
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        if (Objects.requireNonNull(Launch.moduleManager.getModule(SilentRotations.class)).getState())
            RotationUtils.Companion.enableLook();

        isRouteTracking = false;
        if (!routePositions.isEmpty())
            routePositions.clear();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (isRouteTracking)
            FontLoaders.SF20.drawCenteredStringWithShadow("Tracking Pos (X: " + routeX + ", Y: " + routeY + ", Z: " + routeZ + ")", new ScaledResolution(mc).getScaledWidth() / 2F, new ScaledResolution(mc).getScaledHeight() / 2f - 160f, -1);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (isRouteTracking) {
            if (routeTimer.hasTimePassed(500L)) {
                findPos(routeX, routeY, routeZ);
                routeTimer.reset();
            }
            Color color = new Color(255, 255, 255, 180);
            synchronized (routePositions) {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                mc.entityRenderer.disableLightmap();
                GL11.glBegin(GL11.GL_LINE_STRIP);
                RenderUtils.glColor(color);
                double renderPosX = mc.getRenderManager().viewerPosX;
                double renderPosY = mc.getRenderManager().viewerPosY;
                double renderPosZ = mc.getRenderManager().viewerPosZ;
                for (Vec3 pos : routePositions) {
                    GL11.glVertex3d(pos.getX() - renderPosX, pos.getY() - renderPosY, pos.getZ() - renderPosZ);
                }
                GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                GL11.glEnd();
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glPopMatrix();
            }
        } else if (!routePositions.isEmpty())
            routePositions.clear();

        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.leftClickCounter == 0 && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock().getMaterial() != Material.air)
            Launch.eventManager.callEvent(new ClickBlockEvent(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit));

        if (Objects.requireNonNull(Launch.moduleManager.getModule(SilentRotations.class)).getState()) {
            if (RotationUtils.targetRotation != null) {
                mc.thePlayer.prevRenderArmYaw = RotationUtils.targetRotation.getYaw();
                mc.thePlayer.prevRenderArmPitch = RotationUtils.targetRotation.getPitch();
                mc.thePlayer.renderArmYaw = RotationUtils.targetRotation.getYaw();
                mc.thePlayer.renderArmPitch = RotationUtils.targetRotation.getPitch();
            } else {
                mc.thePlayer.prevRenderArmYaw = RotationUtils.prevCameraArmYaw;
                mc.thePlayer.prevRenderArmPitch = RotationUtils.prevCameraArmPitch;
                mc.thePlayer.renderArmYaw = RotationUtils.cameraArmYaw;
                mc.thePlayer.renderArmPitch = RotationUtils.cameraArmPitch;
            }
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        mc.leftClickCounter = 0;

        if (!Objects.requireNonNull(Launch.moduleManager.getModule(SilentRotations.class)).getState())
            Objects.requireNonNull(Launch.moduleManager.getModule(SilentRotations.class)).setState(true);
        if (!Objects.requireNonNull(Launch.moduleManager.getModule(BrandSpoofer.class)).getState())
            Objects.requireNonNull(Launch.moduleManager.getModule(BrandSpoofer.class)).setState(true);

        if ((Animations.swingAnimValue.get().equals("Smooth") || Animations.swingAnimValue.get().equals("Dash")) && event.getEventState() == EventState.PRE) {
            if (mc.thePlayer.swingProgressInt == 1) {
                swing = 9;
            } else {
                swing = Math.max(0, swing - 1);
            }
        }

        final KillAura killAura = Objects.requireNonNull(Launch.moduleManager.getModule(KillAura.class));
        final TPAura tpAura = Objects.requireNonNull(Launch.moduleManager.getModule(TPAura.class));
        final KillAuraRecode killAuraRecode = Objects.requireNonNull(Launch.moduleManager.getModule(KillAuraRecode.class));

        if (Animations.swingLimitOnlyBlocking.get()) {
            if (mc.thePlayer.swingProgress >= 1f)
                mc.thePlayer.isSwingInProgress = false;
            if (mc.thePlayer.isBlocking() || (killAura.getState() && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None") || tpAura.getState() && tpAura.isBlocking() || killAuraRecode.getState() && killAuraRecode.isBlocking())) {
                if (mc.thePlayer.swingProgress >= Animations.swingLimit.get())
                    mc.thePlayer.isSwingInProgress = false;
            }
        } else if (mc.thePlayer.swingProgress >= Animations.swingLimit.get()) {
            mc.thePlayer.isSwingInProgress = false;
        }
    }

    @EventTarget
    public void onTeleport(TeleportEvent event) {
        lastTpX = (int) event.getPosX();
        lastTpY = (int) event.getPosY();
        lastTpZ = (int) event.getPosZ();

        if (RotationUtils.targetRotation != null) {
            RotationUtils.targetRotation.setYaw(event.getYaw());
            RotationUtils.targetRotation.setPitch(event.getPitch());
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet.toString().startsWith("net.minecraft.network.play.client.C"))
            preSend++;
        if (packet.toString().startsWith("net.minecraft.network.play.server.S"))
            preReceive++;

        if (packetCountTimer.hasTimePassed(1000L)) {
            sendPacketCounts = preSend;
            receivePacketCounts = preReceive;
            preSend = 0;
            preReceive = 0;
            packetCountTimer.reset();
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}