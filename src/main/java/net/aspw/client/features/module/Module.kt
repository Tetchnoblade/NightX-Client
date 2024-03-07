package net.aspw.client.features.module

import net.aspw.client.Launch
import net.aspw.client.event.Listenable
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.MinecraftInstance
import net.aspw.client.value.Value
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

abstract class Module : MinecraftInstance(), Listenable {

    // Module information
    var name: String
    var spacedName: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
    var array = true
    private val canEnable: Boolean
    private val onlyEnable: Boolean
    private val forceNoSound: Boolean

    var slideStep = 0F

    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!

        name = moduleInfo.name
        spacedName = if (moduleInfo.spacedName == "") name else moduleInfo.spacedName
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
        onlyEnable = moduleInfo.onlyEnable
        forceNoSound = moduleInfo.forceNoSound
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value || !canEnable) return

            // Call on enabled or disabled
            if (value) {
                if (!onlyEnable) field = true
                onEnable()

            } else {
                field = false
                onDisable()
            }

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!Launch.isStarting && !forceNoSound) {
                when (Launch.moduleManager.toggleSoundMode) {
                    1 -> mc.soundHandler.playSound(
                        PositionedSoundRecord.create(
                            ResourceLocation("random.click"),
                            1F
                        )
                    )

                    2 -> (if (value) Launch.tipSoundManager.enableSound else Launch.tipSoundManager.disableSound).asyncPlay(
                        Launch.moduleManager.toggleVolume
                    )
                }
                if (Launch.moduleManager.shouldNotify)
                    chat("${if (value) "Enabled" else "Disabled"} §r$name")
            }
        }

    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F
    var arrayY = 0F

    // Tag
    open val tag: String?
        get() = null

    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage(Launch.CLIENT_CHAT + "§c$msg")

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Called when module initialized
     */
    open fun onInitialize() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}