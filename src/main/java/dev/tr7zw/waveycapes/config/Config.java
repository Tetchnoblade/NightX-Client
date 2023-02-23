package dev.tr7zw.waveycapes.config;

import dev.tr7zw.waveycapes.CapeMovement;
import dev.tr7zw.waveycapes.CapeStyle;
import dev.tr7zw.waveycapes.WindMode;

public class Config {

    public int configVersion = 3;
    public WindMode windMode = WindMode.WAVES;
    public CapeStyle capeStyle = CapeStyle.SMOOTH;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
}