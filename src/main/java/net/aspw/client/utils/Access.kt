package net.aspw.client.utils

import net.aspw.client.Launch
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Access {

    var canConnect = false
    var isLatest = false
    var clientGithub = ""
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var bmcstafflist = ""
    var mushstafflist = ""
    var hypixelstafflist = ""

    fun checkStaffList() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Launch.CLIENT_STAFFS)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            val details = content.split("-")
            bmcstafflist = details[0]
            mushstafflist = details[1]
            hypixelstafflist = details[2]
            ClientUtils.getLogger().info("Loaded Staff List")
        } catch (e: Exception) {
            ClientUtils.getLogger().info("Failed to load Staff List")
        }
    }

    fun checkStatus() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Launch.CLIENT_STATUS)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            val details = content.split("///")
            isLatest = details[5] == Launch.CLIENT_VERSION
            clientGithub = details[4]
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
}