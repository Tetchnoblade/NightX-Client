package dev.tr7zw.entityculling;


import net.aspw.client.Client;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod(modid = "nightx", name = "NightX", version = Client.CLIENT_VERSION, clientSideOnly = true)
public class EntityCullingMod extends EntityCullingModBase {

    private boolean onServer = false;

    public EntityCullingMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        } catch (Throwable ex) {
            onServer = true;
            return;
        }
        onInitialize();
    }

    @Override
    public void initModloader() {

    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void doClientTick(ClientTickEvent event) {
        this.clientTick();
    }

    @SubscribeEvent
    public void doWorldTick(WorldTickEvent event) {
        this.worldTick();
    }

}
