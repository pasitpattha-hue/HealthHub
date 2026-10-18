package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.EnergyStatus
import com.example.data.model.HealthConstants
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HealthHub", appName)
    }

    @Test
    fun testGasV4EnergyBalanceFormula() {
        // Daily Intake = 2420, Active Burn = 0 (Rest Day)
        val summary = DailySummary(
            date = "2026-10-18",
            intakeKcal = 2420,
            carbsG = 242,
            fatG = 113,
            proteinG = 103,
            sodiumMg = 3640,
            activeBurnKcal = 0,
            workoutTitle = "พัก (Rest Day)",
            weightKg = 74.6,
            weightSource = DeviceSource.VISBODY_3D,
            smmKg = 26.7,
            bodyFatPct = 35.4,
            aptCm = 11.7,
            sleepHrvMs = 84
        )

        // TEF = round(2420 * 0.10) = 242
        assertEquals(242, summary.tef)

        // TDEE = 1608 + 400 + 242 + 0 = 2250
        assertEquals(2250, summary.tdee)

        // Net = 2420 - 2250 = +170
        assertEquals(170, summary.netBalance)

        // Net > +150 is Calorie Surplus (⚡ Calorie Surplus)
        assertEquals(EnergyStatus.SURPLUS, summary.status)
    }
}
