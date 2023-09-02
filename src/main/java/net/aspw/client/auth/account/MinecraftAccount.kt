package net.aspw.client.auth.account

import com.google.gson.JsonObject
import net.aspw.client.auth.compat.Session

abstract class MinecraftAccount(val type: String) {
    /**
     * get display name of this account
     */
    abstract val name: String

    /**
     * get the session of the account
     */
    abstract val session: Session

    /**
     * login with this account info
     * @throws me.liuli.elixir.exception.LoginException if login failed
     */
    abstract fun update()

    /**
     * save the account data to json
     * @param json needs to write data in
     */
    abstract fun toRawJson(json: JsonObject)

    /**
     * load the account data from json
     * @param json contains the account data
     */
    abstract fun fromRawJson(json: JsonObject)
}