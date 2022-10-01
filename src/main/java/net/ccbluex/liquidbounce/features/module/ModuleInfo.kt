package net.ccbluex.liquidbounce.features.module

import org.lwjgl.input.Keyboard

@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleInfo(
    val name: String,
    val spacedName: String = "",
    val description: String,
    val category: ModuleCategory,
    val keyBind: Int = Keyboard.CHAR_NONE,
    val canEnable: Boolean = true,
    val onlyEnable: Boolean = false,
    val forceNoSound: Boolean = false,
    val array: Boolean = true
)
