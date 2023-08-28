package net.aspw.client.features.api;

import io.netty.buffer.Unpooled;
import net.aspw.client.Client;
import net.aspw.client.event.*;
import net.aspw.client.features.module.impl.combat.KillAura;
import net.aspw.client.features.module.impl.other.ClientSpoof;
import net.aspw.client.features.module.impl.visual.Animations;
import net.aspw.client.features.module.impl.visual.Cape;
import net.aspw.client.util.MinecraftInstance;
import net.aspw.client.util.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.util.Objects;

public class PacketManager extends MinecraftInstance implements Listenable {

    public static int ticks;
    public static String selectedCape;
    public static int swing;

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

        if (mc.thePlayer.ticksExisted % 3 == 0)
            ticks++;

        if (ticks > maxFrames) {
            ticks = 1;
        }
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.getEventState() == EventState.PRE) {
            if (mc.thePlayer.swingProgressInt == 1) {
                swing = 9;
            } else {
                swing = Math.max(0, swing - 1);
            }
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindDrop) && mc.thePlayer.getHeldItem() != null && mc.currentScreen == null)
            mc.thePlayer.isSwingInProgress = true;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        final ClientSpoof clientSpoof = Client.moduleManager.getModule(ClientSpoof.class);
        final KillAura killAura = Objects.requireNonNull(Client.moduleManager.getModule(KillAura.class));

        if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
            if (Objects.requireNonNull(clientSpoof).blockModsCheck.get() && packet instanceof FMLProxyPacket)
                event.cancelEvent();
            if (packet instanceof C17PacketCustomPayload) {
                if (((C17PacketCustomPayload) event.getPacket()).getChannelName().equalsIgnoreCase("MC|Brand")) {
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Vanilla"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("vanilla")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Forge"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("FML")));
                    if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("OptiFine"))
                        PacketUtils.sendPacketNoEvent(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("optifine")));
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

        if (mc.thePlayer == null) return;

        if (Animations.oldAnimations.get() && mc.playerController.curBlockDamageMP == 0F && (mc.thePlayer.isUsingItem() || mc.thePlayer.isEating() || mc.thePlayer.isBlocking()) && killAura.getTarget() == null) {
            if (packet instanceof C0APacketAnimation)
                event.cancelEvent();
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}