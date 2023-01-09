package dev.tr7zw.waveycapes;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = "capes", name = "capes", version = "@VER@", clientSideOnly = true, guiFactory = "dev.tr7zw.waveycapes.config.WaveyCapesModGuiFactory")
public class WaveyCapesMod extends WaveyCapesBase {

	public WaveyCapesMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            LOGGER.warn("Capes Mod installed on a Server. Going to sleep.");
            return;
        }
	    init();
	    
	}

    @Override
    public void initSupportHooks() {


    }
	
}
