package net.aspw.client.features.module.modules.visual

import net.aspw.client.event.EventState
import net.aspw.client.event.EventTarget
import net.aspw.client.event.MotionEvent
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.BoolValue
import net.aspw.client.value.FloatValue
import net.aspw.client.value.IntegerValue
import net.aspw.client.value.ListValue

@ModuleInfo(name = "Animations", category = ModuleCategory.VISUAL, array = false)
class Animations : Module() {
    override fun onInitialize() {
        state = true
    }

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
                "Stella",
                "Sloth",
                "Edit",
                "Reverse",
                "Autumn",
                "Astolfo",
                "Fruit",
                "VisionFX",
                "ETB",
                "Moon",
                "MoonPush",
                "Lennox",
                "Smooth",
                "Leaked",
                "Jello",
                "Shield",
                "Ninja",
                "Jigsaw",
                "Avatar",
                "Stab",
                "Sigma3",
                "Sigma4",
                "Flux1",
                "Flux2",
                "Flux3",
                "Dortware1",
                "Dortware2",
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
        val Scale = FloatValue("Scale", 0.4f, 0f, 4f)

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
        val SpeedSwing = IntegerValue("Swing-Speed", 0, -9, 5)

        @JvmField
        val Equip = FloatValue("Equip-Motion", 1.8f, -5f, 5f)

        @JvmField
        val handPos = IntegerValue("Hand-Pos", 0, -500, 500)
        val onlySwingValue = BoolValue("Only-Swing", false)

        @JvmField
        val SpeedRotate = FloatValue("Spin-Speed", 10f, 0f, 10f) {
            Sword.get().equals("spinny", ignoreCase = true) || Sword.get()
                .equals("rotate", ignoreCase = true) || Sword.get()
                .equals("spin", ignoreCase = true)
        }

        @JvmField
        val guiAnimations = ListValue("Container-Animation", arrayOf("None", "Zoom", "Slide"), "None")

        @JvmField
        val tabAnimations = ListValue("Tab-Animation", arrayOf("None", "Zoom", "Slide"), "None")

        @JvmField
        val oldAnimations = BoolValue("1.7-Animations", false)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState === EventState.PRE && onlySwingValue.get() && mc.thePlayer.isSwingInProgress) {
            mc.thePlayer.renderArmPitch = handPos.get() + mc.thePlayer.rotationPitch
        }

        if (event.eventState === EventState.PRE && !onlySwingValue.get()) {
            mc.thePlayer.renderArmPitch = handPos.get() + mc.thePlayer.rotationPitch
        }
    }
}