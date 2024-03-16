package net.aspw.client.protocol;

import net.aspw.client.protocol.api.VFPlatform;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "NightX", version = "Release")
public class ProtocolMod implements VFPlatform {

    public static final ProtocolMod PLATFORM = new ProtocolMod();

    @Override
    public int getGameVersion() {
        return RealmsSharedConstants.NETWORK_PROTOCOL_VERSION;
    }
}