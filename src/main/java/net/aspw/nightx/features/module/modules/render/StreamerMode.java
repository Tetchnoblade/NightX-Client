package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.TextEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.file.configs.FriendsConfig;
import net.aspw.nightx.utils.misc.StringUtils;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.TextValue;
import net.minecraft.client.network.NetworkPlayerInfo;

@ModuleInfo(name = "StreamerMode", spacedName = "Streamer Mode", category = ModuleCategory.RENDER)
public class StreamerMode extends Module {

    public final BoolValue selfValue = new BoolValue("Yourself", true);
    public final BoolValue tagValue = new BoolValue("Tag", true);
    public final BoolValue allPlayersValue = new BoolValue("AllPlayers", false);
    private final TextValue fakeNameValue = new TextValue("FakeName", "NightX");
    private final TextValue allFakeNameValue = new TextValue("AllPlayersFakeName", "Censored");

    public void onInitialize() {
        setState(true);
    }

    @EventTarget
    public void onText(final TextEvent event) {
        if (mc.thePlayer == null || event.getText().contains(NightX.CLIENT_CHAT + "§3") || event.getText().startsWith("/") || event.getText().startsWith(NightX.commandManager.getPrefix() + ""))
            return;

        for (final FriendsConfig.Friend friend : NightX.fileManager.friendsConfig.getFriends())
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
