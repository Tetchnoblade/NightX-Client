package net.aspw.client.features.module.impl.other

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.util.ClientUtils
import kotlin.concurrent.thread

@ModuleInfo(name = "DiscordRPC", spacedName = "Discord RPC", description = "", category = ModuleCategory.OTHER)
class DiscordRPC : Module() {
    override fun onEnable() {
        thread {
            try {
                Client.discordRPC.setup()
            } catch (throwable: Throwable) {
                ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
            }
        }
    }

    override fun onDisable() {
        Client.discordRPC.shutdown()
    }
}