package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;

@ModuleInfo(name = "PlayerEdit", spacedName = "Player Edit", category = ModuleCategory.RENDER)
public class PlayerEdit extends Module {

    public static FloatValue playerSizeValue = new FloatValue("PlayerSize", 0.5f,0.01f,5f);
    public static BoolValue rotatePlayer = new BoolValue("RotatePlayer", false);
    public static BoolValue editPlayerSizeValue = new BoolValue("EditPlayerSize", true);
}