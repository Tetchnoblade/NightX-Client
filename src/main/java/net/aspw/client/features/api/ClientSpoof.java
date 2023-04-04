package net.aspw.client.features.api;

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

import java.util.Objects;

public class ClientSpoof extends MinecraftInstance implements Listenable {
    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        final net.aspw.client.features.module.impl.exploit.ClientSpoof clientSpoof = Client.moduleManager.getModule(net.aspw.client.features.module.impl.exploit.ClientSpoof.class);

        if (!Minecraft.getMinecraft().isIntegratedServerRunning() && packet instanceof C17PacketCustomPayload) {
            try {
                final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Vanilla"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Forge"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("FML"));
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("LabyMod"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("LMC"));
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("CheatBreaker"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("CB"));
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("PvPLounge"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("PLC18"));
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Geyser"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("eyser"));
                if (Objects.requireNonNull(clientSpoof).modeValue.get().equals("Lunar"))
                    customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("lunarclient:ddec45f"));
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