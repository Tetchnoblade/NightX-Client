package net.aspw.client.features.module.impl.movement.speeds.other;

import net.aspw.client.Client;
import net.aspw.client.event.MoveEvent;
import net.aspw.client.features.module.impl.movement.Inventory;
import net.aspw.client.features.module.impl.movement.speeds.SpeedMode;
import net.aspw.client.utils.MovementUtils;
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
        final Inventory invMove = Client.moduleManager.getModule(Inventory.class);
        mc.gameSettings.keyBindJump.pressed = ((MovementUtils.isMoving() || GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && (mc.inGameHasFocus || (invMove.getState() && !(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu) && (!invMove.getNoDetectableValue().get() || !(mc.currentScreen instanceof GuiContainer)))));
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
