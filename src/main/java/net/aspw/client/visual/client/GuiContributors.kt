package net.aspw.client.visual.client

import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import net.aspw.client.Client
import net.aspw.client.config.FileManager
import net.aspw.client.injection.implementations.IMixinGuiSlot
import net.aspw.client.util.misc.HttpUtils
import net.aspw.client.util.newfont.FontLoaders
import net.aspw.client.util.render.CustomTexture
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot
import net.minecraft.client.renderer.GlStateManager.*
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.math.sin

class GuiContributors(private val prevGui: GuiScreen) : GuiScreen() {
    private val DECIMAL_FORMAT = NumberFormat.getInstance(Locale.US) as DecimalFormat
    private lateinit var list: GuiList

    private var credits = emptyList<Credit>()
    private var failed = false

    override fun initGui() {
        list = GuiList(this)
        list.registerScrollButtons(7, 8)

        buttonList.add(GuiButton(1, width / 2 - 100, height - 30, "Done"))

        failed = false

        thread { loadCredits() }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        list.drawScreen(mouseX, mouseY, partialTicks)

        drawRect(width / 4, 40, width, height - 40, Integer.MIN_VALUE)

        if (credits.isNotEmpty()) {
            val credit = credits[list.selectedSlot]

            var y = 45
            val x = width / 4 + 5
            var infoOffset = 0

            val avatar = credit.avatar

            val imageSize = fontRendererObj.FONT_HEIGHT * 4

            if (avatar != null) {
                glPushAttrib(GL_ALL_ATTRIB_BITS)

                enableAlpha()
                enableBlend()
                tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
                enableTexture2D()

                glColor4f(1f, 1f, 1f, 1f)

                bindTexture(avatar.textureId)


                glBegin(GL_QUADS)

                glTexCoord2f(0f, 0f)
                glVertex2i(x, y)
                glTexCoord2f(0f, 1f)
                glVertex2i(x, y + imageSize)
                glTexCoord2f(1f, 1f)
                glVertex2i(x + imageSize, y + imageSize)
                glTexCoord2f(1f, 0f)
                glVertex2i(x + imageSize, y)

                glEnd()

                bindTexture(0)

                disableBlend()

                infoOffset = imageSize

                glPopAttrib()
            }

            y += imageSize

            FontLoaders.SF21.drawString(
                "@" + credit.name,
                x + infoOffset + 5f.toDouble(),
                48f.toDouble(),
                Color.WHITE.rgb,
                false
            )
            FontLoaders.SF21.drawString(
                "${credit.commits} commits §a${DECIMAL_FORMAT.format(credit.additions)}++ §4${
                    DECIMAL_FORMAT.format(
                        credit.deletions
                    )
                }--",
                x + infoOffset + 5f.toDouble(),
                (y - FontLoaders.SF21.height).toFloat().toDouble(),
                Color.WHITE.rgb,
                false
            )

            for (s in credit.contributions) {
                y += FontLoaders.SF21.height + 2

                disableTexture2D()
                glColor4f(1f, 1f, 1f, 1f)
                glBegin(GL_LINES)

                glVertex2f(x.toFloat(), y + FontLoaders.SF21.height / 2f - 1)
                glVertex2f(x + 3f, y + FontLoaders.SF21.height / 2f - 1)

                glEnd()

                FontLoaders.SF21.drawString(s, (x + 5f.toDouble()), y.toFloat().toDouble(), Color.WHITE.rgb, false)
            }
        }

        FontLoaders.SF21.drawCenteredString("Contributors", width / 2F, 6F, 0xffffff)

