package net.aspw.client.util

class TimerUtils {
    private var lastMS: Long = 0
    private val currentMS: Long
        private get() = System.nanoTime() / 1000000L

    fun hasReached(milliseconds: Double): Boolean {
        return currentMS - lastMS >= milliseconds
    }

    fun reset() {
        lastMS = currentMS
    }

    fun delay(milliSec: Float): Boolean {
        return time - lastMS >= milliSec
    }

    val time: Long
        get() = currentMS - lastMS //System.nanoTime() / 1000000L;
}