package net.aspw.client.injection.forge;

import net.aspw.client.injection.transformers.AbstractJavaLinkerTransformer;
import net.aspw.client.injection.transformers.ForgeNetworkTransformer;
import net.aspw.client.injection.transformers.OptimizeTransformer;
import net.aspw.client.injection.transformers.ProtocolTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class MixinLoader implements IFMLLoadingPlugin {

    public MixinLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("client.forge.mixins.json");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ForgeNetworkTransformer.class.getName(), AbstractJavaLinkerTransformer.class.getName(), ProtocolTransformer.class.getName(), OptimizeTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
