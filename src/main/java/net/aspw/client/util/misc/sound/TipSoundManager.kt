package net.aspw.client.util.misc.sound

import net.aspw.client.Client
import net.aspw.client.util.FileUtils
import java.io.File

class TipSoundManager {
    var enableSound: TipSoundPlayer
    var disableSound: TipSoundPlayer
    var popSound: TipSoundPlayer
    var swingSound: TipSoundPlayer
    var hwidSound: TipSoundPlayer

    init {
        val enableSoundFile = File(Client.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(Client.fileManager.soundsDir, "disable.wav")
        val popSoundFile = File(Client.fileManager.soundsDir, "pop.wav")
        val swingSoundFile = File(Client.fileManager.soundsDir, "swing.wav")
        val hwidSoundFile = File(Client.fileManager.soundsDir, "hwid.wav")

        if (!enableSoundFile.exists())
            FileUtils.unpackFile(enableSoundFile, "assets/minecraft/client/sound/enable.wav")
        if (!disableSoundFile.exists())
            FileUtils.unpackFile(disableSoundFile, "assets/minecraft/client/sound/disable.wav")
        if (!popSoundFile.exists())
            FileUtils.unpackFile(popSoundFile, "assets/minecraft/client/sound/pop.wav")
        if (!swingSoundFile.exists())
            FileUtils.unpackFile(swingSoundFile, "assets/minecraft/client/sound/swing.wav")
        if (!hwidSoundFile.exists())
            FileUtils.unpackFile(hwidSoundFile, "assets/minecraft/client/sound/hwid.wav")

        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
        popSound = TipSoundPlayer(popSoundFile)
        swingSound = TipSoundPlayer(swingSoundFile)
        hwidSound = TipSoundPlayer(hwidSoundFile)
    }
}