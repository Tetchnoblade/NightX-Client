package net.aspw.client.features.module.impl.other;

import net.aspw.client.event.EventTarget;
import net.aspw.client.event.MotionEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.value.BoolValue;
import net.aspw.client.value.FloatValue;

/**
 * The type Player edit.
 */
@ModuleInfo(name = "PlayerEdit", spacedName = "Player Edit", description = "", category = ModuleCategory.OTHER)
public class PlayerEdit extends Module {

    /**
     * The constant editPlayerSizeValue.
     */
    public static BoolValue editPlayerSizeValue = new BoolValue("PlayerSize", true);
    /**
     * The constant playerSizeValue.
     */
    public static FloatValue playerSizeValue = new FloatValue("PlayerSize", 1.5f, 0.5f, 2.5f, "m", () -> editPlayerSizeValue.get());
    /**
     * The constant rotatePlayer.
     */
    public static BoolValue rotatePlayer = new BoolValue("PlayerRotate", true);
    public static FloatValue xRot = new FloatValue("X-Rotation", 0.0f, -180.0f, 180.0f, () -> rotatePlayer.get());
    public static FloatValue yPos = new FloatValue("Y-Position", 0.0f, -5.0f, 5.0f, () -> rotatePlayer.get());

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (editPlayerSizeValue.get())
            mc.thePlayer.eyeHeight = playerSizeValue.get() + 0.62f;
        else mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight();
    }
}