package net.aspw.nightx.features.module.modules.client;

import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.ListValue;

@ModuleInfo(name = "ClientSpoof", spacedName = "Client Spoof", category = ModuleCategory.CLIENT, forceNoSound = true, onlyEnable = true, array = false)
public final class ClientSpoof extends Module {
    public final ListValue modeValue = new ListValue("Mode", new String[]{
            "Vanilla",
            "Forge",
            "PvPLounge"
    }, "Vanilla");
}