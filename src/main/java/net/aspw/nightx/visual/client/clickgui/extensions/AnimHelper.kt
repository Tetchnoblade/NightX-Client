package net.aspw.nightx.visual.client.clickgui.extensions

import net.aspw.nightx.features.module.modules.client.Fix
import net.aspw.nightx.utils.AnimationUtils
import net.aspw.nightx.utils.render.RenderUtils

fun Float.animSmooth(target: Float, speed: Float) =
    if (Fix.fixValue.get()) target else AnimationUtils.animate(
        target,
        this,
        speed * RenderUtils.deltaTime * 0.025F
    )

fun Float.animLinear(speed: Float, min: Float, max: Float) = if (Fix.fixValue.get()) {
    if (speed < 0F) min else max
} else (this + speed).coerceIn(min, max)