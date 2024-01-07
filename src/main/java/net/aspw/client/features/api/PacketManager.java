package net.aspw.client.features.api;

import io.netty.buffer.Unpooled;
import net.aspw.client.Client;
import net.aspw.client.event.*;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.combat.TPAura;
import net.aspw.client.features.module.impl.other.ClientSpoof;
import net.aspw.client.features.module.impl.targets.*;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.Cape;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.util.EntityUtils;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.PacketUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Objects;

public class PacketManager extends MinecraftInstance implements Listenable {

    public static int ticks;
    public static String selectedCape;
    public static int swing;
    public static boolean isVisualBlocking = false;

    public static void update() {
        int maxFrames = 40;

        switch (Objects.requireNonNull(Client.moduleManager.getModule(Cape.class)).getStyleValue().get()) {
            case "Rise5":
                selectedCape = "rise5";
                maxFrames = 14;
                break;
            case "NightX":
                selectedCape = "nightx";
                maxFrames = 14;
                break;
        }

        if (mc.thePlayer.ticksExisted % 2 == 0)
            ticks++;

        if (ticks > maxFrames) {
            ticks = 1;
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        for (Entity en : mc.theWorld.loadedEntityList) {
            if (shouldStopRender(en)) {
                en.renderDistanceWeight = 0.0;
            } else {
                en.renderDistanceWeight = 1.0;
            }
        }
    }

    public static boolean shouldStopRender(Entity entity) {
        return (EntityUtils.isMob(entity) ||
                EntityUtils.isAnimal(entity) ||
                entity instanceof EntityBoat ||
                entity instanceof EntityMinecart ||
                entity instanceof EntityItemFrame ||
                entity instanceof EntityTNTPrimed ||
                entity instanceof EntityArmorStand) &&
                entity != mc.thePlayer && mc.thePlayer.getDistanceToEntity(entity) > 35.0f;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        mc.leftClickCounter = 0;
        if (mc.thePlayer.ticksExisted % 10 == 0) {
            EntityUtils.targetInvisible = Objects.requireNonNull(Client.moduleManager.getModule(Invisible.class)).getState();
            EntityUtils.targetPlayer = Objects.requireNonNull(Client.moduleManager.getModule(Players.class)).getState();
            EntityUtils.targetMobs = Objects.requireNonNull(Client.moduleManager.getModule(Mobs.class)).getState();
            EntityUtils.targetAnimals = Objects.requireNonNull(Client.moduleManager.getModule(Animals.class)).getState();
            EntityUtils.targetDead = Objects.requireNonNull(Client.moduleManager.getModule(Dead.class)).getState();
        }
        if (Animations.swingAnimValue.get().equals("Smooth") && event.getEventState() == EventState.PRE) {
            if (mc.thePlayer.swingProgressInt == 1) {
                swing = 9;
            } else {
                swing = Math.max(0, swing - 1);
            }
        }
        final KillAura killAura = Objects.requireNonNull(Client.moduleManager.getModule(KillAura.class));
        final TPAura tpAura = Objects.requireNonNull(Client.moduleManager.getModule(TPAura.class));
        if (Animations.swingLimitOnlyBlocking.get()) {
            if (mc.thePlayer.swingProgress >= 1f)
                mc.thePlayer.isSwingInProgress = false;
            if (mc.thePlayer.isBlocking() || (killAura.getState() && killAura.getTarget() != null && !killAura.getAutoBlockModeValue().get().equals("None") || tpAura.getState() && tpAura.isBlocking())) {
                if (mc.thePlayer.swingProgress >= Animations.swingLimit.get())
                    mc.thePlayer.isSwingInProgress = false;
            }
        } else if (mc.thePlayer.swingProgress >= Animations.swingLimit.get()) {
            mc.thePlayer.isSwingInProgress = false;
        }
        if (Animations.sigmaHeld.get())
            mc.thePlayer.renderArmPitch = mc.thePlayer.rotationPitch - 30;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        final ClientSpoof clientSpoof = Client.moduleManager.getModule(ClientSpoof.class);

        if (ProtocolBase.getManager().getTargetVersion().isNewerThan(VersionEnum.r1_9_3tor1_9_4)) {
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                ((C08PacketPlayerBlockPlacement) packet).facingX = 0.5F;
                ((C08PacketPlayerBlockPlacement) packet).facingY = 0.5F;
                ((C08PacketPlayerBlockPlacement) packet).facingZ = 0.5F;
            }
        }

        if (!MinecraftInstance.mc.isIntegratedServerRunning()) {
            if (packet instanceof C17PacketCustomPayload) {
                if (((C17PacketCustomPayload) event.getPacket()).getChannelName().equalsIgnoreCase("MC|Brand")) {
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Vanilla"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("vanilla")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Forge"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("FML")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("OptiFine"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("optifine")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Fabric"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("fabric")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("LabyMod"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("LMC")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("CheatBreaker"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("CB")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("PvPLounge"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("PLC18")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Geyser"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("eyser")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Lunar"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer())).writeString("Lunar-Client")));
                }
                event.cancelEvent();
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}