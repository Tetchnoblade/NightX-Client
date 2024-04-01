package net.aspw.client.utils

import net.aspw.client.Launch
import okhttp3.*

object APIConnecter {

    var canConnect = false
    var isLatest = false
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var bmcstafflist = ""
    var mushstafflist = ""
    var hypixelstafflist = ""

    fun checkStatus() {
        try {
            var gotData: String
            val client = OkHttpClient()
            val builder = Request.Builder().url(URLComponent.STATUS)
            val request: Request = builder.build()
            client.newCall(request).execute().use { response ->
                gotData = response.body!!.string()
            }
            val details = gotData.split("///")
            isLatest = details[4] == Launch.CLIENT_VERSION
            discord = details[3]
            discordApp = details[2]
            appClientSecret = details[1]
            appClientID = details[0]
            canConnect = true
            ClientUtils.getLogger().info("Loaded API")
        } catch (e: Exception) {
            canConnect = false
            ClientUtils.getLogger().info("Failed to load API")
        }
    }

    fun checkStaffList() {
        try {
            var gotData: String
            val client = OkHttpClient()
            val builder = Request.Builder().url(URLComponent.STAFFS)
            val request: Request = builder.build()
            client.newCall(request).execute().use { response ->
                gotData = response.body!!.string()
            }
            val details = gotData.split("-")
            bmcstafflist = details[0]
            mushstafflist = details[1]
            hypixelstafflist = details[2]
            ClientUtils.getLogger().info("Loaded Staff List")
        } catch (e: Exception) {
            ClientUtils.getLogger().info("Failed to load Staff List")
        }
    }

    fun configLoad(name: String): String {
        var gotData = ""
        try {
            val client = OkHttpClient()
            val builder = Request.Builder().url(URLComponent.CONFIGS + name)
            val request: Request = builder.build()
            client.newCall(request).execute().use { response ->
                gotData = response.body!!.string()
            }
            ClientUtils.getLogger().info("Loaded Config Data")
        } catch (e: Exception) {
            ClientUtils.getLogger().info("Failed to load Config Data")
        }
        return gotData
    }

    fun configList(): String {
        var gotData = ""
        try {
            val client = OkHttpClient()
            val builder = Request.Builder().url(URLComponent.CONFIGLIST)
            val request: Request = builder.build()
            client.newCall(request).execute().use { response ->
                gotData = response.body!!.string()
            }
            ClientUtils.getLogger().info("Loaded Config List")
        } catch (e: Exception) {
            ClientUtils.getLogger().info("Failed to load Config List")
        }
        return gotData
    }
}