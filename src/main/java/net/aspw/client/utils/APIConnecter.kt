package net.aspw.client.utils

import net.aspw.client.Launch
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object APIConnecter {

    var canConnect = false
    var isLatest = false
    var discord = ""
    var discordApp = ""
    var appClientID = ""
    var appClientSecret = ""
    var donate = ""
    var changelogs = ""
    var bugs = ""
    var bmcstafflist = ""
    var mushstafflist = ""
    var hypixelstafflist = ""

    var maxTicks = 0
    private var mainmenu = mutableListOf<Pair<Int, ResourceLocation>>()
    private var pictures = mutableListOf<Triple<String, String, ResourceLocation>>()
    private var donorCapeLocations = mutableListOf<Pair<String, ResourceLocation>>()

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

    fun callImage(image: String, location: String): ResourceLocation {
        for ((i, l, s) in pictures) {
            if (i == image && l == location)
                return s
        }
        return ResourceLocation("client/temp.png")
    }

    fun callMainMenu(image: Int): ResourceLocation {
        for ((i, l) in mainmenu) {
            if (i == image)
                return l
        }
        return ResourceLocation("client/temp.png")
    }

    fun loadPictures() {
        try {
            if (pictures.isNotEmpty())
                pictures.clear()
            var gotNames: String
            tlsAuthConnectionFixes()
            val nameClient = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
            val nameBuilder = Request.Builder().url(URLComponent.PICTURES + "locations.txt")
            val nameRequest: Request = nameBuilder.build()
            nameClient.newCall(nameRequest).execute().use { response ->
                gotNames = response.body!!.string()
            }
            val details = gotNames.split("---")
            for (i in details) {
                var gotImage: BufferedImage
                val fileName = i.split(":")[0]
                val picType = i.split(":")[1]
                tlsAuthConnectionFixes()
                gotImage = ImageIO.read(URL(URLComponent.PICTURES + picType + "/" + fileName + ".png"))
                pictures.add(
                    Triple(
                        fileName,
                        picType,
                        MinecraftInstance.mc.textureManager.getDynamicTextureLocation(
                            Launch.CLIENT_FOLDER,
                            DynamicTexture(gotImage)
                        )
                    )
                )
                ClientUtils.getLogger().info("Load Picture $fileName, $picType")
            }
            canConnect = true
            ClientUtils.getLogger().info("Loaded Pictures")
        } catch (e: Exception) {
            canConnect = false
            ClientUtils.getLogger().info("Failed to load Pictures")
        }
    }

    fun loadMainMenu() {
        try {
            if (mainmenu.isNotEmpty())
                mainmenu.clear()
            for ((counter, i) in (0..Int.MAX_VALUE).withIndex()) {
                tlsAuthConnectionFixes()
                val gotImage: BufferedImage =
                    ImageIO.read(URL(URLComponent.PICTURES + "background/mainmenu/" + counter + ".png"))
                mainmenu.add(
                    Pair(
                        i,
                        MinecraftInstance.mc.textureManager.getDynamicTextureLocation(
                            Launch.CLIENT_FOLDER,
                            DynamicTexture(gotImage)
                        )
                    )
                )
                ClientUtils.getLogger().info("Load MainMenu $counter")
                maxTicks = counter
            }
        } catch (_: Exception) {
        }
    }

    fun loadCape(player: EntityPlayer): ResourceLocation? {
        for ((i, l) in donorCapeLocations) {
            if (i == player.uniqueID.toString())
                return l
        }
        return null
    }

    fun loadDonors() {
        try {
            if (donorCapeLocations.isNotEmpty())
                donorCapeLocations.clear()
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
                val uuid = i.split(":")[0]
                val cape = i.split(":")[1]
                tlsAuthConnectionFixes()
                gotCapes = ImageIO.read(URL(URLComponent.DONORS + cape))
                donorCapeLocations.add(
                    Pair(
                        uuid,
                        MinecraftInstance.mc.textureManager.getDynamicTextureLocation(
                            Launch.CLIENT_FOLDER,
                            DynamicTexture(gotCapes)
                        )
                    )
                )
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
            isLatest = details[5] == Launch.CLIENT_VERSION
            discord = details[4]
            donate = details[3]
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