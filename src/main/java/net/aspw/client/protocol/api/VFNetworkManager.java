package net.aspw.client.protocol.api;

import net.raphimc.vialoader.util.VersionEnum;

public interface VFNetworkManager {

    VersionEnum viaForge$getTrackedVersion();

    void viaForge$setTrackedVersion(final VersionEnum version);

}