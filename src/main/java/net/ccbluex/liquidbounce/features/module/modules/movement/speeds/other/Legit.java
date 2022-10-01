package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Inventory;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
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
        final Inventory invMove = LiquidBounce.moduleManager.getModule(Inventory.class);
        mc.gameSettings.keyBindJump.pressed = ((MovementUtils.isMoving() || GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && (mc.inGameHasFocus || (invMove.getState() && !(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu) && (!invMove.getNoDetectableValue().get() || !(mc.currentScreen instanceof GuiContainer)))));
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
