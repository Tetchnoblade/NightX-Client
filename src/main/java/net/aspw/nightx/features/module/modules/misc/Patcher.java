package net.aspw.nightx.features.module.modules.misc;

import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.BoolValue;

@ModuleInfo(name = "Patcher", category = ModuleCategory.MISC, canEnable = false)
public class Patcher extends Module {

    public static final BoolValue noHitDelay = new BoolValue("NoHitDelay", true);
    public static final BoolValue jumpPatch = new BoolValue("JumpFix", false);
    public static final BoolValue chatPosition = new BoolValue("ChatPosition1.12", true);
    public static final BoolValue silentNPESP = new BoolValue("SilentNPE-SpawnPlayer", true);
}