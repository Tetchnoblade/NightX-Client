package net.aspw.client.features.module.impl.visual

import net.aspw.client.Client
import net.aspw.client.event.EventTarget
import net.aspw.client.event.TextEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.utils.misc.StringUtils
import net.aspw.client.utils.render.ColorUtils.translateAlternateColorCodes
import net.aspw.client.value.BoolValue
import net.aspw.client.value.TextValue

@ModuleInfo(name = "StreamerMode", spacedName = "Streamer Mode", category = ModuleCategory.VISUAL)
class StreamerMode : Module() {
    val selfValue = BoolValue("Yourself", true)
    val tagValue = BoolValue("Tag", false)
    val allPlayersValue = BoolValue("AllPlayers", false)
    private val fakeNameValue = TextValue("FakeName", "User")
    private val allFakeNameValue = TextValue("AllPlayersFakeName", "Censored")

    @EventTarget
    fun onText(event: TextEvent) {
        if (mc.thePlayer == null || event.text!!.contains(Client.CLIENT_CHAT + "§3") || event.text!!.startsWith("/") || event.text!!.startsWith(
                Client.commandManager.prefix.toString() + ""
            )
        ) return
        for (friend in Client.fileManager.friendsConfig.friends) event.text =
            StringUtils.replace(event.text, friend.playerName, translateAlternateColorCodes(friend.alias) + "§f")
        event.text = StringUtils.replace(
            event.text,
            mc.thePlayer.name,
            if (selfValue.get()) (if (tagValue.get()) StringUtils.injectAirString(mc.thePlayer.name) + " §7(§a" + translateAlternateColorCodes(
                fakeNameValue.get() + "§7)"
            ) else translateAlternateColorCodes(fakeNameValue.get()) + "§r") else mc.thePlayer.name
        )
        if (allPlayersValue.get()) for (playerInfo in mc.netHandler.playerInfoMap) event.text = StringUtils.replace(
            event.text,
            playerInfo.gameProfile.name,
            translateAlternateColorCodes(allFakeNameValue.get()) + "§f"
        )
    }
}