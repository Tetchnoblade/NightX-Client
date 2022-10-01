package net.ccbluex.liquidbounce.discord

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import org.json.JSONObject
import org.lwjgl.opengl.Display
import java.time.OffsetDateTime
import kotlin.concurrent.thread

class ClientRichPresence : MinecraftInstance() {

    var showRichPresenceValue = true

    // IPC Client
    private var ipcClient: IPCClient? = null

    private var appID = 0L
    private val assets = mutableMapOf<String, String>()
    private val timestamp = OffsetDateTime.now()

    // Status of running
    private var running: Boolean = false

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
        if (assets.containsKey("new"))
            builder.setLargeImage(assets["new"], "build ${LiquidBounce.CLIENT_VERSION}")

        val serverData = mc.currentServerData

        // Set display infos
        builder.setDetails(if (Display.isActive()) (if (mc.isIntegratedServerRunning || serverData != null) "youtube.com/As0452" else "youtube.com/As0452") else "youtube.com/As0452")
        builder.setState("Username: ${mc.session.username}")

        if (mc.isIntegratedServerRunning || serverData != null)
            builder.setSmallImage(
                assets["astolfo"],
                "${if (mc.isIntegratedServerRunning || serverData == null) "youtube.com/As0452" else serverData.serverIP} - Enabled ${LiquidBounce.moduleManager.modules.count { it.state }}/${LiquidBounce.moduleManager.modules.size}."
            )
        else
            builder.setSmallImage(
                assets["astolfo"],
                "Enabled ${LiquidBounce.moduleManager.modules.count { it.state }}/${LiquidBounce.moduleManager.modules.size}."
            )

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
        appID = 905789396010295316
        assets["new"] = "new"
        assets["astolfo"] = "astolfo"
    }
}
