package net.yeputons.spbau.fall2016.netchat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ProtobufHelperTest {
    @Test fun testIntToDate() {
        val calendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        calendar.set(2016, Calendar.DECEMBER, 30, 19, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        assertEquals(calendar.time, ProtobufHelper.intToDate(1483124400000))
    }

    @Test fun testDateToInt() {
        val calendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
        calendar.set(2016, Calendar.DECEMBER, 30, 19, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        assertEquals(1483124400000, ProtobufHelper.dateToInt(calendar.time))
    }
}