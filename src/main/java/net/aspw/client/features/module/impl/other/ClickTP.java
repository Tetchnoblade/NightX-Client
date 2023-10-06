package net.aspw.client.features.module.impl.other;

import net.aspw.client.Client;
import net.aspw.client.event.EventTarget;
import net.aspw.client.event.Render3DEvent;
import net.aspw.client.event.UpdateEvent;
import net.aspw.client.features.module.Module;
import net.aspw.client.features.module.ModuleCategory;
import net.aspw.client.features.module.ModuleInfo;
import net.aspw.client.features.module.impl.visual.Interface;
import net.aspw.client.util.PacketUtils;
import net.aspw.client.util.block.BlockUtils;
import net.aspw.client.util.pathfinder.MainPathFinder;
import net.aspw.client.util.pathfinder.Vec3;
import net.aspw.client.value.ListValue;
import net.aspw.client.visual.hud.element.elements.Notification;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * The type Click tp.
 */
@ModuleInfo(name = "ClickTP", spacedName = "Click TP", description = "", category = ModuleCategory.OTHER)
public class ClickTP extends Module {
    private final ListValue buttonValue = new ListValue("Button", new String[]{"Left", "Right", "Middle"}, "Middle");

    private int delay;
    private BlockPos endPos;
    private MovingObjectPosition objectPosition;

    @Override
    public void onDisable() {
        delay = 0;
        endPos = null;
        super.onDisable();
    }

    /**
     * On update.
     *
     * @param event the event
     */
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mc.currentScreen == null && Mouse.isButtonDown(Arrays.asList(buttonValue.getValues()).indexOf(buttonValue.get())) && delay <= 0) {
            endPos = objectPosition.getBlockPos();

            if (Objects.requireNonNull(BlockUtils.getBlock(endPos)).getMaterial() == Material.air) {
                endPos = null;
                return;
            }
            Client.hud.addNotification(new Notification("Successfully Teleported to X: " + endPos.getX() + ", Y: " + endPos.getY() + ", Z: " + endPos.getZ(), Notification.Type.SUCCESS));
            delay = 6;
        }

        if (delay > 0) {
            --delay;
        }

        if (endPos != null) {
            final double endX = endPos.getX() + 0.5D;
            final double endY = endPos.getY() + 1D;
            final double endZ = endPos.getZ() + 0.5D;
            new Thread(() -> {
                final ArrayList<Vec3> path = MainPathFinder.computePath(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
                        new Vec3(endX, endY, endZ));
                for (final Vec3 point : path)
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(point.getX(), point.getY(), point.getZ(), true));
                mc.thePlayer.setPosition(endX, endY, endZ);
            }).start();
            if (Objects.requireNonNull(Client.moduleManager.getModule(Interface.class)).getFlagSoundValue().get()) {
                Client.tipSoundManager.getPopSound().asyncPlay(Client.moduleManager.getPopSoundPower());
            }
            endPos = null;
        }
    }

    /**
     * On render 3 d.
     *
     * @param event the event
     */
    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        objectPosition = mc.thePlayer.rayTrace(1000, event.getPartialTicks());

        if (objectPosition.getBlockPos() == null)
            return;

        final int x = objectPosition.getBlockPos().getX();
        final int y = objectPosition.getBlockPos().getY();
        final int z = objectPosition.getBlockPos().getZ();

        if (Objects.requireNonNull(BlockUtils.getBlock(objectPosition.getBlockPos())).getMaterial() != Material.air) {
            final RenderManager renderManager = mc.getRenderManager();
        }
    }

}