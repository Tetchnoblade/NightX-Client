package net.aspw.client.features.module.modules.visual;

import net.aspw.client.Client;
import net.aspw.client.config.configs.FriendsConfig;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.TextEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.utils.misc.StringUtils;
import net.aspw.client.utils.render.ColorUtils;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.TextValue;
import net.minecraft.client.network.NetworkPlayerInfo;

@ModuleInfo(name = "StreamerMode", spacedName = "Streamer Mode", category = ModuleCategory.VISUAL)
public class StreamerMode extends Module {

    public final BoolValue selfValue = new BoolValue("Yourself", true);
    public final BoolValue tagValue = new BoolValue("Tag", false);
    public final BoolValue allPlayersValue = new BoolValue("AllPlayers", false);
    private final TextValue fakeNameValue = new TextValue("FakeName", "User");
    private final TextValue allFakeNameValue = new TextValue("AllPlayersFakeName", "Censored");

    @EventTarget
    public void onText(final TextEvent event) {
        if (mc.thePlayer == null || event.getText().contains(Client.CLIENT_CHAT + "§3") || event.getText().startsWith("/") || event.getText().startsWith(Client.commandManager.getPrefix() + ""))
            return;

        for (final FriendsConfig.Friend friend : Client.fileManager.friendsConfig.getFriends())
            event.setText(StringUtils.replace(event.getText(), friend.getPlayerName(), ColorUtils.translateAlternateColorCodes(friend.getAlias()) + "§f"));

        event.setText(StringUtils.replace(
                event.getText(),
                mc.thePlayer.getName(),
                (selfValue.get() ? (tagValue.get() ? StringUtils.injectAirString(mc.thePlayer.getName()) + " §7(§a" + ColorUtils.translateAlternateColorCodes(fakeNameValue.get() + "§7)") : ColorUtils.translateAlternateColorCodes(fakeNameValue.get()) + "§r") : mc.thePlayer.getName())
        ));

        if (allPlayersValue.get())
            for (final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap())
                event.setText(StringUtils.replace(event.getText(), playerInfo.getGameProfile().getName(), ColorUtils.translateAlternateColorCodes(allFakeNameValue.get()) + "§f"));
    }
}