        if (credits.isEmpty()) {
            if (failed) {
                val gb = ((sin(System.currentTimeMillis() * (1 / 333.0)) + 1) * (0.5 * 255)).toInt()
                FontLoaders.SF21.drawCenteredString("Failed to load", width / 8f, height / 2f, Color(255, gb, gb).rgb)
            } else {
                FontLoaders.SF21.drawCenteredString("Connecting Database...", width / 8f, height / 2f, Color.WHITE.rgb)
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 1) {
            mc.displayGuiScreen(prevGui)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> mc.displayGuiScreen(prevGui)
            Keyboard.KEY_UP -> list.selectedSlot -= 1
            Keyboard.KEY_DOWN -> list.selectedSlot += 1
            Keyboard.KEY_TAB ->
                list.selectedSlot += if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) -1 else 1

            else -> super.keyTyped(typedChar, keyCode)
        }
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        list.handleMouseInput()
    }

    private fun loadCredits() {
        val jsonParser = JsonParser()

        val gitHubContributors = FileManager.PRETTY_GSON.fromJson(
            HttpUtils.get(Client.CLIENT_INFORMATION),
            Array<GitHubContributor>::class.java
        )
        val additionalInformation = jsonParser.parse(HttpUtils.get(Client.CLIENT_CONTRIBUTORS)).asJsonObject

        val credits = mutableListOf<Credit>()

        for (gitHubContributor in gitHubContributors) {
            var contributorInformation: ContributorInformation? = null
            val author = gitHubContributor.author ?: continue // Skip invalid contributors
            val jsonElement = additionalInformation[author.id.toString()]

            if (jsonElement != null) {
                contributorInformation =
                    FileManager.PRETTY_GSON.fromJson(jsonElement, ContributorInformation::class.java)
            }

            var additions = 0
            var deletions = 0
            var commits = 0

            for (week in gitHubContributor.weeks) {
                additions += week.additions
                deletions += week.deletions
                commits += week.commits
            }

            credits += Credit(
                author.name, author.avatarUrl, null,
                additions, deletions, commits,
                contributorInformation?.teamMember ?: false,
                contributorInformation?.contributions ?: emptyList()
            )
        }

        credits.sortWith { o1, o2 ->
            when {
                o1.isTeamMember && o2.isTeamMember -> -o1.commits.compareTo(o2.commits)

                o1.isTeamMember -> -1

                o2.isTeamMember -> 1

                else -> -o1.additions.compareTo(o2.additions)
            }
        }

        this.credits = credits

        for (credit in credits) {
            try {
                HttpUtils.requestStream("${credit.avatarUrl}?s=${fontRendererObj.FONT_HEIGHT * 4}", "GET").use {
                    credit.avatar = CustomTexture(ImageIO.read(it))
                }
            } catch (_: Exception) {

            }
        }
    }

    internal inner class ContributorInformation(
        val name: String,
        val teamMember: Boolean,
        val contributions: List<String>
    )

    internal inner class GitHubContributor(
        @SerializedName("total") val totalContributions: Int,
        val weeks: List<GitHubWeek>,
        val author: GitHubAuthor?
    )

    internal inner class GitHubWeek(
        @SerializedName("w") val timestamp: Long,
        @SerializedName("a") val additions: Int,
        @SerializedName("d") val deletions: Int,
        @SerializedName("c") val commits: Int
    )

    internal inner class GitHubAuthor(
        @SerializedName("login") val name: String,
        val id: Int,
        @SerializedName("avatar_url") val avatarUrl: String
    )

    internal inner class Credit(
        val name: String,
        val avatarUrl: String,
        var avatar: CustomTexture?,
        val additions: Int,
        val deletions: Int,
        val commits: Int,
        val isTeamMember: Boolean,
        val contributions: List<String>
    )

    private inner class GuiList(gui: GuiScreen) : GuiSlot(mc, gui.width / 4, gui.height, 40, gui.height - 40, 15) {

        init {
            val mixin = this as IMixinGuiSlot

            mixin.setListWidth(gui.width * 3 / 13)
            mixin.setEnableScissor(true)
        }

        var selectedSlot = 0
            set(value) {
                field = (value + credits.size) % credits.size
            }

        override fun isSelected(id: Int) = selectedSlot == id

        override fun getSize() = credits.size

        public override fun elementClicked(index: Int, doubleClick: Boolean, var3: Int, var4: Int) {
            selectedSlot = index
        }

        override fun drawSlot(
            entryID: Int,
            p_180791_2_: Int,
            p_180791_3_: Int,
            p_180791_4_: Int,
            mouseXIn: Int,
            mouseYIn: Int
        ) {
            val credit = credits[entryID]

            FontLoaders.SF21.drawCenteredString(credit.name, width / 2F, p_180791_3_ + 2F, Color.WHITE.rgb)
        }

        override fun drawBackground() {}
    }
}