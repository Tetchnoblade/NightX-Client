package net.aspw.client.protocol.api;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.aspw.client.protocol.ProtocolBase;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;

public class ProtocolOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        final ProtocolBase common = ProtocolBase.getManager();
        common.getPlatform().joinServer(serverId);
    }

}