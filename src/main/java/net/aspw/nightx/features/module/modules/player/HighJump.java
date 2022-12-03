package net.aspw.nightx.features.module.modules.player;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.JumpEvent;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.MovementUtils;
import net.aspw.nightx.utils.block.BlockUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.FloatValue;
import net.aspw.nightx.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "HighJump", spacedName = "High Jump", category = ModuleCategory.PLAYER)
public class HighJump extends Module {

    private final FloatValue heightValue = new FloatValue("Height", 5F, 1.0F, 10F, "m");
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Vanilla", "Damage", "AACv3", "DAC", "Mineplex", "MatrixWater"}, "Vanilla");
    private final BoolValue glassValue = new BoolValue("OnlyGlassPane", false);

    public int tick;

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch (modeValue.get().toLowerCase()) {
            case "damage":
                if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround)
                    mc.thePlayer.motionY += 0.42F * heightValue.get();
                break;
            case "aacv3":
                if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.059D;
                break;
            case "dac":
                if (!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.049999;
                break;
            case "mineplex":
                if (!mc.thePlayer.onGround) MovementUtils.strafe(0.35F);
                break;
            case "matrixwater":
                if (mc.thePlayer.isInWater()) {
                    if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ)).getBlock() == Block.getBlockById(9)) {
                        mc.thePlayer.motionY = 0.18;
                    } else if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).getBlock() == Block.getBlockById(9)) {
                        mc.thePlayer.motionY = heightValue.get();
                        mc.thePlayer.onGround = true;
                    }
                }
                break;
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        if (glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        if (!mc.thePlayer.onGround) {
            if ("mineplex".equalsIgnoreCase(modeValue.get())) {
                mc.thePlayer.motionY += mc.thePlayer.fallDistance == 0 ? 0.0499D : 0.05D;
            }
        }

    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if (glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch (modeValue.get().toLowerCase()) {
            case "vanilla":
                event.setMotion(event.getMotion() * heightValue.get());
                break;
            case "mineplex":
                event.setMotion(0.47F);
                break;
        }
    }


    @Override
    public String getTag() {
        return modeValue.get();
    }
}
