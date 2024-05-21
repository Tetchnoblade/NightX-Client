package net.aspw.client.utils

import net.aspw.client.Launch
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.io.path.exists


object APIConnecter {

    var canConnect = false
    var isLatest = false
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var changelogs = ""
    var bugs = ""
    var bmcstafflist = ""
    var mushstafflist = ""
    var hypixelstafflist = ""

    private var donorCapeLocations = mutableListOf<Pair<String, ResourceLocation>>()
    private var donorIDs = mutableListOf<String>()

    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
            return arrayOf()
        }
    })
    private val sslContext = SSLContext.getInstance("TLS")

    private fun tlsAuthConnectionFixes() {
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    }

    fun loadDonors() {
        try {
            var gotNames: String
            tlsAuthConnectionFixes()
            val nameClient = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
            val builder = Request.Builder().url(URLComponent.DONORS + "users.txt")
            val nameRequest: Request = builder.build()
            nameClient.newCall(nameRequest).execute().use { response ->
                gotNames = response.body!!.string()
            }
            val details = gotNames.split("///")
            for (i in details) {
                var gotCapeImage: BufferedImage?
                val name = i.split(":")[0]
                val cape = i.split(":")[1]
                tlsAuthConnectionFixes()
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(URLComponent.DONORS + cape)
                    .build()
                client.newCall(request).execute().use { response ->
                    gotCapeImage = response.body?.byteStream()?.let { ImageIO.read(it) }
                }
                donorIDs.add(name)
                val file = File(Launch.fileManager.capesDir, cape)
                ImageIO.write(gotCapeImage, "PNG", file)
                val sourcePath = Paths.get(Launch.fileManager.capesDir.canonicalPath, cape)
                val targetPath = Paths.get("src", "main", "resources", "assets", "minecraft", "client", "cape", "cache", cape)
                if (sourcePath.exists() && targetPath.exists()) {
                    ClientUtils.getLogger().info("Copying...")
                    Files.copy(sourcePath, targetPath)
                }
                donorCapeLocations.add(Pair(name, ResourceLocation("client/cape/cache/$cape")))
            }
            canConnect = true
            ClientUtils.getLogger().info("Loaded Donor Capes")
        } catch (e: Exception) {
            println(e)
            canConnect = false
            ClientUtils.getLogger().info("Failed to load Donor Capes")
        }
    }

    fun loadDonorCape(player: EntityPlayer): ResourceLocation? {
        if (player.name in donorIDs) {
            for ((i, l) in donorCapeLocations) {
                if (i == player.name)
                    return l
            }
        }
        return null
    }

    fun checkStatus() {
        try {
            var gotData: String
            tlsAuthConnectionFixes()
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
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

    fun checkChangelogs() {
        try {
            var gotData: String
            tlsAuthConnectionFixes()
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
            val builder = Request.Builder().url(URLComponent.CHANGELOGS)
            val request: Request = builder.build()
            client.newCall(request).execute().use { response ->
                gotData = response.body!!.string()
            }
            changelogs = gotData
            ClientUtils.getLogger().info("Loaded Changelogs")
        } catch (e: Exception) {
            ClientUtils.getLogger().info("Failed to load Changelogs")
        }
    }

    fun checkBugs() {
        try {
            var gotData: String
            tlsAuthConnectionFixes()
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
            val builder = Request.Builder().url(URLComponent.BUGS)
            val request: Request = builder.build()
            client.newCall(request).execute().use { response ->
                gotData = response.body!!.string()
            }
            bugs = gotData
            ClientUtils.getLogger().info("Loaded Bugs")
        } catch (e: Exception) {
            ClientUtils.getLogger().info("Failed to load Bugs")
        }
    }

    fun checkStaffList() {
        try {
            var gotData: String
            tlsAuthConnectionFixes()
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
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
            tlsAuthConnectionFixes()
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
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
            tlsAuthConnectionFixes()
            val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
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