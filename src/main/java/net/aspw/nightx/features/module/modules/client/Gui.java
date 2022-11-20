package net.aspw.nightx.features.module.modules.client;

import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.ui.client.clickgui.newVer.NewUi;
import net.aspw.nightx.utils.render.ColorUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "Gui", category = ModuleCategory.CLIENT, keyBind = Keyboard.KEY_RSHIFT, forceNoSound = true, onlyEnable = true, array = false)
public class Gui extends Module {
    public static final BoolValue fastRenderValue = new BoolValue("FastRender", false);

    private static final ListValue colorModeValue = new ListValue("Color", new String[]{"Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade", "Mixer"}, "Fade");
    private static final IntegerValue colorRedValue = new IntegerValue("Red", 154, 0, 255);
    private static final IntegerValue colorGreenValue = new IntegerValue("Green", 114, 0, 255);
    private static final IntegerValue colorBlueValue = new IntegerValue("Blue", 175, 0, 255);
    private static final FloatValue saturationValue = new FloatValue("Saturation", 0.5F, 0F, 1F);
    private static final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private static final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);

    public static Color getAccentColor() {
        Color c = new Color(255, 255, 255, 255);
        switch (colorModeValue.get().toLowerCase()) {
            case "custom":
                c = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                break;
            case "rainbow":
                c = new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
                break;
            case "sky":
                c = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
                break;
            case "liquidslowly":
                c = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
                break;
            case "fade":
                c = ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
                break;
            case "mixer":
                c = ColorMixer.getMixedColor(0, mixerSecondsValue.get());
                break;
        }
        return c;
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(NewUi.getInstance());
    }
}
