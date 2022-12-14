package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;

@ModuleInfo(name = "EnchantColor", spacedName = "Enchant Color", category = ModuleCategory.RENDER, array = false)
public class EnchantColor extends Module {
    public IntegerValue redValue = new IntegerValue("Red", 180, 0, 255);
    public IntegerValue greenValue = new IntegerValue("Green", 0, 0, 255);
    public IntegerValue blueValue = new IntegerValue("Blue", 0, 0, 255);
    public ListValue modeValue = new ListValue("Mode", new String[]{"Custom", "Rainbow", "Sky", "Mixer"}, "Custom");
    public IntegerValue rainbowSpeedValue = new IntegerValue("Seconds", 2, 1, 6);
    public IntegerValue rainbowDelayValue = new IntegerValue("Delay", 0, 0, 10);
    public FloatValue rainbowSatValue = new FloatValue("Saturation", 1.0f, 0.0f, 1.0f);
    public FloatValue rainbowBrgValue = new FloatValue("Brightness", 1.0f, 0.0f, 1.0f);
}