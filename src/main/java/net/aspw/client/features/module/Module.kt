package net.aspw.client.features.module

import net.aspw.client.Client
import net.aspw.client.event.Listenable
import net.aspw.client.utils.ClientUtils
import net.aspw.client.utils.MinecraftInstance
import net.aspw.client.value.Value
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

abstract class Module : MinecraftInstance(), Listenable {

    // Module information
    var name: String
    var spacedName: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!Client.isStarting)
                Client.fileManager.saveConfig(Client.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!Client.isStarting)
                Client.fileManager.saveConfig(Client.fileManager.modulesConfig)
        }
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

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!Client.isStarting && !forceNoSound) {
                when (Client.moduleManager.toggleSoundMode) {
                    1 -> mc.soundHandler.playSound(
                        PositionedSoundRecord.create(
                            ResourceLocation("random.click"),
                            1F
                        )
                    )

                    2 -> (if (value) Client.tipSoundManager.enableSound else Client.tipSoundManager.disableSound).asyncPlay(
                        Client.moduleManager.toggleVolume
                    )
                }
                if (Client.moduleManager.shouldNotify)
                    Client.hud.addNotification(
                        Notification(
                            "${if (value) "Enabled" else "Disabled"} §r$name.",
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
            Client.fileManager.saveConfig(Client.fileManager.modulesConfig)
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
    protected fun chat(msg: String) = ClientUtils.displayChatMessage(Client.CLIENT_CHAT + "§3$msg")

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