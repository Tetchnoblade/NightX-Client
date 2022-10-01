/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;

@ModuleInfo(name = "EnchantColor", spacedName = "Enchant Color", description = "", category = ModuleCategory.RENDER)
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