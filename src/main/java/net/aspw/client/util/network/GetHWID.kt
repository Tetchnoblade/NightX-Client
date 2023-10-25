package net.aspw.client.util.network

import net.aspw.client.util.ClientUtils
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

// Old Auth System
fun getCurrentHWID(): String {
    val process = ProcessBuilder("wmic", "csproduct", "get", "uuid").start()
    process.waitFor()
    val scanner = Scanner(process.inputStream, "UTF-8").useDelimiter("\\A")
    val uuid = if (scanner.hasNext()) scanner.next() else ""
    val pos1 = uuid.indexOf("\n") + 1
    LoginID.currentHWID = uuid.substring(pos1, uuid.length - 15)
    return uuid.substring(pos1, uuid.length - 15)
}

fun getHWID(): String {
    val process = ProcessBuilder("wmic", "csproduct", "get", "uuid").start()
    process.waitFor()

    val scanner = Scanner(process.inputStream, "UTF-8").useDelimiter("\\A")
    val uuid = if (scanner.hasNext()) scanner.next() else ""

    val pos1 = uuid.indexOf("\n") + 1

    ClientUtils.getLogger().info("Your HWID is:")
    ClientUtils.getLogger().info(uuid.substring(pos1, uuid.length - 15))
    ClientUtils.getLogger().info("Copied to your clipboard!")

    val stringSelection = StringSelection(uuid.substring(pos1, uuid.length - 15))
    Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)

    return uuid.substring(pos1, uuid.length - 15)
}