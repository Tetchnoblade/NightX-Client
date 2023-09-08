package net.aspw.client.features.module.impl.other;

import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.ListValue;

/**
 * The type Client spoof.
 */
@ModuleInfo(name = "ClientSpoof", spacedName = "Client Spoof", description = "", category = ModuleCategory.OTHER, forceNoSound = true, onlyEnable = true, array = false)
public final class ClientSpoof extends Module {
    /**
     * The Mode value.
     */
    public final BoolValue blockModsCheck = new BoolValue("AntiFML", false);
    public final ListValue modeValue = new ListValue("Mode", new String[]{
            "Vanilla",
            "Forge",
            "OptiFine",
            "Fabric",
            "Lunar",
            "LabyMod",
            "CheatBreaker",
            "PvPLounge",
            "Geyser"
    }, "Vanilla");
}