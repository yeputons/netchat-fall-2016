package net.yeputons.spbau.fall2016.netchat

import net.ldvsoft.spbau.messenger.protocol.P2PMessenger
import java.util.*

/**
 * Helper class which contains functions for converting timestamps used in protobuf messages into java.util.Date.
 */
object ProtobufHelper {
    fun intToDate(value: Long): Date {
        return Date(value)
    }

    fun dateToInt(value: Date): Long {
        return value.time
    }
}