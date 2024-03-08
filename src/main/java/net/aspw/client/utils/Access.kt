package net.aspw.client.utils

import net.aspw.client.Launch
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Access {

    var canConnect = false
    var clientGithub = ""
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var bmcstafflist = ""
    var mushstafflist = ""
    var hypixelstafflist = ""
    var isLatest = false

    fun checkLatestVersion() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Launch.CLIENT_LATEST)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            val details = content.split("-")
            if (details[0] == Launch.CLIENT_VERSION || details[1] == Launch.CLIENT_VERSION)
                isLatest = true
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
            isLatest = false
        }
    }

    fun checkStaffList() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet("${Launch.CLIENT_WEBSITE}/staff/blocksmc.txt")
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            bmcstafflist = content
        } catch (e: Exception) {
            canConnect = false
        }

        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet("${Launch.CLIENT_WEBSITE}/staff/mushmc.txt")
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            mushstafflist = content
        } catch (e: Exception) {
            canConnect = false
        }

        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet("${Launch.CLIENT_WEBSITE}/staff/hypixel.txt")
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            hypixelstafflist = content
        } catch (e: Exception) {
            canConnect = false
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
            clientGithub = details[4]
            discord = details[3]
            discordApp = details[2]
            appClientSecret = details[1]
            appClientID = details[0]
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
        }
    }
}