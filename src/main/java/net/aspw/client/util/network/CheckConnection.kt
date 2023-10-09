package net.aspw.client.util.network

import net.aspw.client.Client
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object CheckConnection {

    // Old Auth System
    //var userList = ""
    var canConnect = false
    var announcement = ""
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var clientContributors = ""
    var clientRealContributors = ""
    var srgFile = ""
    var changeLog1 = ""
    var changeLog2 = ""
    var changeLog3 = ""
    var changeLog4 = ""
    var changeLog5 = ""
    var changeLog6 = ""
    var changeLog7 = ""
    var changeLog8 = ""
    var changeLog9 = ""
    var changeLog10 = ""
    var changeLog11 = ""
    var changeLog12 = ""
    var changeLog13 = ""
    var changeLog14 = ""
    var changeLog15 = ""
    var changeLog16 = ""
    var changeLog17 = ""
    var changeLog18 = ""
    var changeLog19 = ""
    var changeLog20 = ""
    var changeLog21 = ""
    var changeLog22 = ""
    var changeLog23 = ""
    var changeLog24 = ""
    var changeLog25 = ""
    var changeLog26 = ""
    var changeLog27 = ""
    var changeLog28 = ""
    var changeLog29 = ""
    var changeLog30 = ""
    var changeLog31 = ""
    var changeLog32 = ""
    var changeLog33 = ""
    var changeLog34 = ""
    var changeLog35 = ""
    var changeLog36 = ""
    var changeLog37 = ""
    var changeLog38 = ""
    var changeLog39 = ""
    var changeLog40 = ""
    var changeLog41 = ""
    var changeLog42 = ""
    var changeLog43 = ""
    var changeLog44 = ""
    var changeLog45 = ""
    var changeLog46 = ""
    var changeLog47 = ""
    var changeLog48 = ""
    var changeLog49 = ""
    var changeLog50 = ""
    var isLatest = false
    var isAvailable = false

    fun getSRG() {
        val httpClient: CloseableHttpClient = HttpClients.createDefault()
        val request = HttpGet(Client.CLIENT_SRG)
        val response = httpClient.execute(request)
        val entity = response.entity
        val content = EntityUtils.toString(entity)
        srgFile = content
        EntityUtils.consume(entity)
        response.close()
        httpClient.close()
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

    fun getContributors() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Client.CLIENT_CONTRIBUTORS)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            clientContributors = content
            EntityUtils.consume(entity)
            response.close()
            httpClient.close()
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
        }
    }

    fun getRealContributors() {
        try {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val request = HttpGet(Client.CLIENT_INFORMATION)
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)
            clientRealContributors = content
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
            val details = content.split("///")
            isAvailable = details[0] == "True"
            isLatest = details[1] == Client.CLIENT_VERSION
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
            isLatest = false
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
            val slashLog = details[4].split("---")
            changeLog1 = slashLog[0]
            changeLog2 = slashLog[1]
            changeLog3 = slashLog[2]
            changeLog4 = slashLog[3]
            changeLog5 = slashLog[4]
            changeLog6 = slashLog[5]
            changeLog7 = slashLog[6]
            changeLog8 = slashLog[7]
            changeLog9 = slashLog[8]
            changeLog10 = slashLog[9]
            changeLog11 = slashLog[10]
            changeLog12 = slashLog[11]
            changeLog13 = slashLog[12]
            changeLog14 = slashLog[13]
            changeLog15 = slashLog[14]
            changeLog16 = slashLog[15]
            changeLog17 = slashLog[16]
            changeLog18 = slashLog[17]
            changeLog19 = slashLog[18]
            changeLog20 = slashLog[19]
            changeLog21 = slashLog[20]
            changeLog22 = slashLog[21]
            changeLog23 = slashLog[22]
            changeLog24 = slashLog[23]
            changeLog25 = slashLog[24]
            changeLog26 = slashLog[25]
            changeLog27 = slashLog[26]
            changeLog28 = slashLog[27]
            changeLog29 = slashLog[28]
            changeLog30 = slashLog[29]
            changeLog31 = slashLog[30]
            changeLog32 = slashLog[31]
            changeLog33 = slashLog[32]
            changeLog34 = slashLog[33]
            changeLog35 = slashLog[34]
            changeLog36 = slashLog[35]
            changeLog37 = slashLog[36]
            changeLog38 = slashLog[37]
            changeLog39 = slashLog[38]
            changeLog40 = slashLog[39]
            changeLog41 = slashLog[40]
            changeLog42 = slashLog[41]
            changeLog43 = slashLog[42]
            changeLog44 = slashLog[43]
            changeLog45 = slashLog[44]
            changeLog46 = slashLog[45]
            changeLog47 = slashLog[46]
            changeLog48 = slashLog[47]
            changeLog49 = slashLog[48]
            changeLog50 = slashLog[49]
            canConnect = true
        } catch (e: Exception) {
            canConnect = false
        }
    }

    // Old Auth System
    //fun getUserList() {
    //    val httpClient: CloseableHttpClient = HttpClients.createDefault()
    //    val request = HttpGet(Client.CLIENT_USER)
    //    val response = httpClient.execute(request)
    //    val entity = response.entity
    //    val content = EntityUtils.toString(entity)
    //    EntityUtils.consume(entity)
    //    response.close()
    //    httpClient.close()
    //    userList = content
    //}
}