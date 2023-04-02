package net.aspw.client.visual.client.clickgui.extensions

import net.aspw.client.features.api.GuiFastRender
import net.aspw.client.utils.AnimationUtils
import net.aspw.client.utils.render.RenderUtils

fun Float.animSmooth(target: Float, speed: Float) =
    if (GuiFastRender.fixValue.get()) target else AnimationUtils.animate(
        target,
        this,
        speed * RenderUtils.deltaTime * 0.025F
    )

fun Float.animLinear(speed: Float, min: Float, max: Float) = if (GuiFastRender.fixValue.get()) {
    if (speed < 0F) min else max
} else (this + speed).coerceIn(min, max)