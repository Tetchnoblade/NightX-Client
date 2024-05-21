package net.aspw.client.utils

import net.aspw.client.Launch
import net.minecraft.client.renderer.texture.DynamicTexture
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

    var donorCapeLocations = mutableListOf<Pair<String, BufferedImage>>()
    var donorIDs = mutableListOf<String>()

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
            val nameBuilder = Request.Builder().url(URLComponent.DONORS + "users.txt")
            val nameRequest: Request = nameBuilder.build()
            nameClient.newCall(nameRequest).execute().use { response ->
                gotNames = response.body!!.string()
            }
            val details = gotNames.split("///")
            for (i in details) {
                var gotCapes: BufferedImage
                val name = i.split(":")[0]
                val cape = i.split(":")[1]
                tlsAuthConnectionFixes()
                val donorClient = OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
                val donorBuilder = Request.Builder().url(URLComponent.DONORS + cape)
                val donorRequest: Request = donorBuilder.build()
                donorClient.newCall(donorRequest).execute().use { response ->
                    gotCapes = response.body?.byteStream().let { ImageIO.read(it) }
                }
                donorIDs.add(name)
                donorCapeLocations.add(Pair(name, gotCapes))
            }
            canConnect = true
            ClientUtils.getLogger().info("Loaded Donor Capes")
        } catch (e: Exception) {
            canConnect = false
            ClientUtils.getLogger().info("Failed to load Donor Capes")
        }
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