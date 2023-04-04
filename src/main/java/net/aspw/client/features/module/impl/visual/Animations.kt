package net.aspw.client.features.module.impl.visual

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue

@ModuleInfo(
    name = "Animations",
    category = ModuleCategory.VISUAL,
    forceNoSound = true,
    onlyEnable = true,
    array = false
)
class Animations : Module() {
    companion object {
        @JvmField
        val Sword = ListValue(
            "Style", arrayOf(
                "1.8",
                "Hide",
                "Old",
                "SlideLow",
                "SlideMedium",
                "SlideFull",
                "Push",
                "Dash",
                "Swing",
                "Swank",
                "Swang",
                "Swonk",
                "Float",
                "Stella",
                "Sloth",
                "Edit",
                "Reverse",
                "Autumn",
                "Astolfo",
                "Dortware",
                "DortwarePush",
                "VisionFX",
                "ETB",
                "Moon",
                "MoonPush",
                "Lennox",
                "Smooth",
                "Jello",
                "Shield",
                "Ninja",
                "Jigsaw",
                "Avatar",
                "Stab",
                "Sigma3",
                "Sigma4",
                "Double1",
                "Double2",
                "Zoom",
                "Rotate",
                "Spin",
                "Spinny"
            ), "Swing"
        )

        @JvmField
        val swingAnimValue = ListValue(
            "Swing-Animation", arrayOf(
                "Vanilla",
                "Flux"
            ), "Vanilla"
        )

        @JvmField
        val itemPosX = FloatValue("ItemPosX", 0f, -1f, 1f)

        @JvmField
        val itemPosY = FloatValue("ItemPosY", 0f, -1f, 1f)

        @JvmField
        val itemPosZ = FloatValue("ItemPosZ", 0f, -1f, 1f)

        @JvmField
        val itemFovX = FloatValue("ItemFovX", 0f, -10f, 10f)

        @JvmField
        val itemFovY = FloatValue("ItemFovY", 0f, -10f, 10f)

        @JvmField
        val itemFovZ = FloatValue("ItemFovZ", 0f, -10f, 10f)

        @JvmField
        val blockPosX = FloatValue("BlockPosX", 0f, -1f, 1f)

        @JvmField
        val blockPosY = FloatValue("BlockPosY", 0f, -1f, 1f)

        @JvmField
        val blockPosZ = FloatValue("BlockPosZ", 0f, -1f, 1f)

        @JvmField
        val Equip = FloatValue("Equip-Modify", 2f, -5f, 5f)

        @JvmField
        val SpeedSwing = IntegerValue("Swing-Speed", 0, -9, 5)

        @JvmField
        val SpeedRotate = FloatValue("Spin-Speed", 10f, 0f, 10f) {
            Sword.get().equals("spinny", ignoreCase = true) || Sword.get()
                .equals("rotate", ignoreCase = true) || Sword.get()
                .equals("spin", ignoreCase = true)
        }

        @JvmField
        val tabAnimations = ListValue("Tab-Animation", arrayOf("None", "Zoom", "Slide"), "None")

        @JvmField
        val oldAnimations = BoolValue("1.7-Animations", false)
    }
}