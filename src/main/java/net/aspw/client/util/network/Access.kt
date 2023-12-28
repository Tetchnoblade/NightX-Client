package net.aspw.client.util.network

import net.aspw.client.Client
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object Access {

    //var userList = ""
    var canConnect = false
    var announcement = ""
    var latestVersion = ""
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var bmcstafflist = ""
    var mushstafflist = ""
    var isLatest = false

    fun getUserList() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            //val request = HttpGet(Client.CLIENT_USERS)
            //val response = httpClient.execute(request)
            //val entity = response.entity
            //val content = EntityUtils.toString(entity)
            //userList = content
            //EntityUtils.consume(entity)
            //response.close()
            httpClient.close()
        } catch (_: Exception) {
        }
    }

    fun getAnnouncement() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Client.CLIENT_ANNOUNCEMENT)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            announcement = content
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
        }
    }

    fun checkLatestVersion() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Client.CLIENT_LATEST)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            latestVersion = content
            isLatest = content == Client.CLIENT_VERSION
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
            isLatest = false
        }
    }

    fun checkStaffList() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet("${Client.CLIENT_WEBSITE}/staffs/blocksmc.txt")
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
            val request = HttpGet("${Client.CLIENT_WEBSITE}/staffs/mushmc.txt")
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
    }

    fun checkStatus() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Client.CLIENT_STATUS)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            val details = content.split("///")
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