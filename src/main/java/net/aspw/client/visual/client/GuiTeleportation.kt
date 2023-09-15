package net.aspw.client.visual.client

import net.aspw.client.Client
import net.aspw.client.features.module.impl.targets.AntiBots
import net.aspw.client.util.MinecraftInstance
import net.aspw.client.util.PacketUtils
import net.aspw.client.util.pathfinder.MainPathFinder
import net.aspw.client.util.pathfinder.Vec3
import net.aspw.client.util.render.RenderUtils
import net.aspw.client.visual.hud.element.elements.Notification
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.network.play.client.C03PacketPlayer
import org.lwjgl.input.Keyboard

class GuiTeleportation(private val prevGui: GuiScreen) : GuiScreen() {
    private lateinit var teleportXField: GuiTextField
    private lateinit var teleportYField: GuiTextField
    private lateinit var teleportZField: GuiTextField
    private lateinit var playerField: GuiTextField
    private lateinit var playerTeleportation: GuiButton
    private var playerTeleport = false

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        teleportXField = GuiTextField(2, mc.fontRendererObj, width / 2 - 100, 65, 200, 20)
        teleportYField = GuiTextField(3, mc.fontRendererObj, width / 2 - 100, 95, 200, 20)
        teleportZField = GuiTextField(4, mc.fontRendererObj, width / 2 - 100, 125, 200, 20)
        playerField = GuiTextField(6, mc.fontRendererObj, width / 2 - 100, 125, 200, 20)
        teleportXField.maxStringLength = Int.MAX_VALUE
        teleportYField.maxStringLength = Int.MAX_VALUE
        teleportZField.maxStringLength = Int.MAX_VALUE
        playerField.maxStringLength = 16
        buttonList.add(GuiButton(5, width / 2 - 100, 160, "").also { playerTeleportation = it })
        buttonList.add(GuiButton(0, width / 2 - 100, 185, "Click to Teleport"))
        buttonList.add(GuiButton(11, width / 2 - 100, 210, "Set Survival"))
        buttonList.add(GuiButton(12, width / 2 - 100, 235, "Set Creative"))
        buttonList.add(GuiButton(13, width / 2 - 100, 260, "Set Adventure"))
        buttonList.add(GuiButton(14, width / 2 - 100, 285, "Set Spectator"))
        updateButtonStat()
    }

    private fun updateButtonStat() {
        playerTeleportation.displayString = "Teleport Mode: §a" + if (playerTeleport) "§aPlayer" else "§aCoordinates"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        RenderUtils.drawGradientRect(0, 0, width, height, -1072689136, -804253680)
        if (!playerTeleport) {
            teleportXField.drawTextBox()
            teleportYField.drawTextBox()
            teleportZField.drawTextBox()
        } else playerField.drawTextBox()
        if (!playerTeleport) {
            if (teleportXField.text.isEmpty() && !teleportXField.isFocused)
                drawString(mc.fontRendererObj, "§7X", width / 2 - 96, 65 + 6, 0xffffff)
            if (teleportYField.text.isEmpty() && !teleportYField.isFocused)
                drawString(mc.fontRendererObj, "§7Y", width / 2 - 96, 95 + 6, 0xffffff)
            if (teleportZField.text.isEmpty() && !teleportZField.isFocused)
                drawString(mc.fontRendererObj, "§7Z", width / 2 - 96, 125 + 6, 0xffffff)
        } else {
            if (playerField.text.isEmpty() && !playerField.isFocused)
                drawString(mc.fontRendererObj, "§7Player ID", width / 2 - 96, 125 + 6, 0xffffff)
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            11 -> MinecraftInstance.mc.thePlayer.sendChatMessage("/gamemode survival @p")
            12 -> MinecraftInstance.mc.thePlayer.sendChatMessage("/gamemode creative @p")
            13 -> MinecraftInstance.mc.thePlayer.sendChatMessage("/gamemode adventure @p")
            14 -> MinecraftInstance.mc.thePlayer.sendChatMessage("/gamemode spectator @p")

            0 -> {
                if (!playerTeleport) {
                    if (teleportXField.text.isNotEmpty() && teleportYField.text.isNotEmpty() && teleportZField.text.isNotEmpty()) {
                        Thread {
                            val path: ArrayList<Vec3> = MainPathFinder.computePath(
                                Vec3(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ
                                ),
                                Vec3(
                                    teleportXField.text.toDouble(),
                                    teleportYField.text.toDouble(),
                                    teleportZField.text.toDouble()
                                )
                            )
                            for (point in path) PacketUtils.sendPacketNoEvent(
                                C03PacketPlayer.C04PacketPlayerPosition(
                                    point.x,
                                    point.y,
                                    point.z,
                                    true
                                )
                            )
                            mc.thePlayer.setPosition(
                                teleportXField.text.toDouble(),
                                teleportYField.text.toDouble(),
                                teleportZField.text.toDouble()
                            )
                        }.start()
                        Client.hud.addNotification(
                            Notification(
                                "Successfully teleported to §a${teleportXField.text.toInt()}, ${teleportYField.text.toInt()}, ${teleportZField.text.toInt()}",
                                Notification.Type.SUCCESS
                            )
                        )
                        return
                    }
                } else {
                    val targetPlayer = mc.theWorld.playerEntities
                        .filter { !AntiBots.isBot(it) && it.name.equals(playerField.text, true) }
                        .firstOrNull()

                    if (targetPlayer != null) {
                        Thread {
                            val path: ArrayList<Vec3> = MainPathFinder.computePath(
                                Vec3(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ
                                ),
                                Vec3(
                                    targetPlayer.posX,
                                    targetPlayer.posY,
                                    targetPlayer.posZ
                                )
                            )
                            for (point in path) PacketUtils.sendPacketNoEvent(
                                C03PacketPlayer.C04PacketPlayerPosition(
                                    point.x,
                                    point.y,
                                    point.z,
                                    true
                                )
                            )
                            mc.thePlayer.setPosition(
                                targetPlayer.posX,
                                targetPlayer.posY,
                                targetPlayer.posZ
                            )
                        }.start()
                        Client.hud.addNotification(
                            Notification(
                                "Successfully teleported to §a${targetPlayer.name}",
                                Notification.Type.SUCCESS
                            )
                        )
                        return
                    } else {
                        Client.hud.addNotification(
                            Notification(
                                "No players found!",
                                Notification.Type.ERROR
                            )
                        )
                    }
                }
            }

            5 -> {
                playerTeleport = !playerTeleport
            }
        }
        updateButtonStat()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (!playerTeleport) {
            teleportXField.textboxKeyTyped(typedChar, keyCode)
            teleportYField.textboxKeyTyped(typedChar, keyCode)
            teleportZField.textboxKeyTyped(typedChar, keyCode)
        } else playerField.textboxKeyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (!playerTeleport) {
            teleportXField.mouseClicked(mouseX, mouseY, mouseButton)
            teleportYField.mouseClicked(mouseX, mouseY, mouseButton)
            teleportZField.mouseClicked(mouseX, mouseY, mouseButton)
        } else playerField.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        if (!playerTeleport) {
            teleportXField.updateCursorCounter()
            teleportYField.updateCursorCounter()
            teleportZField.updateCursorCounter()
        } else playerField.updateCursorCounter()
        super.updateScreen()
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }
}