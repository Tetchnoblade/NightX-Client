package net.aspw.nightx.features.module.modules.player;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.PacketEvent;
import net.aspw.nightx.event.Render3DEvent;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.features.module.modules.render.Trails;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.utils.timer.MSTimer;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.IntegerValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Blink", category = ModuleCategory.PLAYER)
public class Blink extends Module {

    public final BoolValue pulseValue = new BoolValue("Pulse", true);
    private final LinkedBlockingQueue<Packet> packets = new LinkedBlockingQueue<>();
    private final LinkedList<double[]> positions = new LinkedList<>();
    private final BoolValue c0FValue = new BoolValue("C0FCancel", false);
    private final IntegerValue pulseDelayValue = new IntegerValue("PulseDelay", 400, 10, 5000, "ms");
    private final MSTimer pulseTimer = new MSTimer();
    private EntityOtherPlayerMP fakePlayer = null;
    private boolean disableLogger;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        if (!pulseValue.get()) {
            fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            fakePlayer.clonePlayer(mc.thePlayer, true);
            fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
            fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.addEntityToWorld(-1337, fakePlayer);
        }

        synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (mc.thePlayer.getEyeHeight() / 2), mc.thePlayer.posZ});
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }

        pulseTimer.reset();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null)
            return;

        blink();
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (mc.thePlayer == null || disableLogger)
            return;

        if (packet instanceof C03PacketPlayer) // Cancel all movement stuff
            event.cancelEvent();

        if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook ||
                packet instanceof C08PacketPlayerBlockPlacement ||
                packet instanceof C0APacketAnimation ||
                packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity
                || (c0FValue.get() && packet instanceof C0FPacketConfirmTransaction)) {
            event.cancelEvent();

            packets.add(packet);
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        synchronized (positions) {
            positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ});
        }

        if (pulseValue.get() && pulseTimer.hasTimePassed(pulseDelayValue.get())) {
            blink();
            pulseTimer.reset();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final Trails breadcrumbs = NightX.moduleManager.getModule(Trails.class);
        final Color color = breadcrumbs.colorRainbow.get() ? ColorUtils.rainbow() : new Color(breadcrumbs.colorRedValue.get(), breadcrumbs.colorGreenValue.get(), breadcrumbs.colorBlueValue.get());

        synchronized (positions) {
            glPushMatrix();

            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            mc.entityRenderer.disableLightmap();
            glBegin(GL_LINE_STRIP);
            RenderUtils.glColor(color);
            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;

            for (final double[] pos : positions)
                glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);

            glColor4d(1D, 1D, 1D, 1D);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        }
    }

    @Override
    public String getTag() {
        return String.valueOf(packets.size());
    }

    private void blink() {
        try {
            disableLogger = true;

            while (!packets.isEmpty()) {
                mc.getNetHandler().getNetworkManager().sendPacket(packets.take());
            }

            disableLogger = false;
        } catch (final Exception e) {
            e.printStackTrace();
            disableLogger = false;
        }

        synchronized (positions) {
            positions.clear();
        }
    }
}
