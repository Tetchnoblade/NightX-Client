package net.aspw.client.value

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.aspw.client.Client
import net.aspw.client.utils.ClientUtils
import net.aspw.client.visual.font.Fonts
import net.minecraft.client.gui.FontRenderer
import java.util.*

abstract class Value<T>(val name: String, var value: T, var canDisplay: () -> Boolean) {

    fun set(newValue: T) {
        if (newValue == value) return

        val oldValue = get()

        try {
            onChange(oldValue, newValue)
            changeValue(newValue)
            onChanged(oldValue, newValue)
            Client.fileManager.saveConfig(Client.fileManager.valuesConfig)
        } catch (e: Exception) {
            ClientUtils.getLogger()
                .error("[ValueSystem ($name)]: ${e.javaClass.name} (${e.message}) [$oldValue >> $newValue]")
        }
    }

    fun get() = value

    open fun changeValue(value: T) {
        this.value = value
    }

    abstract fun toJson(): JsonElement?
    abstract fun fromJson(element: JsonElement)

    protected open fun onChange(oldValue: T, newValue: T) {}
    protected open fun onChanged(oldValue: T, newValue: T) {}

}

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean, displayable: () -> Boolean) :
    Value<Boolean>(name, value, displayable) {

    constructor(name: String, value: Boolean) : this(name, value, { true })

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
    }

}

/**
 * Integer value represents a value with a integer
 */
open class IntegerValue(
    name: String,
    value: Int,
    val minimum: Int = 0,
    val maximum: Int = Integer.MAX_VALUE,
    val suffix: String,
    displayable: () -> Boolean
) : Value<Int>(name, value, displayable) {

    constructor(name: String, value: Int, minimum: Int, maximum: Int, displayable: () -> Boolean) : this(
        name,
        value,
        minimum,
        maximum,
        "",
        displayable
    )

    constructor(name: String, value: Int, minimum: Int, maximum: Int, suffix: String) : this(
        name,
        value,
        minimum,
        maximum,
        suffix,
        { true })

    constructor(name: String, value: Int, minimum: Int, maximum: Int) : this(name, value, minimum, maximum, { true })

    fun set(newValue: Number) {
        set(newValue.toInt())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asInt
    }

}

/**
 * Float value represents a value with a float
 */
open class FloatValue(
    name: String,
    value: Float,
    val minimum: Float = 0F,
    val maximum: Float = Float.MAX_VALUE,
    val suffix: String,
    displayable: () -> Boolean
) : Value<Float>(name, value, displayable) {

    constructor(name: String, value: Float, minimum: Float, maximum: Float, displayable: () -> Boolean) : this(
        name,
        value,
        minimum,
        maximum,
        "",
        displayable
    )

    constructor(name: String, value: Float, minimum: Float, maximum: Float, suffix: String) : this(
        name,
        value,
        minimum,
        maximum,
        suffix,
        { true })

    constructor(name: String, value: Float, minimum: Float, maximum: Float) : this(
        name,
        value,
        minimum,
        maximum,
        { true })

    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asFloat
    }

}

/**
 * Text value represents a value with a string
 */
open class TextValue(name: String, value: String, displayable: () -> Boolean) :
    Value<String>(name, value, displayable) {

    constructor(name: String, value: String) : this(name, value, { true })

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asString
    }
}
/*
open class ColorValue(name: String, value: Color, val transparent: Boolean, displayable: () -> Boolean) : Value<Color>(name, value, displayable) {

    constructor(name: String, value: Color, transparent: Boolean): this(name, value, transparent, { true } )

    fun set(hue: Float, saturation: Float, brightness: Float, alpha: Float) = set(Color(Color.HSBtoRGB(hue, saturation, brightness)).setAlpha(alpha))

    override fun toJson(): JsonElement? {
        val valueObject = JsonObject()
        valueObject.addProperty("red", value.red)
        valueObject.addProperty("green", value.green)
        valueObject.addProperty("blue", value.blue)
        valueObject.addProperty("alpha", value.alpha)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Color(valueObject["red"].asInt, valueObject["green"].asInt, valueObject["blue"].asInt, valueObject["alpha"].asInt)
    }

}
*/
/**
 * Font value represents a value with a font
 */
class FontValue(valueName: String, value: FontRenderer, displayable: () -> Boolean) :
    Value<FontRenderer>(valueName, value, displayable) {

    constructor(valueName: String, value: FontRenderer) : this(valueName, value, { true })

    override fun toJson(): JsonElement? {
        val fontDetails = Fonts.getFontDetails(value) ?: return null
        val valueObject = JsonObject()
        valueObject.addProperty("fontName", fontDetails[0] as String)
        valueObject.addProperty("fontSize", fontDetails[1] as Int)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Fonts.getFontRenderer(valueObject["fontName"].asString, valueObject["fontSize"].asInt)
    }
}

/**
 * Block value represents a value with a block
 */
class BlockValue(name: String, value: Int, displayable: () -> Boolean) :
    IntegerValue(name, value, 1, 197, displayable) {
    constructor(name: String, value: Int) : this(name, value, { true })
}

/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: Array<String>, value: String, displayable: () -> Boolean) :
    Value<String>(name, value, displayable) {

    constructor(name: String, values: Array<String>, value: String) : this(name, values, value, { true })

    @JvmField
    var openList = false

    init {
        this.value = value
    }

    operator fun contains(string: String?): Boolean {
        return Arrays.stream(values).anyMatch { s: String -> s.equals(string, ignoreCase = true) }
    }

    override fun changeValue(value: String) {
        for (element in values) {
            if (element.equals(value, ignoreCase = true)) {
                this.value = element
                break
            }
        }
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) changeValue(element.asString)
    }


}