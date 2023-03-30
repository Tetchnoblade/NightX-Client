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
        final net.aspw.client.features.module.modules.exploit.ClientSpoof clientSpoof = Client.moduleManager.getModule(net.aspw.client.features.module.modules.exploit.ClientSpoof.class);

        if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
            try {
                if (packet instanceof C17PacketCustomPayload) {
                    final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if (!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand") && clientSpoof.modeValue.get().equals("Vanilla"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand") && clientSpoof.modeValue.get().equals("LabyMod"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("LMC"));

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand") && clientSpoof.modeValue.get().equals("CheatBreaker"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("CB"));

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand") && clientSpoof.modeValue.get().equals("PvPLounge"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("PLC18"));

                    else if (customPayload.getChannelName().equalsIgnoreCase("MC|Brand") && clientSpoof.modeValue.get().equals("Geyser"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("eyser"));

                    else if ((customPayload.getChannelName().equalsIgnoreCase("REGISTER") || customPayload.getChannelName().equalsIgnoreCase("MC|Brand")) && clientSpoof.modeValue.get().equals("Lunar"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("Lunar-Client"));
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