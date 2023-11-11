package net.aspw.client.injection.forge.mixins.gui;

import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.AltService;
import com.thealtening.api.TheAltening;
import com.thealtening.api.data.AccountData;
import net.aspw.client.Client;
import net.aspw.client.auth.account.CrackedAccount;
import net.aspw.client.auth.account.MinecraftAccount;
import net.aspw.client.event.SessionEvent;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.protocol.Protocol;
import net.aspw.client.util.ClientUtils;
import net.aspw.client.util.ServerUtils;
import net.aspw.client.util.SessionUtils;
import net.aspw.client.util.misc.RandomUtils;
import net.aspw.client.visual.client.GuiMainMenu;
import net.aspw.client.visual.client.GuiProxyManager;
import net.aspw.client.visual.client.altmanager.GuiAltManager;
import net.aspw.client.visual.client.altmanager.menus.GuiLoginProgress;
import net.aspw.client.visual.client.altmanager.menus.GuiTheAltening;
import net.aspw.client.visual.font.semi.Fonts;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * The type Mixin gui disconnected.
 */
@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends MixinGuiScreen {

    @Shadow
    private int field_175353_i;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        SessionUtils.handleConnection();

        buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 22, 200, 20, "Reconnect to §7" + ServerUtils.serverData.serverIP));
        buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, 100, 20, "Reconnect with Alt"));
        buttonList.add(new GuiButton(4, this.width / 2 + 2, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 44, 98, 20, "Random Cracked"));
        buttonList.add(new GuiButton(998, width - 94, 5, 88, 20, "Alt Manager"));
        buttonList.add(new GuiButton(1000, 4, height - 24, 68, 20, "Proxy"));
        buttonList.add(Protocol.getAsyncVersionSlider());
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch (button.id) {
            case 1:
                ServerUtils.connectToLastServer();
                break;
            case 3:
                if (!GuiTheAltening.Companion.getApiKey().isEmpty()) {
                    final String apiKey = GuiTheAltening.Companion.getApiKey();
                    final TheAltening theAltening = new TheAltening(apiKey);

                    try {
                        final AccountData account = theAltening.getAccountData();
                        GuiAltManager.Companion.getAltService().switchService(AltService.EnumAltService.THEALTENING);

                        final YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, ""), Agent.MINECRAFT);
                        yggdrasilUserAuthentication.setUsername(account.getToken());
                        yggdrasilUserAuthentication.setPassword(Client.CLIENT_BEST);
                        yggdrasilUserAuthentication.logIn();

                        mc.session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");
                        Client.eventManager.callEvent(new SessionEvent());
                        ServerUtils.connectToLastServer();
                        break;
                    } catch (final Throwable throwable) {
                        ClientUtils.getLogger().error("Failed to login into random account from TheAltening.", throwable);
                    }
                }

                final List<MinecraftAccount> accounts = Client.fileManager.accountsConfig.getAccounts();
                if (accounts.isEmpty())
                    break;
                final MinecraftAccount minecraftAccount = accounts.get(new Random().nextInt(accounts.size()));

                mc.displayGuiScreen(new GuiLoginProgress(minecraftAccount, () -> {
                    mc.addScheduledTask(() -> {
                        Client.eventManager.callEvent(new SessionEvent());
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
                final Interface anInterface = Objects.requireNonNull(Client.moduleManager.getModule(Interface.class));
                crackedAccount.setName(RandomUtils.randomString(RandomUtils.nextInt(5, 16)));
                crackedAccount.update();
                if (anInterface.getFlagSoundValue().get()) {
                    Client.tipSoundManager.getPopSound().asyncPlay(Client.moduleManager.getPopSoundPower());
                }
                mc.session = new Session(crackedAccount.getSession().getUsername(), crackedAccount.getSession().getUuid(),
                        crackedAccount.getSession().getToken(), crackedAccount.getSession().getType());
                Client.eventManager.callEvent(new SessionEvent());
                break;
            case 1000:
                mc.displayGuiScreen(new GuiProxyManager((GuiScreen) (Object) this));
                break;
            case 998:
                mc.displayGuiScreen(new GuiAltManager((GuiScreen) (Object) this));
                break;
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        Fonts.minecraftFont.drawStringWithShadow(
                "§7Username: §d" + mc.getSession().getUsername(),
                117f,
                11.5f,
                0xffffff);
    }
}