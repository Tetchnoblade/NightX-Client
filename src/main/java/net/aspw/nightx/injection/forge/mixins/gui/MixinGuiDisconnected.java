package net.aspw.nightx.injection.forge.mixins.gui;

import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.AltService;
import com.thealtening.api.TheAltening;
import com.thealtening.api.data.AccountData;
import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.protocol.ProtocolCollection;
import me.liuli.elixir.account.CrackedAccount;
import me.liuli.elixir.account.MinecraftAccount;
import net.aspw.nightx.NightX;
import net.aspw.nightx.event.SessionEvent;
import net.aspw.nightx.features.module.modules.client.Hud;
import net.aspw.nightx.utils.ClientUtils;
import net.aspw.nightx.utils.ServerUtils;
import net.aspw.nightx.utils.SessionUtils;
import net.aspw.nightx.utils.misc.RandomUtils;
import net.aspw.nightx.visual.client.GuiProxy;
import net.aspw.nightx.visual.client.altmanager.GuiAltManager;
import net.aspw.nightx.visual.client.altmanager.menus.GuiLoginProgress;
import net.aspw.nightx.visual.client.altmanager.menus.GuiTheAltening;
import net.aspw.nightx.visual.font.Fonts;
import net.minecraft.client.gui.*;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends MixinGuiScreen {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0");

    @Shadow
    private int field_175353_i;

    private GuiButton reconnectButton;
    private GuiSlider viaSlider;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        SessionUtils.handleConnection();

        buttonList.add(reconnectButton = new GuiButton(1, this.width / 2 + -100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 22, 200, 20, "Reconnect to §7" + ServerUtils.serverData.serverIP));

        buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, 100, 20, "Reconnect with Alt"));
        buttonList.add(new GuiButton(4, this.width / 2 + 2, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, 98, 20, "Random Cracked"));
        buttonList.add(new GuiButton(999, width - 218, 7, 98, 20, "Proxy"));
        buttonList.add(new GuiButton(998, width - 320, 7, 98, 20, "Alt Manager"));
        buttonList.add(viaSlider = new GuiSlider(1337, width - 116, 7, 110, 20, "Protocol: ", "", 0, ProtocolCollection.values().length - 1, ProtocolCollection.values().length - 1 - getProtocolIndex(ViaForge.getInstance().getVersion()), false, true,
                guiSlider -> {
                    ViaForge.getInstance().setVersion(ProtocolCollection.values()[ProtocolCollection.values().length - 1 - guiSlider.getValueInt()].getVersion().getVersion());
                    this.updatePortalText();
                }));
        this.updatePortalText();
    }

    private void updatePortalText() {
        if (this.viaSlider == null)
            return;

        this.viaSlider.displayString = "Protocol: " + ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName();
    }

    private int getProtocolIndex(int id) {
        for (int i = 0; i < ProtocolCollection.values().length; i++)
            if (ProtocolCollection.values()[i].getVersion().getVersion() == id)
                return i;
        return -1;
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch (button.id) {
            case 1:
                ServerUtils.connectToLastServer();
                break;
            case 3:
                if (NightX.moduleManager.getModule(Hud.class).getFlagSoundValue().get()) {
                    NightX.tipSoundManager.getPopSound().asyncPlay(90f);
                }
                if (!GuiTheAltening.Companion.getApiKey().isEmpty()) {
                    final String apiKey = GuiTheAltening.Companion.getApiKey();
                    final TheAltening theAltening = new TheAltening(apiKey);

                    try {
                        final AccountData account = theAltening.getAccountData();
                        GuiAltManager.Companion.getAltService().switchService(AltService.EnumAltService.THEALTENING);

                        final YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, ""), Agent.MINECRAFT);
                        yggdrasilUserAuthentication.setUsername(account.getToken());
                        yggdrasilUserAuthentication.setPassword(NightX.CLIENT_BEST);
                        yggdrasilUserAuthentication.logIn();

                        mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "microsoft");
                        NightX.eventManager.callEvent(new SessionEvent());
                        break;
                    } catch (final Throwable throwable) {
                        ClientUtils.getLogger().error("Failed to login into random account from The Altening.", throwable);
                    }
                }

                final List<MinecraftAccount> accounts = NightX.fileManager.accountsConfig.getAccounts();
                if (accounts.isEmpty())
                    break;
                final MinecraftAccount minecraftAccount = accounts.get(new Random().nextInt(accounts.size()));

                mc.displayGuiScreen(new GuiLoginProgress(minecraftAccount, () -> {
                    mc.addScheduledTask(() -> {
                        NightX.eventManager.callEvent(new SessionEvent());
                        ServerUtils.connectToLastServer();
                    });
                    return null;
                }, e -> {
                    mc.addScheduledTask(() -> {
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("text", e.getMessage());

                        mc.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), e.getMessage(), IChatComponent.Serializer.jsonToComponent(jsonObject.toString())));
                    });
                    return null;
                }, () -> null));

                break;
            case 4:
                final CrackedAccount crackedAccount = new CrackedAccount();
                crackedAccount.setName(RandomUtils.randomString(RandomUtils.nextInt(5, 16)));
                crackedAccount.update();
                if (NightX.moduleManager.getModule(Hud.class).getFlagSoundValue().get()) {
                    NightX.tipSoundManager.getPopSound().asyncPlay(90f);
                }
                mc.session = new Session(crackedAccount.getSession().getUsername(), crackedAccount.getSession().getUuid(),
                        crackedAccount.getSession().getToken(), crackedAccount.getSession().getType());
                NightX.eventManager.callEvent(new SessionEvent());
                break;
            case 999:
                mc.displayGuiScreen(new GuiProxy((GuiScreen) (Object) this));
                break;
            case 998:
                mc.displayGuiScreen(new GuiAltManager((GuiScreen) (Object) this));
                break;
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        Fonts.minecraftFont.drawStringWithShadow(
                "§7Username: §a" + mc.getSession().getUsername(),
                6f,
                6f,
                0xffffff);
    }
}