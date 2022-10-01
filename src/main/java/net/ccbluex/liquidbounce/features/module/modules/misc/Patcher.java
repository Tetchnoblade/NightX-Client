package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "Patcher", description = "", category = ModuleCategory.MISC, canEnable = false)
public class Patcher extends Module {

    public static final BoolValue noHitDelay = new BoolValue("NoHitDelay", true);
    public static final BoolValue jumpPatch = new BoolValue("JumpFix", false);
    public static final BoolValue chatPosition = new BoolValue("ChatPosition1.12", true);
    public static final BoolValue silentNPESP = new BoolValue("SilentNPE-SpawnPlayer", true);
}