package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.TextEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.file.configs.FriendsConfig;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.misc.StringUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.TextValue;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

@ModuleInfo(name = "Nick", spacedName = "Nick", description = "", category = ModuleCategory.RENDER, array = false)
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
        File skinFile = new File(LiquidBounce.fileManager.dir, "cskin.png");
        if (skinFile.isFile()) {
            try {
                final BufferedImage bufferedImage = ImageIO.read(new FileInputStream(skinFile));

                if (bufferedImage == null)
                    return;

                skinImage = new ResourceLocation(LiquidBounce.CLIENT_FOLDER.toLowerCase() + "/cskin.png");

                mc.getTextureManager().loadTexture(skinImage, new DynamicTexture(bufferedImage));
                ClientUtils.getLogger().info("Loaded custom skin for nick.");
            } catch (final Exception e) {
                ClientUtils.getLogger().error("Failed to load custom skin.", e);
            }
        }
    }

    @EventTarget
    public void onText(final TextEvent event) {
        if (mc.thePlayer == null || event.getText().contains("§c>> §3") || event.getText().startsWith("/") || event.getText().startsWith(LiquidBounce.commandManager.getPrefix() + ""))
            return;

        for (final FriendsConfig.Friend friend : LiquidBounce.fileManager.friendsConfig.getFriends())
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

    public void onInitialize() {
        setState(true);
    }
}
