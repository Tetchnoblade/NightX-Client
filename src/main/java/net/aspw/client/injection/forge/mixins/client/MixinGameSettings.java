package net.aspw.client.injection.forge.mixins.client;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * The type Mixin game settings.
 */
@Mixin(GameSettings.class)
public class MixinGameSettings {
    /**
     * Is key down boolean.
     *
     * @param key the key
     * @return the boolean
     * @author As_pw
     * @reason Game Settings
     */
    @Overwrite
    public static boolean isKeyDown(KeyBinding key) {
        int keyCode = key.getKeyCode();
        if (keyCode != 0 && keyCode < 256) {
            return keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
        } else {
            return false;
        }
    }
}
