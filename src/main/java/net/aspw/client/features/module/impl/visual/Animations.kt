package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue

@ModuleInfo(
    name = "Animations", description = "",
    category = ModuleCategory.VISUAL,
    forceNoSound = true,
    onlyEnable = true,
    array = false
)
class Animations : Module() {
    companion object {
        @JvmField
        val Sword = ListValue(
            "Mode", arrayOf(
                "1.8",
                "Swing",
                "Old",
                "Push",
                "Dash",
                "Slash",
                "Slide",
                "Swank",
                "Swang",
                "Swonk",
                "Stella",
                "Small",
                "Edit",
                "Rhys",
                "Stab",
                "Float",
                "Remix",
                "Winter",
                "Xiv",
                "Reverse",
                "Leaked",
                "Aqua",
                "Invent",
                "Fadeaway",
                "Astolfo",
                "Moon",
                "MoonPush",
                "Smooth",
                "Jigsaw",
                "Sigma3",
                "Sigma4",
                "Spin"
            ), "Swing"
        )

        @JvmField
        val swingAnimValue = ListValue(
            "Swing-Animation", arrayOf(
                "1.7",
                "1.8",
                "Flux",
                "Smooth"
            ), "1.7"
        )

        @JvmField
        val tabAnimations = ListValue("Tab-Animation", arrayOf("None", "Zoom", "Slide"), "None")

        @JvmField
        val oldAnimations = BoolValue("OldAnimations", true)

        @JvmField
        val cancelEquip = BoolValue("CancelEquip", false)

        @JvmField
        val blockingOnly = BoolValue("BlockingOnly", true) { cancelEquip.get() }

        @JvmField
        val scale = FloatValue("Scale", 0f, -0.5f, 0.5f)

        @JvmField
        val itemFov = FloatValue("Fov", 0f, -5f, 5f)

        @JvmField
        val itemPosX = FloatValue("ItemPos-X", 0f, -1f, 1f)

        @JvmField
        val itemPosY = FloatValue("ItemPos-Y", 0f, -1f, 1f)

        @JvmField
        val itemPosZ = FloatValue("ItemPos-Z", 0f, -1f, 1f)

        @JvmField
        val blockPosX = FloatValue("BlockPos-X", 0f, -1f, 1f)

        @JvmField
        val blockPosY = FloatValue("BlockPos-Y", 0f, -1f, 1f)

        @JvmField
        val blockPosZ = FloatValue("BlockPos-Z", 0f, -1f, 1f)

        @JvmField
        val SpeedSwing = IntegerValue("Swing-Speed", 0, -15, 4)
    }
}