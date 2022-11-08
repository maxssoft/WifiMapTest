package com.maxssoft.wifimaptest.util.logger

import android.util.Log

/**
 * Обертка над системным LogCat для более удобного логирования данных
 *
 * @author Сидоров Максим on 07.11.2022
 */
interface Logger {
    fun d(block: () -> String)
    fun w(block: () -> String)
    fun w(exception: Exception?, block: () -> String)
    fun e(block: () -> String)
    fun e(exception: Exception?, block: () -> String)
}

@Suppress("OVERRIDE_BY_INLINE")
class SimpleLogger(val tag: String) : Logger {

    override inline fun d(block: () -> String) {
        Log.d(tag, block.invoke())
    }

    override inline fun w(block: () -> String) {
        w(null, block)
    }

    override inline fun w(exception: Exception?, block: () -> String) {
        Log.w(tag, block.invoke(), exception)
    }

    override inline fun e(block: () -> String) {
        e(null, block)
    }

    override inline fun e(exception: Exception?, block: () -> String) {
        Log.e(tag, block.invoke(), exception)
    }
}

/**
 * Фабрика, создающая [Logger] с заданным тегом
 */
class LoggerFactory {
    fun get(tag: String): Logger = SimpleLogger(tag)
}