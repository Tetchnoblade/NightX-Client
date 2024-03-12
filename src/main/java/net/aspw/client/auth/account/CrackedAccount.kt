package net.aspw.client.auth.account

import com.google.gson.JsonObject
import net.aspw.client.auth.compat.Session
import net.aspw.client.auth.utils.set
import net.aspw.client.auth.utils.string
import java.util.UUID

class CrackedAccount : MinecraftAccount("Cracked") {
    override var name = "Player"

    override val session: Session
        get() = Session(name, UUID.nameUUIDFromBytes(name.toByteArray(Charsets.UTF_8)).toString(), "-", "legacy")

    override fun update() {
        // has nothing to update with cracked account
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = name
    }

    override fun fromRawJson(json: JsonObject) {
        name = json.string("name")!!
    }
}
