package net.aspw.client.injection.forge.mixins.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.viaversion.viaversion.api.connection.UserConnection;
import net.aspw.client.protocol.ProtocolBase;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.NetworkManager;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DataFlowIssue")
@Mixin(NetHandlerLoginClient.class)
public class MixinNetHandlerLoginClient {

    @Shadow
    @Final
    private NetworkManager networkManager;

    @Redirect(method = "handleEncryptionRequest", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Ljava/lang/String;)V"), remap = false)
    public void onlyJoinServerIfPremium(MinecraftSessionService instance, GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        if (ProtocolBase.getManager().getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            final UserConnection user = networkManager.channel().attr(ProtocolBase.LOCAL_VIA_USER).get();
            if (user != null && user.has(ProtocolMetadataStorage.class) && !user.get(ProtocolMetadataStorage.class).authenticate) {
                return;
            }
        }
        instance.joinServer(profile, authenticationToken, serverId);
    }
}