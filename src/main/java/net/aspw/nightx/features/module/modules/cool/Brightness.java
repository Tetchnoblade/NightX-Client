package net.aspw.nightx.features.module.modules.cool;

import net.aspw.nightx.event.ClientShutdownEvent;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(name = "Brightness", category = ModuleCategory.COOL)
public class Brightness extends Module {

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) mc.thePlayer.removePotionEffectClient(Potion.nightVision.id);
    }

    @EventTarget(ignoreCondition = true)
    public void onUpdate(final UpdateEvent event) {
        if (getState()) {
                    mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1337, 1));
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onShutdown(final ClientShutdownEvent event) {
        onDisable();
    }
}