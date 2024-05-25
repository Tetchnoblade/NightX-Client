package net.aspw.client.visual.client.altmanager

import net.aspw.client.Launch
import net.aspw.client.Launch.fileManager
import net.aspw.client.auth.account.CrackedAccount
import net.aspw.client.auth.account.MicrosoftAccount
import net.aspw.client.auth.account.MinecraftAccount
import net.aspw.client.event.SessionEvent
import net.aspw.client.features.module.impl.visual.Interface
import net.aspw.client.utils.APIConnecter
import net.aspw.client.utils.MinecraftInstance
import net.aspw.client.utils.login.UserUtils.isValidTokenOffline
import net.aspw.client.utils.misc.RandomUtils
import net.aspw.client.utils.render.RenderUtils
import net.aspw.client.visual.client.altmanager.menus.GuiAddAccount
import net.aspw.client.visual.font.semi.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.Session
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.util.*
import kotlin.concurrent.thread


class GuiAltManager(private val prevGui: GuiScreen) : GuiScreen() {

    var status = "§7Waiting..."

    private lateinit var loginButton: GuiButton
    private lateinit var randomButton: GuiButton
    private lateinit var randomCracked: GuiButton
    private lateinit var altsList: GuiList
    private lateinit var searchField: GuiTextField

    private var lastSessionToken: String? = null

    override fun initGui() {
        val textFieldWidth = (width / 8).coerceAtLeast(70)
        searchField = GuiTextField(2, mc.fontRendererObj, width - textFieldWidth - 10, 10, textFieldWidth, 20)
        searchField.maxStringLength = Int.MAX_VALUE

        altsList = GuiList(this)
        altsList.registerScrollButtons(7, 8)

        val mightBeTheCurrentAccount =
            fileManager.accountsConfig.accounts.indexOfFirst { it.name == mc.session.username }
        altsList.elementClicked(mightBeTheCurrentAccount, false, 0, 0)
        altsList.scrollBy(mightBeTheCurrentAccount * altsList.getSlotHeight())

        val startPositionY = 22
        buttonList.add(GuiButton(1, width - 80, startPositionY + 24, 70, 20, "Add"))
        buttonList.add(GuiButton(6, width - 80, startPositionY + 24 * 2, 70, 20, "Direct"))
        buttonList.add(GuiButton(2, width - 80, startPositionY + 24 * 3, 70, 20, "Delete"))
        buttonList.add(GuiButton(9, width - 80, startPositionY + 24 * 4, 70, 20, "Reload"))
        buttonList.add(GuiButton(0, width - 80, height - 65, 70, 20, "Done"))
        buttonList.add(GuiButton(3, 5, startPositionY + 24, 90, 20, "Login").also { loginButton = it })
        buttonList.add(GuiButton(4, 5, startPositionY + 24 * 2, 90, 20, "Random Alt").also { randomButton = it })
        buttonList.add(GuiButton(99, 5, startPositionY + 24 * 3, 90, 20, "Random Cracked").also { randomCracked = it })
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        RenderUtils.drawImage(
            APIConnecter.callImage("portal", "background"), 0, 0,
            width, height
        )
        altsList.drawScreen(mouseX, mouseY, partialTicks)
        this.drawCenteredString(mc.fontRendererObj, "Alt Manager", width / 2, 12, 0xffffff)
        this.drawCenteredString(mc.fontRendererObj, "§7Status: §a$status", width / 2, 25, 0xffffff)
        this.drawString(
            mc.fontRendererObj,
            if (searchField.text.isEmpty()) "§7Alts: §a${fileManager.accountsConfig.accounts.size}" else "§7Search Results: §a" + altsList.accounts.size.toString(),
            6,
            26,
            0xffffff
        )
        this.drawString(
            mc.fontRendererObj, "§7Ign: §a${mc.getSession().username}",
            6,
            6,
            0xffffff
        )
        this.drawString(
            mc.fontRendererObj, "§7Type: §a${
                if (isValidTokenOffline(
                        mc.getSession().token
                    )
                ) "Microsoft" else "Cracked"
            }", 6, 16, 0xffffff
        )
        searchField.drawTextBox()
        if (searchField.text.isEmpty() && !searchField.isFocused)
            this.drawString(
                mc.fontRendererObj, "§7Search...",
                (searchField.xPosition + 3),
                16,
                0xffffff
            )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    public override fun actionPerformed(button: GuiButton) {
        if (!button.enabled)
            return

        when (button.id) {
            0 -> mc.displayGuiScreen(prevGui)
            1 -> mc.displayGuiScreen(GuiAddAccount(this, false))
            2 -> {
                status = if (altsList.selectedSlot != -1 && altsList.selectedSlot < altsList.size) {
                    fileManager.accountsConfig.removeAccount(altsList.accounts[altsList.selectedSlot])
                    fileManager.saveConfig(fileManager.accountsConfig)
                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    "§aThe account has been deleted."
                } else {
                    "§cSelect an account."
                }
            }

            3 -> {
                if (lastSessionToken == null)
                    lastSessionToken = mc.session.token

                status = altsList.selectedAccount?.let {
                    loginButton.enabled = false
                    randomButton.enabled = false
                    randomCracked.enabled = false

                    login(it, {
                        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                        }
                        status = "§aLogged successfully to ${mc.session.username}."
                    }, { exception ->
                        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                        }
                        status = "§cLogin failed to '${exception.message}'."
                    }, {
                        loginButton.enabled = true
                        randomButton.enabled = true
                        randomCracked.enabled = true
                    })

                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    "§aLogging in..."
                } ?: "§cSelect an account."
            }

