package net.aspw.client.protocol;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.raphimc.vialoader.netty.VLLegacyPipeline;
import net.raphimc.vialoader.util.VersionEnum;

public class ProtocolPipeline extends VLLegacyPipeline {

    public ProtocolPipeline(UserConnection user, VersionEnum version) {
        super(user, version);
    }

    @Override
    protected String decompressName() {
        return "decompress";
    }

    @Override
    protected String compressName() {
        return "compress";
    }

    @Override
    protected String packetDecoderName() {
        return "decoder";
    }

    @Override
    protected String packetEncoderName() {
        return "encoder";
    }

    @Override
    protected String lengthSplitterName() {
        return "splitter";
    }

    @Override
    protected String lengthPrependerName() {
        return "prepender";
    }
}
