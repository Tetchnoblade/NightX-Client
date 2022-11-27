package net.aspw.nightx.features.module

import net.aspw.nightx.NightX
import net.aspw.nightx.event.Listenable
import net.aspw.nightx.ui.client.hud.element.elements.Notification
import net.aspw.nightx.utils.ClientUtils
import net.aspw.nightx.utils.MinecraftInstance
import net.aspw.nightx.value.Value
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

open class Module : MinecraftInstance(), Listenable {

    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    var name: String
    var spacedName: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!NightX.isStarting)
                NightX.fileManager.saveConfig(NightX.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!NightX.isStarting)
                NightX.fileManager.saveConfig(NightX.fileManager.modulesConfig)
        }
    private val canEnable: Boolean
    private val onlyEnable: Boolean
    private val forceNoSound: Boolean

    var slideStep = 0F
    var animation = 0F

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

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!NightX.isStarting && !forceNoSound) {
                when (NightX.moduleManager.toggleSoundMode) {
                    1 -> mc.soundHandler.playSound(
                        PositionedSoundRecord.create(
                            ResourceLocation("random.click"),
                            1F
                        )
                    )

                    2 -> (if (value) NightX.tipSoundManager.enableSound else NightX.tipSoundManager.disableSound).asyncPlay(
                        NightX.moduleManager.toggleVolume
                    )
                }
                if (NightX.moduleManager.shouldNotify)
                    NightX.hud.addNotification(
                        Notification(
                            "${if (value) "Enabled" else "Disabled"} §r$name",
                            if (value) Notification.Type.SUCCESS else Notification.Type.ERROR,
                            1000L
                        )
                    )
            }

            // Call on enabled or disabled
            if (value) {
                onEnable()

                if (!onlyEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            // Save module state
            NightX.fileManager.saveConfig(NightX.fileManager.modulesConfig)
        }


    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F
    var arrayY = 0F

    // Tag
    open val tag: String?
        get() = null
    /*
        val tagName: String
            get() = "$name${if (tag == null) "" else "§7 - $tag"}"

        val colorlessTagName: String
            get() = "$name${if (tag == null) "" else " - " + stripColor(tag)}"
    */
    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage("§f§l[§d§lN§7§lightX§f§l] §3$msg")

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