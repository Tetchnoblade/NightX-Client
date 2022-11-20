package net.aspw.nightx.utils.misc.sound

import net.aspw.nightx.NightX
import net.aspw.nightx.utils.FileUtils
import java.io.File

class TipSoundManager {
    var enableSound: TipSoundPlayer
    var disableSound: TipSoundPlayer

    init {
        val enableSoundFile = File(NightX.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(NightX.fileManager.soundsDir, "disable.wav")

        if (!enableSoundFile.exists())
            FileUtils.unpackFile(enableSoundFile, "assets/minecraft/nightx/sound/enable.wav")
        if (!disableSoundFile.exists())
            FileUtils.unpackFile(disableSoundFile, "assets/minecraft/nightx/sound/disable.wav")

        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
    }
}