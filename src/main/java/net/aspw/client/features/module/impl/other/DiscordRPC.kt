package net.aspw.client.features.module.impl.other

import net.aspw.client.Launch
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo

@ModuleInfo(name = "DiscordRPC", spacedName = "Discord RPC", category = ModuleCategory.OTHER)
class DiscordRPC : Module() {
    override fun onEnable() {
        Launch.discordRPC.setup()
    }

    override fun onDisable() {
        Launch.discordRPC.shutdown()
    }
}