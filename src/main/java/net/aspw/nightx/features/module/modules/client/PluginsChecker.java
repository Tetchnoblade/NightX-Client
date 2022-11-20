package net.aspw.nightx.features.module.modules.client;

import joptsimple.internal.Strings;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.PacketEvent;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.ClientUtils;
import net.aspw.nightx.utils.timer.TickTimer;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ModuleInfo(name = "PluginsChecker", spacedName = "Plugins Checker", category = ModuleCategory.CLIENT, array = false)
public class PluginsChecker extends Module {

    private final TickTimer tickTimer = new TickTimer();

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("/"));
        tickTimer.reset();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        tickTimer.update();

        if (tickTimer.hasTimePassed(20)) {
            ClientUtils.displayChatMessage("§c>> §cFailed!");
            tickTimer.reset();
            setState(false);
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S3APacketTabComplete) {
            final S3APacketTabComplete s3APacketTabComplete = (S3APacketTabComplete) event.getPacket();

            final List<String> plugins = new ArrayList<>();
            final String[] commands = s3APacketTabComplete.func_149630_c();

            for (final String command1 : commands) {
                final String[] command = command1.split(":");

                if (command.length > 1) {
                    final String pluginName = command[0].replace("/", "");

                    if (!plugins.contains(pluginName))
                        plugins.add(pluginName);
                }
            }

            Collections.sort(plugins);

            if (!plugins.isEmpty())
                ClientUtils.displayChatMessage("§c>> §aPlugins §7(§8" + plugins.size() + "§7): §c" + Strings.join(plugins.toArray(new String[0]), "§7, §c"));
            else
                ClientUtils.displayChatMessage("§c>> §cNo plugins found!");
            setState(false);
            tickTimer.reset();
        }
    }
}