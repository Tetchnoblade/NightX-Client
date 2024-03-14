package net.aspw.client.features.module.impl.visual;

import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.value.FloatValue;
import net.aspw.client.value.IntegerValue;
import net.aspw.client.value.ListValue;

/**
 * The type Enchant color.
 */
@ModuleInfo(name = "EnchantColor", spacedName = "Enchant Color", category = ModuleCategory.VISUAL)
public class EnchantColor extends Module {
    /**
     * The Red value.
     */
    public IntegerValue redValue = new IntegerValue("Red", 180, 0, 255);
    /**
     * The Green value.
     */
    public IntegerValue greenValue = new IntegerValue("Green", 0, 0, 255);
    /**
     * The Blue value.
     */
    public IntegerValue blueValue = new IntegerValue("Blue", 0, 0, 255);
    /**
     * The Mode value.
     */
    public ListValue modeValue = new ListValue("Mode", new String[]{"Custom", "Rainbow", "Sky"}, "Custom");
    /**
     * The Rainbow speed value.
     */
    public IntegerValue rainbowSpeedValue = new IntegerValue("Seconds", 2, 1, 6);
    /**
     * The Rainbow delay value.
     */
    public IntegerValue rainbowDelayValue = new IntegerValue("Delay", 0, 0, 10);
    /**
     * The Rainbow sat value.
     */
    public FloatValue rainbowSatValue = new FloatValue("Saturation", 1.0f, 0.0f, 1.0f);
    /**
     * The Rainbow brg value.
     */
    public FloatValue rainbowBrgValue = new FloatValue("Brightness", 1.0f, 0.0f, 1.0f);
}