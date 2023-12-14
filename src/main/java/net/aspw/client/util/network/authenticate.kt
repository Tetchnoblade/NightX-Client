package net.aspw.client.util.network

import net.aspw.client.Client
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.MinecraftInstance
import net.aspw.client.visual.client.GuiMainMenu

fun main() {
    val data = "" //Access.userList
    val usernameInput = readln()
    val passwordInput = readln()
    val hwidInput = readln()
    val uidInput = readln()
    authenticate(data, usernameInput, passwordInput, hwidInput, uidInput)
}

fun authenticate(authInfo: String, username: String, password: String, hwid: String, uid: String) {
    Access.checkLatestVersion()
    Access.getAnnouncement()
    if (!Access.isLatest) return
    val users = authInfo.split("///")
    for (user in users) {
        val userInfo = user.split(':')
        if ((userInfo[0] == username && userInfo[1] == password || userInfo[3] == uid) && userInfo[2] == hwid) {
            LoginID.id = userInfo[0]
            LoginID.password = userInfo[1]
            LoginID.hwid = userInfo[2]
            LoginID.uid = userInfo[3]
            //Access.userList = ""
            Access.canConnect = true
            LoginID.loggedIn = true
            if (Client.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                Client.tipSoundManager.popSound.asyncPlay(Client.moduleManager.popSoundPower)
            }
            ClientUtils.getLogger().info("Logged in with Your Account!")
            MinecraftInstance.mc.displayGuiScreen(GuiMainMenu())
            return
        }
    }
    //Access.userList = ""
    ClientUtils.getLogger().info("Incorrect Your Information!")
}