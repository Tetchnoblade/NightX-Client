package net.aspw.client.utils.misc.sound

import net.aspw.client.Launch
import net.aspw.client.utils.FileUtils
import java.io.File

class TipSoundManager {
    var enableSound: TipSoundPlayer
    var disableSound: TipSoundPlayer
    var popSound: TipSoundPlayer
    var swingSound: TipSoundPlayer

    init {
        val enableSoundFile = File(Launch.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(Launch.fileManager.soundsDir, "disable.wav")
        val popSoundFile = File(Launch.fileManager.soundsDir, "pop.wav")
        val swingSoundFile = File(Launch.fileManager.soundsDir, "swing.wav")

        if (!enableSoundFile.exists())
            FileUtils.unpackFile(enableSoundFile, "assets/minecraft/client/sound/enable.wav")
        if (!disableSoundFile.exists())
            FileUtils.unpackFile(disableSoundFile, "assets/minecraft/client/sound/disable.wav")
        if (!popSoundFile.exists())
            FileUtils.unpackFile(popSoundFile, "assets/minecraft/client/sound/pop.wav")
        if (!swingSoundFile.exists())
            FileUtils.unpackFile(swingSoundFile, "assets/minecraft/client/sound/swing.wav")

        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
        popSound = TipSoundPlayer(popSoundFile)
        swingSound = TipSoundPlayer(swingSoundFile)
    }
}