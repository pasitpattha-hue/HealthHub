package com.example

import com.example.data.local.DailyLogEntity
import com.example.data.local.MicrocycleWorkoutEntity
import com.example.data.local.NutritionEntryEntity
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.EnergyStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    @Test
    fun testRoomDatabaseEntitiesForOfflinePersistence() {
        // 1. DailyLogEntity verification
        val dailyLog = DailyLogEntity(
            date = "2026-10-18",
            intakeKcal = 2420,
            carbsG = 242,
            fatG = 113,
            proteinG = 103,
            sodiumMg = 3640,
            activeBurnKcal = 0,
            workoutTitle = "พัก (Rest Day)",
            weightKg = 74.6,
            weightSource = "Visbody 3D",
            smmKg = 26.7,
            bodyFatPct = 35.4,
            aptCm = 11.7,
            sleepHrvMs = 84,
            isSynced = false
        )

        assertEquals("2026-10-18", dailyLog.date)
        assertEquals(242, dailyLog.tef)
        assertEquals(2250, dailyLog.tdee)
        assertEquals(170, dailyLog.netBalance)
        assertFalse(dailyLog.isSynced)

        // 2. NutritionEntryEntity verification
        val nutritionEntry = NutritionEntryEntity(
            id = 1L,
            date = "2026-10-18",
            time = "08:30",
            mealType = "อาหารจานหลัก",
            name = "ข้าวกะเพราไก่ + ไข่ดาว",
            calories = 650,
            proteinG = 32.0,
            carbsG = 68.0,
            fatG = 27.0,
            sodiumMg = 950,
            isSynced = false
        )

        assertEquals(650, nutritionEntry.calories)
        assertEquals(32.0, nutritionEntry.proteinG, 0.01)
        assertEquals("อาหารจานหลัก", nutritionEntry.mealType)
        assertFalse(nutritionEntry.isSynced)

        // 3. MicrocycleWorkoutEntity verification
        val workout = MicrocycleWorkoutEntity(
            id = 1L,
            dayCode = "P-03",
            dayName = "Tuesday",
            sessionTitle = "Heavy Leg Day & Shin Protection",
            exerciseName = "Barbell Back Squat",
            warmUpSets = "11kg x 10, 41kg x 8, 61kg x 5, 71kg x 3",
            workingSets = "Top Set: 121kg x 5 reps (RPE 8.5) + Back-off 95kg 3x8",
            topWeightKg = 121.0,
            completedSets = 5,
            totalSets = 5,
            coachCue = "Root tripod foot into floor, brace 360° core",
            isCompleted = true,
            isSynced = true
        )

        assertEquals("P-03", workout.dayCode)
        assertEquals(121.0, workout.topWeightKg, 0.01)
        assertTrue(workout.isCompleted)
        assertTrue(workout.isSynced)
    }
}
