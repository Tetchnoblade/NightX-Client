package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;

@ModuleInfo(name = "Animations", category = ModuleCategory.RENDER, array = false)
public class Animations extends Module {

    // some ListValue
    public static final ListValue Sword = new ListValue("Style", new String[]{
            "1.8", "SlideFull", "Slide", "Spin", "Push", "Swing", "Swank", "Swong", "Swang", "Swaing", "Stella", "Sweet", "Moon", "MoonPush", "Lennox", "Leaked", "Jigsaw", "Sigma3", "Sigma4", "Okura", "Leet",
            "Old", "OldFull", "Flux1", "Flux2", "Flux3", "DortwareNew", "Dortware1", "Dortware2", "Smart", "Cool", "Funny", "Pumpkin", "ETB", "Zoom", "Avatar", "Tap1", "Tap2", "Poke", "Push1", "Push2", "Up", "Shield", "Akrien", "VisionFX",
            "Lucky", "Rotate360", "SmoothFloat", "Strange", "Move", "Stab", "OldSwang", "Jello"
    }, "Swing");

    // item general scale
    public static final FloatValue Scale = new FloatValue("Scale", 0.4f, 0f, 4f);

    // normal item position
    public static final FloatValue itemPosX = new FloatValue("ItemX", 0f, -1f, 1f);
    public static final FloatValue itemPosY = new FloatValue("ItemY", 0f, -1f, 1f);
    public static final FloatValue itemPosZ = new FloatValue("ItemZ", 0f, -1f, 1f);

    // change Position Blocking Sword
    public static final FloatValue blockPosX = new FloatValue("BlockingX", 0f, -1f, 1f);
    public static final FloatValue blockPosY = new FloatValue("BlockingY", 0f, -1f, 1f);
    public static final FloatValue blockPosZ = new FloatValue("BlockingZ", 0f, -1f, 1f);

    // modify item swing and rotate
    public static final IntegerValue SpeedSwing = new IntegerValue("Swing-Speed", 6, -3, 10);
    public static final BoolValue RotateItems = new BoolValue("Rotate-Items", false);
    public static final FloatValue SpeedRotate = new FloatValue("Rotate-Speed", 1f, 0f, 10f, () -> RotateItems.get() || Sword.get().equalsIgnoreCase("smoothfloat") || Sword.get().equalsIgnoreCase("rotate360"));
    public static final FloatValue SpinSpeed = new FloatValue("Spin-Speed", 5f, 0f, 50f, () -> Sword.get().equalsIgnoreCase("spin"));

    // transform rotation
    public static final ListValue transformFirstPersonRotate = new ListValue("RotateMode", new String[]{"RotateY", "RotateXY", "Custom", "None"}, "RotateY");

    // custom item rotate
    public static final FloatValue customRotate1 = new FloatValue("RotateXAxis", 0, -180, 180, () -> RotateItems.get() && transformFirstPersonRotate.get().equalsIgnoreCase("custom"));
    public static final FloatValue customRotate2 = new FloatValue("RotateYAxis", 0, -180, 180, () -> RotateItems.get() && transformFirstPersonRotate.get().equalsIgnoreCase("custom"));
    public static final FloatValue customRotate3 = new FloatValue("RotateZAxis", 0, -180, 180, () -> RotateItems.get() && transformFirstPersonRotate.get().equalsIgnoreCase("custom"));

    // fake blocking
    public static final BoolValue fakeBlock = new BoolValue("Always-Block", true);

    // block not everything
    public static final BoolValue blockEverything = new BoolValue("Block-Everything", false);

    // gui animations
    public static final ListValue guiAnimations = new ListValue("Container-Animation", new String[]{"None", "Zoom", "Slide", "Smooth"}, "None");
    public static final ListValue vSlideValue = new ListValue("Slide-Vertical", new String[]{"None", "Upward", "Downward"}, "None", () -> guiAnimations.get().equalsIgnoreCase("slide"));
    public static final ListValue hSlideValue = new ListValue("Slide-Horizontal", new String[]{"None", "Right", "Left"}, "Left", () -> guiAnimations.get().equalsIgnoreCase("slide"));
    public static final IntegerValue animTimeValue = new IntegerValue("Container-AnimTime", 200, 0, 3000, () -> !guiAnimations.get().equalsIgnoreCase("none"));
    public static final ListValue tabAnimations = new ListValue("Tab-Animation", new String[]{"None", "Zoom", "Slide"}, "None");
    // block break
    public static final BoolValue noBlockParticles = new BoolValue("NoBlockParticles", false);
    public static final BoolValue swingAnimValue = new BoolValue("SwingAnimation", false);

    public void onInitialize() {
        setState(true);
    }
}