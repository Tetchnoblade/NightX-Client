package net.aspw.client.features.special;

import io.netty.buffer.Unpooled;
import net.aspw.client.Client;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Listenable;
import net.aspw.client.event.PacketEvent;
import net.aspw.client.utils.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class ClientSpoof extends MinecraftInstance implements Listenable {
    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        final net.aspw.client.features.module.modules.client.ClientSpoof clientSpoof = Client.moduleManager.getModule(net.aspw.client.features.module.modules.client.ClientSpoof.class);

        if (!Minecraft.getMinecraft().isIntegratedServerRunning() && clientSpoof.modeValue.get().equals("Vanilla")) {
            try {
                if (packet.getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"))
                    event.cancelEvent();

                if (packet instanceof C17PacketCustomPayload) {
                    final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if (!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (!Minecraft.getMinecraft().isIntegratedServerRunning() && clientSpoof.modeValue.get().equals("LabyMod")) {
            try {
                if (packet.getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"))
                    event.cancelEvent();

                if (packet instanceof C17PacketCustomPayload) {
                    final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if (!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("LMC"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (!Minecraft.getMinecraft().isIntegratedServerRunning() && clientSpoof.modeValue.get().equals("CheatBreaker")) {
            try {
                if (packet.getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"))
                    event.cancelEvent();

                if (packet instanceof C17PacketCustomPayload) {
                    final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if (!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("CB"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (!Minecraft.getMinecraft().isIntegratedServerRunning() && clientSpoof.modeValue.get().equals("Lunar")) {
            try {
                if (packet.getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"))
                    event.cancelEvent();

                if (packet instanceof C17PacketCustomPayload) {
                    final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if (!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();

                    else if (customPayload.getChannelName().equalsIgnoreCase("REGISTER"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("Lunar-Client"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        if (!Minecraft.getMinecraft().isIntegratedServerRunning() && clientSpoof.modeValue.get().equals("PvPLounge")) {
            try {
                if (packet.getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"))
                    event.cancelEvent();

                if (packet instanceof C17PacketCustomPayload) {
                    final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if (!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("PLC18"));
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}