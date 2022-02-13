package com.openclassrooms.realestatemanager.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@RunWith(JUnit4::class)
class UtilsTest {

    @Test
    fun getCurrentDayShouldReturnToday() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val currentDayExpected = dateFormat.format(Date())
        val currentDayToTest = dateFormat.format(Utils.getTodayDate())

        assertEquals(currentDayExpected, currentDayToTest)
    }

    @Test
    fun convertDollarToEuroShouldReturnSameAmount() {
        val dollarAmount = 250000
        val euroResultExpected = (dollarAmount * 0.88).roundToInt()
        val euroResultToTest = Utils.convertDollarToEuro(dollarAmount)
        assertEquals(euroResultExpected, euroResultToTest)
    }

}