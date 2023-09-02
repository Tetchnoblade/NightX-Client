package net.aspw.client.auth.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

// makes gson api access much easier in Kotlin

operator fun JsonObject.set(key: String, value: JsonElement) {
    this.add(key, value)
}

operator fun JsonObject.set(key: String, value: Char) {
    this.addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: Number) {
    this.addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: String) {
    this.addProperty(key, value)
}

operator fun JsonObject.set(key: String, value: Boolean) {
    this.addProperty(key, value)
}

fun JsonElement.toJsonString(prettyPrint: Boolean = false): String {
    val gson = if (prettyPrint) {
        GsonBuilder().setPrettyPrinting().create()
    } else {
        GsonBuilder().create()
    }
    return gson.toJson(this)
}

fun JsonObject.string(key: String): String? {
    return if (this.has(key)) {
        this.get(key).asString
    } else {
        null
    }
}

fun JsonObject.int(key: String): Int? {
    return if (this.has(key)) {
        this.get(key).asInt
    } else {
        null
    }
}

fun JsonObject.long(key: String): Long? {
    return if (this.has(key)) {
        this.get(key).asLong
    } else {
        null
    }
}

fun JsonObject.double(key: String): Double? {
    return if (this.has(key)) {
        this.get(key).asDouble
    } else {
        null
    }
}

fun JsonObject.boolean(key: String): Boolean? {
    return if (this.has(key)) {
        this.get(key).asBoolean
    } else {
        null
    }
}

fun JsonObject.obj(key: String): JsonObject? {
    return if (this.has(key)) {
        this.get(key).asJsonObject
    } else {
        null
    }
}

fun JsonObject.array(key: String): JsonArray? {
    return if (this.has(key)) {
        this.get(key).asJsonArray
    } else {
        null
    }
}

fun JsonArray.string(index: Int): String? {
    return if (this.size() > index) {
        this.get(index).asString
    } else {
        null
    }
}

fun JsonArray.int(index: Int): Int? {
    return if (this.size() > index) {
        this.get(index).asInt
    } else {
        null
    }
}

fun JsonArray.long(index: Int): Long? {
    return if (this.size() > index) {
        this.get(index).asLong
    } else {
        null
    }
}

fun JsonArray.double(index: Int): Double? {
    return if (this.size() > index) {
        this.get(index).asDouble
    } else {
        null
    }
}

fun JsonArray.boolean(index: Int): Boolean? {
    return if (this.size() > index) {
        this.get(index).asBoolean
    } else {
        null
    }
}

fun JsonArray.obj(index: Int): JsonObject? {
    return if (this.size() > index) {
        this.get(index).asJsonObject
    } else {
        null
    }
}

fun JsonArray.array(index: Int): JsonArray? {
    return if (this.size() > index) {
        this.get(index).asJsonArray
    } else {
        null
    }
}

