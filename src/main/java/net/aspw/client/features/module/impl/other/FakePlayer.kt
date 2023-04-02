package net.aspw.client.features.module.impl.other

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.minecraft.client.entity.EntityOtherPlayerMP

@ModuleInfo(name = "FakePlayer", spacedName = "Fake Player", category = ModuleCategory.OTHER)
class FakePlayer : Module() {
    private var fakePlayer: EntityOtherPlayerMP? = null

    override fun onEnable() {
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        mc.theWorld.addEntityToWorld(-1000, fakePlayer)
    }

    override fun onDisable() {
        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null
    }
}