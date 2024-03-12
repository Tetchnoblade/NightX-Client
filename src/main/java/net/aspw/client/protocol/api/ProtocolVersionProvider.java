package net.aspw.client.protocol.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import net.aspw.client.protocol.ProtocolBase;
import net.aspw.client.utils.MinecraftInstance;

import java.util.Objects;

public class ProtocolVersionProvider extends BaseVersionProvider {

    @Override
    public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide() && !MinecraftInstance.mc.isIntegratedServerRunning()) {
            return Objects.requireNonNull(connection.getChannel()).attr(ProtocolBase.VF_NETWORK_MANAGER).get().viaForge$getTrackedVersion();
        }
        return super.getClosestServerProtocol(connection);
    }

}