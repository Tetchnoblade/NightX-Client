package net.aspw.nightx.features.module.modules.movement.speeds.other;

import net.aspw.nightx.NightX;
import net.aspw.nightx.event.MoveEvent;
import net.aspw.nightx.features.module.modules.movement.speeds.SpeedMode;
import net.aspw.nightx.features.module.modules.player.Inventory;
import net.aspw.nightx.utils.MovementUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;

public class Legit extends SpeedMode {

    public Legit() {
        super("Legit");
    }

    @Override
    public void onMotion() {
        final Inventory invMove = NightX.moduleManager.getModule(Inventory.class);
        mc.gameSettings.keyBindJump.pressed = ((MovementUtils.isMoving() || GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && (mc.inGameHasFocus || (invMove.getState() && !(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu) && (!invMove.getNoDetectableValue().get() || !(mc.currentScreen instanceof GuiContainer)))));
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
