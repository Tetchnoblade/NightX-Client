package net.aspw.nightx.injection.forge;

import net.aspw.nightx.features.special.script.remapper.injection.transformers.AbstractJavaLinkerTransformer;
import net.aspw.nightx.injection.transformers.ForgeNetworkTransformer;
import net.aspw.nightx.injection.transformers.OptimizeTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
public class TransformerLoader implements IFMLLoadingPlugin {

    public TransformerLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("nightx.forge.mixins.json");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    /**
     * Return a list of classes that implements the IClassTransformer interface
     *
     * @return a list of classes that implements the IClassTransformer interface
     */
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ForgeNetworkTransformer.class.getName(), OptimizeTransformer.class.getName(), AbstractJavaLinkerTransformer.class.getName()};
    }

    /**
     * Return a class name that implements "ModContainer" for injection into the mod list
     * The "getName" function should return a name that other mods can, if need be,
     * depend on.
     * Trivially, this modcontainer will be loaded before all regular mod containers,
     * which means it will be forced to be "immutable" - not susceptible to normal
     * sorting behaviour.
     * All other mod behaviours are available however- this container can receive and handle
     * normal loading events
     */
    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    /**
     * Inject coremod data into this coremod
     * This data includes:
     * "mcLocation" : the location of the minecraft directory,
     * "coremodList" : the list of coremods
     * "coremodLocation" : the file this coremod loaded from,
     *
     * @param data
     */
    @Override
    public void injectData(Map<String, Object> data) {

    }

    /**
     * Return an optional access transformer class for this coremod. It will be injected post-deobf
     *
     * @return the name of an access transformer class or null if none is provided
     */
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
