package com.example

import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.EnergyStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testEnergyStatusThresholds() {
        assertEquals(EnergyStatus.DEFICIT, EnergyStatus.fromNet(-150))
        assertEquals(EnergyStatus.DEFICIT, EnergyStatus.fromNet(-500))

        assertEquals(EnergyStatus.MAINTENANCE, EnergyStatus.fromNet(-149))
        assertEquals(EnergyStatus.MAINTENANCE, EnergyStatus.fromNet(0))
        assertEquals(EnergyStatus.MAINTENANCE, EnergyStatus.fromNet(150))

        assertEquals(EnergyStatus.SURPLUS, EnergyStatus.fromNet(151))
        assertEquals(EnergyStatus.SURPLUS, EnergyStatus.fromNet(400))
    }

    @Test
    fun testMaintenanceScenario() {
        // Intake 2100, Active burn 92
        // TEF = 210
        // TDEE = 1608 + 400 + 210 + 92 = 2310
        // Net = 2100 - 2310 = -210 -> Deficit
        // If intake 2200, active burn 0 -> TEF 220 -> TDEE = 1608 + 400 + 220 = 2228 -> Net = -28 -> Maintenance
        val summary = DailySummary(
            date = "2026-10-18",
            intakeKcal = 2200,
            carbsG = 250,
            fatG = 60,
            proteinG = 130,
            sodiumMg = 2000,
            activeBurnKcal = 0,
            workoutTitle = "Rest Day",
            weightKg = 68.4,
            weightSource = DeviceSource.HUAWEI_SCALE,
            smmKg = 32.8,
            bodyFatPct = 13.8,
            aptCm = 11.7,
            sleepHrvMs = 85
        )

        assertEquals(220, summary.tef)
        assertEquals(2228, summary.tdee)
        assertEquals(-28, summary.netBalance)
        assertEquals(EnergyStatus.MAINTENANCE, summary.status)
    }
}
