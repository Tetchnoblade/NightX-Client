package net.ccbluex.liquidbounce.features.module.modules.cool;

import net.ccbluex.liquidbounce.event.ClientShutdownEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(name = "Brightness", category = ModuleCategory.COOL)
public class Brightness extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Gamma", "NightVision"}, "NightVision");

    private float prevGamma = -1;

    @Override
    public void onEnable() {
        prevGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        if (prevGamma == -1)
            return;

        mc.gameSettings.gammaSetting = prevGamma;
        prevGamma = -1;
        if (mc.thePlayer != null) mc.thePlayer.removePotionEffectClient(Potion.nightVision.id);
    }

    @EventTarget(ignoreCondition = true)
    public void onUpdate(final UpdateEvent event) {
        if (getState()) {
            switch (modeValue.get().toLowerCase()) {
                case "gamma":
                    if (mc.gameSettings.gammaSetting <= 100F)
                        mc.gameSettings.gammaSetting++;
                    break;
                case "nightvision":
                    mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1337, 1));
                    break;
            }
        } else if (prevGamma != -1) {
            mc.gameSettings.gammaSetting = prevGamma;
            prevGamma = -1;
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onShutdown(final ClientShutdownEvent event) {
        onDisable();
    }
}