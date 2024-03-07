package net.aspw.client.utils.login

import com.google.gson.JsonParser
import net.aspw.client.Launch
import net.aspw.client.event.SessionEvent
import net.aspw.client.utils.MinecraftInstance
import net.minecraft.util.Session
import java.util.*

object LoginUtils : MinecraftInstance() {

    @JvmStatic
    fun loginSessionId(sessionId: String): LoginResult {
        val decodedSessionData = try {
            String(Base64.getDecoder().decode(sessionId.split(".")[1]), Charsets.UTF_8)
        } catch (e: Exception) {
            return LoginResult.FAILED_PARSE_TOKEN
        }

        val sessionObject = try {
            JsonParser().parse(decodedSessionData).asJsonObject
        } catch (e: java.lang.Exception) {
            return LoginResult.FAILED_PARSE_TOKEN
        }
        val uuid = sessionObject.get("spr").asString
        val accessToken = sessionObject.get("yggt").asString

        if (!UserUtils.isValidToken(accessToken)) {
            return LoginResult.INVALID_ACCOUNT_DATA
        }

        val username = UserUtils.getUsername(uuid) ?: return LoginResult.INVALID_ACCOUNT_DATA

        mc.session = Session(username, uuid, accessToken, "mojang")
        Launch.eventManager.callEvent(SessionEvent())

        return LoginResult.LOGGED
    }

    enum class LoginResult {
        INVALID_ACCOUNT_DATA, LOGGED, FAILED_PARSE_TOKEN
    }

}