            4 -> {
                if (lastSessionToken == null)
                    lastSessionToken = mc.session.token

                status = altsList.accounts.randomOrNull()?.let {
                    loginButton.enabled = false
                    randomButton.enabled = false
                    randomCracked.enabled = false

                    login(it, {
                        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                        }
                        status = "§aLogged successfully to ${mc.session.username}."
                    }, { exception ->
                        status = "§cLogin failed to '${exception.message}'."
                    }, {
                        loginButton.enabled = true
                        randomButton.enabled = true
                        randomCracked.enabled = true
                    })

                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    "§aLogging in..."
                } ?: "§cYou do not have any accounts."
            }

            6 -> mc.displayGuiScreen(GuiAddAccount(this, true))

            9 -> {
                fileManager.loadConfig(fileManager.accountsConfig)
                if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                    Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                }
            }

            99 -> {
                if (lastSessionToken == null)
                    lastSessionToken = mc.session.token

                loginButton.enabled = false
                randomButton.enabled = false
                randomCracked.enabled = false

                val rand = CrackedAccount()
                rand.name = RandomUtils.randomString(RandomUtils.nextInt(5, 16))

                status = "§aGenerating..."

                login(rand, {
                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    status = "§aLogged successfully to ${mc.session.username}."
                }, { exception ->
                    status = "§cLogin failed to '${exception.message}'."
                }, {
                    loginButton.enabled = true
                    randomButton.enabled = true
                    randomCracked.enabled = true
                })
            }
        }
    }

    public override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (searchField.isFocused) {
            searchField.textboxKeyTyped(typedChar, keyCode)
        }

        when (keyCode) {
            Keyboard.KEY_ESCAPE -> { // Go back
                mc.displayGuiScreen(prevGui)
                return
            }

            Keyboard.KEY_UP -> { // Go one up in account list
                var i = altsList.selectedSlot - 1
                if (i < 0) i = 0
                altsList.elementClicked(i, false, 0, 0)
            }

            Keyboard.KEY_DOWN -> { // Go one down in account list
                var i = altsList.selectedSlot + 1
                if (i >= altsList.size) i = altsList.size - 1
                altsList.elementClicked(i, false, 0, 0)
            }

            Keyboard.KEY_RETURN -> { // Login into account
                altsList.elementClicked(altsList.selectedSlot, true, 0, 0)
            }

            Keyboard.KEY_NEXT -> { // Scroll account list
                altsList.scrollBy(height - 100)
            }

            Keyboard.KEY_PRIOR -> { // Scroll account list
                altsList.scrollBy(-height + 100)
                return
            }
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun onGuiClosed() {
        fileManager.saveConfig(fileManager.accountsConfig)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        altsList.handleMouseInput()
    }

    public override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        searchField.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun updateScreen() {
        searchField.updateCursorCounter()
    }

    private inner class GuiList constructor(prevGui: GuiScreen) :
        GuiSlot(mc, prevGui.width, prevGui.height, 40, prevGui.height - 40, 30) {

        val accounts: List<MinecraftAccount>
            get() {
                var search = searchField.text
                if (search == null || search.isEmpty()) {
                    return fileManager.accountsConfig.accounts
                }
                search = search.lowercase(Locale.getDefault())

                return fileManager.accountsConfig.accounts.filter {
                    it.name.contains(
                        search,
                        ignoreCase = true
                    )
                }
            }

        var selectedSlot = 0
            get() {
                return if (field > accounts.size) {
                    -1
                } else {
                    field
                }
            }

        val selectedAccount: MinecraftAccount?
            get() = if (selectedSlot >= 0 && selectedSlot < accounts.size) {
                accounts[selectedSlot]
            } else {
                null
            }

        override fun isSelected(id: Int) = selectedSlot == id

        public override fun getSize() = accounts.size

        public override fun elementClicked(clickedElement: Int, doubleClick: Boolean, var3: Int, var4: Int) {
            selectedSlot = clickedElement

            if (doubleClick) {
                status = altsList.selectedAccount?.let {
                    loginButton.enabled = false
                    randomButton.enabled = false
                    randomCracked.enabled = false

                    login(it, {
                        if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                            Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                        }
                        status = "§aLogged successfully to ${mc.session.username}."
                    }, { exception ->
                        status = "§cLogin failed to '${exception.message}'."
                    }, {
                        loginButton.enabled = true
                        randomButton.enabled = true
                        randomCracked.enabled = true
                    })

                    if (Launch.moduleManager.getModule(Interface::class.java)?.flagSoundValue!!.get()) {
                        Launch.tipSoundManager.popSound.asyncPlay(Launch.moduleManager.popSoundPower)
                    }
                    "§aLogging in..."
                } ?: "§cSelect an account."
            }
        }

        override fun drawSlot(id: Int, x: Int, y: Int, var4: Int, var5: Int, var6: Int) {
            val minecraftAccount = accounts[id]
            val accountName = minecraftAccount.name
            Fonts.minecraftFont.drawStringWithShadow(accountName, width / 2f - 40, y + 2f, Color.WHITE.rgb)
            Fonts.minecraftFont.drawStringWithShadow(
                if (minecraftAccount is CrackedAccount) "Cracked" else if (minecraftAccount is MicrosoftAccount) "Microsoft" else "Null",
                width / 2f - 40,
                y + 15f,
                if (minecraftAccount is CrackedAccount) Color.GRAY.rgb else Color(118, 255, 95).rgb
            )
        }

        override fun drawBackground() {}
    }

    companion object {

        fun login(
            minecraftAccount: MinecraftAccount,
            success: () -> Unit,
            error: (Exception) -> Unit,
            done: () -> Unit
        ) = thread(name = "LoginTask") {
            try {
                minecraftAccount.update()
                MinecraftInstance.mc.session = Session(
                    minecraftAccount.session.username,
                    minecraftAccount.session.uuid, minecraftAccount.session.token, "mojang"
                )
                Launch.eventManager.callEvent(SessionEvent())

                success()
            } catch (exception: Exception) {
                error(exception)
            }
            done()
        }
    }
}