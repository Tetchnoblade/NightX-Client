package net.aspw.nightx.injection.forge.mixins.client;

import de.enzaxd.viaforge.ViaForge;
import de.enzaxd.viaforge.util.AttackOrder;
import net.aspw.nightx.NightX;
import net.aspw.nightx.event.*;
import net.aspw.nightx.features.module.modules.combat.KillAura;
import net.aspw.nightx.features.module.modules.misc.Annoy;
import net.aspw.nightx.features.module.modules.render.SilentView;
import net.aspw.nightx.features.module.modules.world.FastPlace;
import net.aspw.nightx.features.module.modules.world.Scaffold;
import net.aspw.nightx.injection.forge.mixins.accessors.MinecraftForgeClientAccessor;
import net.aspw.nightx.utils.CPSCounter;
import net.aspw.nightx.utils.RotationUtils;
import net.aspw.nightx.utils.render.RenderUtils;
import net.aspw.nightx.visual.client.GuiMainMenu;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    public boolean skipRenderWorld;
    @Shadow
    public MovingObjectPosition objectMouseOver;
    @Shadow
    public WorldClient theWorld;
    @Shadow
    public EntityPlayerSP thePlayer;
    @Shadow
    public EffectRenderer effectRenderer;
    @Shadow
    public EntityRenderer entityRenderer;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    public int displayWidth;
    @Shadow
    public int displayHeight;
    @Shadow
    public int rightClickDelayTimer;
    @Shadow
    public GameSettings gameSettings;
    @Shadow
    private Entity renderViewEntity;
    @Shadow
    private boolean fullscreen;
    @Shadow
    private int leftClickCounter;
    private long lastFrame = getTime();

    @Shadow
    public abstract IResourceManager getResourceManager();

    @Inject(method = "<init>", at = @At("RETURN"))
    public void injectConstructor(GameConfiguration p_i45547_1_, CallbackInfo ci) {
        try {
            ViaForge.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void init(CallbackInfo callbackInfo) {
        if (displayWidth < 1067)
            displayWidth = 1067;

        if (displayHeight < 622)
            displayHeight = 622;
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void startGame(CallbackInfo callbackInfo) {
        NightX.INSTANCE.startClient();
    }

    @Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void createDisplay(CallbackInfo callbackInfo) {
        Display.setTitle(NightX.CLIENT_BEST + " - " + NightX.CLIENT_VERSION);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != this.theWorld) {
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Inject(
            method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;theWorld:Lnet/minecraft/client/multiplayer/WorldClient;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER)
    )
    private void clearRenderCache(CallbackInfo ci) {
        //noinspection ResultOfMethodCallIgnored
        MinecraftForgeClient.getRenderPass(); // Ensure class is loaded, strange accessor issue
        MinecraftForgeClientAccessor.getRegionCache().invalidateAll();
        MinecraftForgeClientAccessor.getRegionCache().cleanUp();
    }

    @Redirect(
            method = "runGameLoop",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V")
    )
    private void skipTwitchCode1(IStream instance) {
        // No-op
    }

    @Redirect(
            method = "runGameLoop",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V")
    )
    private void skipTwitchCode2(IStream instance) {
        // No-op
    }

    @Inject(method = "displayGuiScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", shift = At.Shift.AFTER))
    private void displayGuiScreen(CallbackInfo callbackInfo) {
        if (currentScreen instanceof net.minecraft.client.gui.GuiMainMenu || (currentScreen != null && currentScreen.getClass().getName().startsWith("net.labymod") && currentScreen.getClass().getSimpleName().equals("ModGuiMainMenu"))) {
            currentScreen = new GuiMainMenu();

            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            currentScreen.setWorldAndResolution(Minecraft.getMinecraft(), scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            skipRenderWorld = false;
        }

        NightX.eventManager.callEvent(new ScreenEvent(currentScreen));
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoop(final CallbackInfo callbackInfo) {
        final long currentTime = getTime();
        final int deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;

        RenderUtils.deltaTime = deltaTime;
    }

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;joinPlayerCounter:I", shift = At.Shift.BEFORE))
    private void onTick(final CallbackInfo callbackInfo) {
        NightX.eventManager.callEvent(new TickEvent());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo callbackInfo) {
        if (Keyboard.getEventKeyState() && currentScreen == null)
            NightX.eventManager.callEvent(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
    }

    @Inject(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovingObjectPosition;getBlockPos()Lnet/minecraft/util/BlockPos;"))
    private void onClickBlock(CallbackInfo callbackInfo) {
        if (this.leftClickCounter == 0 && theWorld.getBlockState(objectMouseOver.getBlockPos()).getBlock().getMaterial() != Material.air) {
            NightX.eventManager.callEvent(new ClickBlockEvent(objectMouseOver.getBlockPos(), this.objectMouseOver.sideHit));
        }
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void shutdown(CallbackInfo callbackInfo) {
        NightX.INSTANCE.stopClient();
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    private void clickMouse(CallbackInfo callbackInfo) {
        CPSCounter.registerClick(CPSCounter.MouseButton.LEFT);
        leftClickCounter = 0;
    }

    @Redirect(
            method = "clickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;swingItem()V")
    )
    private void fixAttackOrder_VanillaSwing() {
        AttackOrder.sendConditionalSwing(this.objectMouseOver);
    }

    @Redirect(
            method = "clickMouse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V")
    )
    private void fixAttackOrder_VanillaAttack() {
        AttackOrder.sendFixedAttack(this.thePlayer, this.objectMouseOver.entityHit);
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"))
    private void middleClickMouse(CallbackInfo ci) {
        CPSCounter.registerClick(CPSCounter.MouseButton.MIDDLE);
    }

    @Inject(method = "rightClickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I", shift = At.Shift.AFTER))
    private void rightClickMouse(final CallbackInfo callbackInfo) {
        CPSCounter.registerClick(CPSCounter.MouseButton.RIGHT);

        final FastPlace fastPlace = NightX.moduleManager.getModule(FastPlace.class);

        if (fastPlace.getState())
            rightClickDelayTimer = fastPlace.getSpeedValue().get();
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void loadWorld(WorldClient p_loadWorld_1_, String p_loadWorld_2_, final CallbackInfo callbackInfo) {
        NightX.eventManager.callEvent(new WorldEvent(p_loadWorld_1_));
    }

    @Inject(method = "getRenderViewEntity", at = @At("HEAD"))
    public void getRenderViewEntity(CallbackInfoReturnable<Entity> cir) {
        if (renderViewEntity instanceof EntityLivingBase && RotationUtils.serverRotation != null && thePlayer != null) {
            final SilentView silentView = NightX.moduleManager.getModule(SilentView.class);
            final KillAura killAura = NightX.moduleManager.getModule(KillAura.class);
            final Scaffold scaffold = NightX.moduleManager.getModule(Scaffold.class);
            final Annoy annoy = NightX.moduleManager.getModule(Annoy.class);
            final EntityLivingBase entityLivingBase = (EntityLivingBase) renderViewEntity;
            final float yaw = RotationUtils.serverRotation.getYaw();
            if (silentView.getState() && silentView.getMode().get().equals("Normal") && killAura.getTarget() != null) {
                entityLivingBase.rotationYawHead = yaw;
                entityLivingBase.renderYawOffset = yaw;
                entityLivingBase.prevRenderYawOffset = yaw;
            }
            if (silentView.getState() && silentView.getMode().get().equals("Normal") && scaffold.getState()) {
                entityLivingBase.rotationYawHead = yaw;
                entityLivingBase.renderYawOffset = yaw;
                entityLivingBase.prevRenderYawOffset = yaw;
            }
            if (silentView.getState() && silentView.getMode().get().equals("Normal") && annoy.getState()) {
                entityLivingBase.rotationYawHead = yaw;
                entityLivingBase.renderYawOffset = yaw;
                entityLivingBase.prevRenderYawOffset = yaw;
            }
        }
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void resolveScreenState(CallbackInfo ci) {
        if (!this.fullscreen && SystemUtils.IS_OS_WINDOWS) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }

    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventCharacter()C", remap = false))
    private char resolveForeignKeyboards() {
        return (char) (Keyboard.getEventCharacter() + 256);
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    private void sendClickBlockToController(boolean leftClick) {
        if (!leftClick)
            this.leftClickCounter = 0;

        if (this.leftClickCounter <= 0 && (!this.thePlayer.isUsingItem())) {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockPos = this.objectMouseOver.getBlockPos();

                if (this.leftClickCounter == 0)
                    NightX.eventManager.callEvent(new ClickBlockEvent(blockPos, this.objectMouseOver.sideHit));


                if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockPos, this.objectMouseOver.sideHit)) {
                    this.effectRenderer.addBlockHitEffects(blockPos, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    /**
     * @author CCBlueX
     */
    @ModifyConstant(method = "getLimitFramerate", constant = @Constant(intValue = 30))
    public int getLimitFramerate(int constant) {
        return 60;
    }
}