package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;

@ModuleInfo(name = "PlayerEdit", spacedName = "Player Edit", category = ModuleCategory.RENDER)
public class PlayerEdit extends Module {

    public static FloatValue playerSizeValue = new FloatValue("PlayerSize", 0.5f,0.01f,5f);
    public static BoolValue rotatePlayer = new BoolValue("RotatePlayer", false);
    public static BoolValue editPlayerSizeValue = new BoolValue("EditPlayerSize", true);
}