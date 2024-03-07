package net.aspw.client.features.module.impl.combat

import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo
import net.aspw.client.value.FloatValue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@ModuleInfo(name = "HitBox", spacedName = "Hit Box", category = ModuleCategory.COMBAT)
class HitBox : Module() {
    val sizeValue = FloatValue("Size", 1F, 0F, 1F)

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))

    override val tag: String
        get() = decimalFormat.format(sizeValue.get())
}