package net.aspw.nightx.features.module.modules.misc;

import net.aspw.nightx.event.ClientShutdownEvent;
import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.value.BoolValue;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(name = "PotionSpoof", spacedName = "Potion Spoof", category = ModuleCategory.MISC)
public class PotionSpoof extends Module {
    private final BoolValue speedValue = new BoolValue("Speed", false);
    private final BoolValue moveSlowDownValue = new BoolValue("Slowness", false);
    private final BoolValue hasteValue = new BoolValue("Haste", false);
    private final BoolValue digSlowDownValue = new BoolValue("MiningFatigue", false);
    private final BoolValue blindnessValue = new BoolValue("Blindness", false);
    private final BoolValue strengthValue = new BoolValue("Strength", false);
    private final BoolValue jumpBoostValue = new BoolValue("JumpBoost", false);
    private final BoolValue weaknessValue = new BoolValue("Weakness", false);
    private final BoolValue regenerationValue = new BoolValue("Regeneration", false);
    private final BoolValue witherValue = new BoolValue("Wither", false);
    private final BoolValue resistanceValue = new BoolValue("Resistance", false);
    private final BoolValue fireResistanceValue = new BoolValue("FireResistance", false);
    private final BoolValue absorptionValue = new BoolValue("Absorption", false);
    private final BoolValue healthBoostValue = new BoolValue("HealthBoost", false);
    private final BoolValue poisonValue = new BoolValue("Poison", false);
    private final BoolValue saturationValue = new BoolValue("Saturation", false);
    private final BoolValue waterBreathingValue = new BoolValue("WaterBreathing", false);

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.removePotionEffectClient(Potion.moveSpeed.id);
            mc.thePlayer.removePotionEffectClient(Potion.digSpeed.id);
            mc.thePlayer.removePotionEffectClient(Potion.moveSlowdown.id);
            mc.thePlayer.removePotionEffectClient(Potion.blindness.id);
            mc.thePlayer.removePotionEffectClient(Potion.damageBoost.id);
            mc.thePlayer.removePotionEffectClient(Potion.jump.id);
            mc.thePlayer.removePotionEffectClient(Potion.weakness.id);
            mc.thePlayer.removePotionEffectClient(Potion.regeneration.id);
            mc.thePlayer.removePotionEffectClient(Potion.fireResistance.id);
            mc.thePlayer.removePotionEffectClient(Potion.wither.id);
            mc.thePlayer.removePotionEffectClient(Potion.resistance.id);
            mc.thePlayer.removePotionEffectClient(Potion.absorption.id);
            mc.thePlayer.removePotionEffectClient(Potion.healthBoost.id);
            mc.thePlayer.removePotionEffectClient(Potion.digSlowdown.id);
            mc.thePlayer.removePotionEffectClient(Potion.poison.id);
            mc.thePlayer.removePotionEffectClient(Potion.saturation.id);
            mc.thePlayer.removePotionEffectClient(Potion.waterBreathing.id);
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onUpdate(final UpdateEvent event) {
        if (getState() && speedValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1337, 1));
        }

        if (getState() && hasteValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 1337, 1));
        }

        if (getState() && moveSlowDownValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 1337, 1));
        }

        if (getState() && blindnessValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 1337, 1));
        }

        if (getState() && strengthValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 1337, 1));
        }

        if (getState() && jumpBoostValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.jump.id, 1337, 1));
        }

        if (getState() && weaknessValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.weakness.id, 1337, 1));
        }

        if (getState() && regenerationValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 1337, 1));
        }

        if (getState() && fireResistanceValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 1337, 1));
        }

        if (getState() && witherValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.wither.id, 1337, 1));
        }

        if (getState() && resistanceValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 1337, 1));
        }

        if (getState() && absorptionValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.absorption.id, 1337, 1));
        }

        if (getState() && healthBoostValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.healthBoost.id, 1337, 1));
        }

        if (getState() && digSlowDownValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1337, 1));
        }

        if (getState() && poisonValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.poison.id, 1337, 1));
        }

        if (getState() && saturationValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.saturation.id, 1337, 1));
        }

        if (getState() && waterBreathingValue.get()) {
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1337, 1));
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onShutdown(final ClientShutdownEvent event) {
        onDisable();
    }
}