package net.aspw.nightx.features.module.modules.movement;

import net.aspw.nightx.event.*;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.MovementUtils;
import net.aspw.nightx.utils.block.BlockUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "Jesus", spacedName = "Jesus", category = ModuleCategory.MOVEMENT)
public class Jesus extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{"Vanilla", "NCP", "AAC", "AAC3.3.11", "AACFlight", "AAC4.2.1", "Horizon1.4.6", "Twillight", "MatrixFast", "Dolphin", "Swim"}, "NCP");
    private final BoolValue noJumpValue = new BoolValue("NoJump", false);

    private boolean nextTick;


    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isSneaking())
            return;

        switch (modeValue.get().toLowerCase()) {
            case "ncp":
            case "vanilla":
                if (BlockUtils.collideBlock(mc.thePlayer.getEntityBoundingBox(), block -> block instanceof BlockLiquid) && mc.thePlayer.isInsideOfMaterial(Material.air) && !mc.thePlayer.isSneaking())
                    mc.thePlayer.motionY = 0.08D;
                break;
            case "aac":
                BlockPos blockPos = mc.thePlayer.getPosition().down();

                if (!mc.thePlayer.onGround && BlockUtils.getBlock(blockPos) == Blocks.water || mc.thePlayer.isInWater()) {
                    if (!mc.thePlayer.isSprinting()) {
                        mc.thePlayer.motionX *= 0.99999;
                        mc.thePlayer.motionY *= 0.0;
                        mc.thePlayer.motionZ *= 0.99999;

                        if (mc.thePlayer.isCollidedHorizontally)
                            mc.thePlayer.motionY = (int) (mc.thePlayer.posY - (int) (mc.thePlayer.posY - 1)) / 8F;
                    } else {
                        mc.thePlayer.motionX *= 0.99999;
                        mc.thePlayer.motionY *= 0.0;
                        mc.thePlayer.motionZ *= 0.99999;

                        if (mc.thePlayer.isCollidedHorizontally)
                            mc.thePlayer.motionY = (int) (mc.thePlayer.posY - (int) (mc.thePlayer.posY - 1)) / 8F;
                    }

                    if (mc.thePlayer.fallDistance >= 4)
                        mc.thePlayer.motionY = -0.004;
                    else if (mc.thePlayer.isInWater())
                        mc.thePlayer.motionY = 0.09;
                }

                if (mc.thePlayer.hurtTime != 0)
                    mc.thePlayer.onGround = false;
                break;
            case "matrixfast":
                if (mc.thePlayer.isInWater()) {
                    mc.thePlayer.motionY = 0;
                    MovementUtils.strafe(0.6f);
                }
                break;
            case "aac3.3.11":
                if (mc.thePlayer.isInWater()) {
                    mc.thePlayer.motionX *= 1.17D;
                    mc.thePlayer.motionZ *= 1.17D;

                    if (mc.thePlayer.isCollidedHorizontally)
                        mc.thePlayer.motionY = 0.24;
                    else if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ)).getBlock() != Blocks.air)
                        mc.thePlayer.motionY += 0.04D;
                }
                break;

            case "dolphin":
                if (mc.thePlayer.isInWater())
                    mc.thePlayer.motionY += 0.03999999910593033;
                break;
            // jumpMovementFactor seem doesn't flag in old aac4 some modules.
            case "aac4.2.1":
                //i didn't fix if player collided wall(trigger aac flag)
                blockPos = mc.thePlayer.getPosition().down();
                if (!mc.thePlayer.onGround && BlockUtils.getBlock(blockPos) == Blocks.water || mc.thePlayer.isInWater()) {
                    mc.thePlayer.motionY *= 0.0;
                    //decrease value due to compatible 4 direction. forward is 0.089F
                    mc.thePlayer.jumpMovementFactor = 0.08F;

                    if (mc.thePlayer.fallDistance > 0)
                        return;
                    else if (mc.thePlayer.isInWater())
                        mc.gameSettings.keyBindJump.pressed = true;
                }
                break;
            //actually old ver horizon has weak some checks
            case "horizon1.4.6":
                if (mc.thePlayer.isInWater()) {
                    MovementUtils.strafe();
                    mc.gameSettings.keyBindJump.pressed = true;
                    if (MovementUtils.isMoving())
                        if (!mc.thePlayer.onGround) {
                            mc.thePlayer.motionY += 0.13D;
                        }
                }
                break;
            //idk
            case "twillight":
                if (mc.thePlayer.isInWater()) {
                    mc.thePlayer.motionX *= 1.04;
                    mc.thePlayer.motionZ *= 1.04;
                    MovementUtils.strafe();
                }
                break;
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        if ("twillight".equalsIgnoreCase(modeValue.get()) && mc.thePlayer.isInWater()) {
            event.setY(0.01);
            mc.thePlayer.motionY = 0.01;
        }
    }

    @EventTarget
    public void onBlockBB(final BlockBBEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.getEntityBoundingBox() == null)
            return;

        if (event.getBlock() instanceof BlockLiquid && !BlockUtils.collideBlock(mc.thePlayer.getEntityBoundingBox(), block -> block instanceof BlockLiquid) && !mc.thePlayer.isSneaking()) {
            switch (modeValue.get().toLowerCase()) {
                case "ncp":
                case "vanilla":
                    event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, event.getY() + 1, event.getZ() + 1));
                    break;
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.thePlayer == null || !modeValue.get().equalsIgnoreCase("NCP"))
            return;

        if (event.getPacket() instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();

            if (BlockUtils.collideBlock(new AxisAlignedBB(mc.thePlayer.getEntityBoundingBox().maxX, mc.thePlayer.getEntityBoundingBox().maxY, mc.thePlayer.getEntityBoundingBox().maxZ, mc.thePlayer.getEntityBoundingBox().minX, mc.thePlayer.getEntityBoundingBox().minY - 0.01D, mc.thePlayer.getEntityBoundingBox().minZ), block -> block instanceof BlockLiquid)) {
                nextTick = !nextTick;

                if (nextTick) packetPlayer.y -= 0.001D;
            }
        }
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if (mc.thePlayer == null)
            return;

        final Block block = BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.01, mc.thePlayer.posZ));

        if (noJumpValue.get() && block instanceof BlockLiquid)
            event.cancelEvent();
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
