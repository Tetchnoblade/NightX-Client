package net.aspw.nightx.features.module.modules.movement;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.block.BlockUtils;
import net.aspw.nightx.value.FloatValue;
import net.minecraft.block.BlockLiquid;

@ModuleInfo(name = "Water", spacedName = "Water", category = ModuleCategory.MOVEMENT)
public class Water extends Module {

    private final FloatValue speedValue = new FloatValue("Speed", 1.2F, 1.0F, 1.5F);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.isInWater() && BlockUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) {
            final float speed = speedValue.get();

            mc.thePlayer.motionX *= speed;
            mc.thePlayer.motionZ *= speed;
        }
    }
}