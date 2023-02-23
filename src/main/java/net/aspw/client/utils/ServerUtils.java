package net.aspw.client.utils;

import net.aspw.client.visual.client.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;

public final class ServerUtils extends MinecraftInstance {

    public static ServerData serverData;

    public static void connectToLastServer() {
        if (serverData == null)
            return;

        mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), mc, serverData));
    }

    public static String getRemoteIp() {
        if (mc.theWorld == null) return "Undefined";

        String serverIp = "Singleplayer";

        if (mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if (serverData != null)
                serverIp = serverData.serverIP;
        }

        return serverIp;
    }

    public static boolean isHypixelLobby() {
        if (mc.theWorld == null) return false;

        String target = "CLICK TO PLAY";
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity.getName().startsWith("§e§l")) {
                if (entity.getName().equals("§e§l" + target)) {
                    return true;
                }
            }
        }
        return false;
    }
}