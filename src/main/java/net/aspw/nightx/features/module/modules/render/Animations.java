package net.aspw.nightx.features.module.modules.render;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.EventState;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.MotionEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.features.module.modules.combat.KillAura;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.ListValue;

@ModuleInfo(name = "Animations", category = ModuleCategory.RENDER, array = false)
public class Animations extends Module {
    // some ListValue
    public static final ListValue Sword = new ListValue("Style", new String[]{
            "1.8", "LiquidBounce", "SlideLow", "SlideMedium", "SlideFull", "SlidePut", "Push", "Dash", "Swing", "Swank", "Swong", "Swang", "Swaing", "Stella", "Smart", "Sloth", "Astolfo", "ETB", "Moon", "MoonPush", "Smooth",
            "Leaked", "Ninja", "Jigsaw", "Avatar", "Sigma3", "Sigma4", "Reverse", "Old", "OldFull", "Flux1", "Flux2", "Flux3", "DortwareNew", "Dortware1", "Dortware2", "Funny", "Zoom", "Rotate", "Spin", "Spinny"
    }, "Sloth");
    // item general scale
    public static final FloatValue Scale = new FloatValue("Scale", 0.4f, 0f, 4f);

    // normal item position
    public static final FloatValue itemPosX = new FloatValue("ItemPosX", 0f, -1f, 1f);
    public static final FloatValue itemPosY = new FloatValue("ItemPosY", 0f, -1f, 1f);
    public static final FloatValue itemPosZ = new FloatValue("ItemPosZ", 0f, -1f, 1f);
    public static final FloatValue itemFovX = new FloatValue("ItemFovX", 1f, -10f, 10f);
    public static final FloatValue itemFovY = new FloatValue("ItemFovY", 1f, -10f, 10f);
    public static final FloatValue itemFovZ = new FloatValue("ItemFovZ", 1f, -10f, 10f);

    // change Position Blocking Sword
    public static final FloatValue blockPosX = new FloatValue("BlockPosX", 0f, -1f, 1f);
    public static final FloatValue blockPosY = new FloatValue("BlockPosY", 0f, -1f, 1f);
    public static final FloatValue blockPosZ = new FloatValue("BlockPosZ", 0f, -1f, 1f);

    // modify item swing and rotate
    public static final IntegerValue SpeedSwing = new IntegerValue("Swing-Speed", -6, -12, 5);
    public static final IntegerValue Equip = new IntegerValue("Equip-Motion", 2, -5, 5, () -> Sword.get().equalsIgnoreCase("push") || Sword.get().equalsIgnoreCase("swank") || Sword.get().equalsIgnoreCase("swong") || Sword.get().equalsIgnoreCase("swang") ||
            Sword.get().equalsIgnoreCase("swaing") || Sword.get().equalsIgnoreCase("dash") || Sword.get().equalsIgnoreCase("smart") || Sword.get().equalsIgnoreCase("moon") || Sword.get().equalsIgnoreCase("dortware1") || Sword.get().equalsIgnoreCase("dortware2"));
    public static final BoolValue RotateItems = new BoolValue("Rotate-Items", false);
    public static final FloatValue SpeedRotate = new FloatValue("Rotate-Speed", 1f, 0f, 10f, () -> RotateItems.get() || Sword.get().equalsIgnoreCase("spinny") || Sword.get().equalsIgnoreCase("rotate"));
    public static final FloatValue SpinSpeed = new FloatValue("Spin-Speed", 5f, 0f, 50f, () -> Sword.get().equalsIgnoreCase("spin"));

    // transform rotation
    public static final ListValue transformFirstPersonRotate = new ListValue("RotateMode", new String[]{"RotateY", "RotateXY", "Custom", "None"}, "RotateY");

    // custom item rotate
    public static final FloatValue customRotate1 = new FloatValue("RotateXAxis", 0, -180, 180, () -> RotateItems.get() && transformFirstPersonRotate.get().equalsIgnoreCase("custom"));
    public static final FloatValue customRotate2 = new FloatValue("RotateYAxis", 0, -180, 180, () -> RotateItems.get() && transformFirstPersonRotate.get().equalsIgnoreCase("custom"));
    public static final FloatValue customRotate3 = new FloatValue("RotateZAxis", 0, -180, 180, () -> RotateItems.get() && transformFirstPersonRotate.get().equalsIgnoreCase("custom"));

    // fake blocking
    public static final BoolValue fakeBlock = new BoolValue("Always-Block", true);

    // gui animations
    public static final ListValue guiAnimations = new ListValue("Container-Animation", new String[]{"None", "Zoom", "Slide", "Smooth"}, "None");
    public static final ListValue vSlideValue = new ListValue("Slide-Vertical", new String[]{"None", "Upward", "Downward"}, "None", () -> guiAnimations.get().equalsIgnoreCase("slide"));
    public static final ListValue hSlideValue = new ListValue("Slide-Horizontal", new String[]{"None", "Right", "Left"}, "Left", () -> guiAnimations.get().equalsIgnoreCase("slide"));
    public static final IntegerValue animTimeValue = new IntegerValue("Container-AnimTime", 200, 0, 3000, () -> !guiAnimations.get().equalsIgnoreCase("none"));
    public static final ListValue tabAnimations = new ListValue("Tab-Animation", new String[]{"None", "Zoom", "Slide"}, "None");
    // block break
    public static final BoolValue noBlockParticles = new BoolValue("NoBlockParticles", false);
    public static final BoolValue swingAnimValue = new BoolValue("FluxSwingAnimation", false);
    public static final BoolValue smoothAnimValue = new BoolValue("SmoothSwingAnimation", false);

    public void onInitialize() {
        setState(true);
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        final KillAura killAura = NightX.moduleManager.getModule(KillAura.class);
        if (event.getEventState() == EventState.POST && mc.thePlayer.isSwingInProgress && smoothAnimValue.get() && !mc.thePlayer.isBlocking() && killAura.getTarget() == null) {
            mc.thePlayer.renderArmPitch = 140f;
        }
    }
}