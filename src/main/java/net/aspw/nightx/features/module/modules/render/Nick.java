package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.TextEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.file.configs.FriendsConfig;
import net.aspw.nightx.utils.ClientUtils;
import net.aspw.nightx.utils.misc.StringUtils;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.TextValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

@ModuleInfo(name = "Nick", spacedName = "Nick", category = ModuleCategory.RENDER, array = false)
public class Nick extends Module {

    public final BoolValue selfValue = new BoolValue("Yourself", true);
    public final BoolValue tagValue = new BoolValue("Tag", true);
    public final BoolValue allPlayersValue = new BoolValue("AllPlayers", false);
    public final BoolValue skinProtectValue = new BoolValue("SkinProtect", false);
    public final BoolValue customSkinValue = new BoolValue("CustomSkin", false, () -> skinProtectValue.get());
    private final TextValue fakeNameValue = new TextValue("FakeName", "NightX");
    private final TextValue allFakeNameValue = new TextValue("AllPlayersFakeName", "Censored");
    public ResourceLocation skinImage;

    public Nick() {
        File skinFile = new File(NightX.fileManager.dir, "cskin.png");
        if (skinFile.isFile()) {
            try {
                final BufferedImage bufferedImage = ImageIO.read(new FileInputStream(skinFile));

                if (bufferedImage == null)
                    return;

                skinImage = new ResourceLocation(NightX.CLIENT_FOLDER.toLowerCase() + "/cskin.png");

                mc.getTextureManager().loadTexture(skinImage, new DynamicTexture(bufferedImage));
                ClientUtils.getLogger().info("Loaded custom skin for nick.");
            } catch (final Exception e) {
                ClientUtils.getLogger().error("Failed to load custom skin.", e);
            }
        }
    }

    @EventTarget
    public void onText(final TextEvent event) {
        if (mc.thePlayer == null || event.getText().contains("§f§l[§d§lN§7§lightX§f§l] §3") || event.getText().startsWith("/") || event.getText().startsWith(NightX.commandManager.getPrefix() + ""))
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
