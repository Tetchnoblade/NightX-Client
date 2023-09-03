package net.aspw.client.features.api

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import net.aspw.client.Client
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.network.CheckConnection
import org.json.JSONObject
import java.time.OffsetDateTime
import kotlin.concurrent.thread

class DiscordRPC : MinecraftInstance() {

    var showRichPresenceValue = true

    // IPC Client
    private var ipcClient: IPCClient? = null

    private var appID = 0L
    private val assets = mutableMapOf<String, String>()
    private val timestamp = OffsetDateTime.now()

    // Status of running
    var running: Boolean = false

    /**
     * Setup Discord RPC
     */
    fun setup() {
        try {
            running = true

            loadConfiguration()

            ipcClient = IPCClient(appID)
            ipcClient?.setListener(object : IPCListener {

                /**
                 * Fired whenever an [IPCClient] is ready and connected to Discord.
                 *
                 * @param client The now ready IPCClient.
                 */
                override fun onReady(client: IPCClient?) {
                    thread {
                        while (running) {
                            update()

                            try {
                                Thread.sleep(1000L)
                            } catch (ignored: InterruptedException) {
                            }
                        }
                    }
                }

                /**
                 * Fired whenever an [IPCClient] has closed.
                 *
                 * @param client The now closed IPCClient.
                 * @param json A [JSONObject] with close data.
                 */
                override fun onClose(client: IPCClient?, json: JSONObject?) {
                    running = false
                }

            })
            ipcClient?.connect()
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to setup Discord RPC.", e)
        }

    }

    /**
     * Update rich presence
     */
    fun update() {
        val builder = RichPresence.Builder()

        // Set playing time
        builder.setStartTimestamp(timestamp)

        // Check assets contains logo and set logo
        if (assets.containsKey("logo"))
            builder.setLargeImage(assets["logo"])

        // Set display infos
        builder.setDetails("Build: ${Client.CLIENT_VERSION}")
        builder.setState(CheckConnection.discord)
        builder.setSmallImage(assets["mahiro"], "https://youtube.com/@as_pw")

        // Check ipc client is connected and send rpc
        if (ipcClient?.status == PipeStatus.CONNECTED)
            ipcClient?.sendRichPresence(builder.build())
    }

    /**
     * Shutdown ipc client
     */
    fun shutdown() {
        if (ipcClient?.status != PipeStatus.CONNECTED) {
            return
        }

        try {
            ipcClient?.close()
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to close Discord RPC.", e)
        }
    }

    private fun loadConfiguration() {
        appID = 1130780572604702821L
        assets["logo"] = "logo"
        assets["mahiro"] = "mahiro"
    }
}