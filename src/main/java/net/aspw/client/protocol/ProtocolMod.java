package net.aspw.client.protocol;

import net.aspw.client.protocol.api.VFPlatform;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "NightX", version = "Release")
public class ProtocolMod implements VFPlatform {
    public static final ProtocolMod PLATFORM = new ProtocolMod();
}