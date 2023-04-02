package net.aspw.client.features.api

import net.aspw.client.features.module.Module
import net.aspw.client.value.BoolValue

class GuiFastRender : Module() {
    companion object {
        @JvmField
        val fixValue = BoolValue("Fix", false)
    }
}