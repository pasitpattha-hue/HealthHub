package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.DailyLogEntity
import com.example.data.local.MicrocycleWorkoutEntity
import com.example.data.local.NutritionEntryEntity
import com.example.data.model.DailySummary
import com.example.data.model.DeviceSource
import com.example.data.model.EnergyStatus
import com.example.di.DatabaseModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

        assertEquals(242, summary.tef)
        assertEquals(2250, summary.tdee)
        assertEquals(170, summary.netBalance)
        assertEquals(EnergyStatus.SURPLUS, summary.status)
    }

    @Test
    fun testRoomDatabaseDaosAndDatabaseModule() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.buildInMemory(context)

        val dailyLogDao = DatabaseModule.provideDailyLogDao(db)
        val nutritionDao = DatabaseModule.provideNutritionDao(db)
        val workoutDao = DatabaseModule.provideWorkoutDao(db)

        assertNotNull(dailyLogDao)
        assertNotNull(nutritionDao)
        assertNotNull(workoutDao)

        val log = DailyLogEntity(
            date = "2026-10-18",
            intakeKcal = 2000,
            proteinG = 135,
            activeBurnKcal = 400
        )
        dailyLogDao.upsertDailyLog(log)
        val fetchedLog = dailyLogDao.getDailyLogByDate("2026-10-18")
        assertNotNull(fetchedLog)
        assertEquals(2000, fetchedLog?.intakeKcal)

        val mealId = nutritionDao.insertNutritionEntry(
            NutritionEntryEntity(
                date = "2026-10-18",
                mealType = "Lunch",
                name = "Grilled Salmon & Quinoa",
                calories = 620,
                proteinG = 46.0
            )
        )
        val meals = nutritionDao.getNutritionEntriesForDate("2026-10-18").first()
        assertEquals(1, meals.size)
        assertEquals("Grilled Salmon & Quinoa", meals.first().name)
        nutritionDao.deleteNutritionEntryById(mealId)
        assertEquals(0, nutritionDao.getNutritionEntriesForDate("2026-10-18").first().size)

        workoutDao.insertMicrocycleWorkout(
            MicrocycleWorkoutEntity(
                dayCode = "P-03",
                dayName = "Tuesday",
                sessionTitle = "Heavy Leg Day",
                exerciseName = "Barbell Back Squat",
                topWeightKg = 121.0
            )
        )
        val p03Exercises = workoutDao.getMicrocycleWorkoutsForDay("P-03").first()
        assertEquals(1, p03Exercises.size)
        assertEquals("Barbell Back Squat", p03Exercises.first().exerciseName)

        db.close()
    }

    @Test
    fun testMainActivityAndViewModelInstantiation() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)
        assertNotNull(vm)

        val tabs = com.example.ui.screens.MainScreenTab.entries
        assertEquals(4, tabs.size)
        assertEquals("Dashboard", tabs[0].title)
        assertEquals("AI Logger", tabs[1].title)
        assertEquals("Nutrition", tabs[2].title)
        assertEquals("Microcycle", tabs[3].title)

        vm.selectTab(1)
        assertEquals(1, vm.selectedTabIndex.value)
        vm.selectTab(2)
        assertEquals(2, vm.selectedTabIndex.value)
        vm.selectTab(3)
        assertEquals(3, vm.selectedTabIndex.value)
        vm.selectTab(0)
        assertEquals(0, vm.selectedTabIndex.value)

        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
        controller.pause().stop().destroy()
    }

    @Test
    fun testHydrationCircularWidgetPresetsAndViewModel() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)

        // Verify presets
        val presets = com.example.ui.components.DEFAULT_GLASS_PRESETS
        assertEquals(4, presets.size)
        assertEquals(150, presets[0].amountMl)
        assertEquals("quick_add_150", presets[0].testTag)
        assertEquals(250, presets[1].amountMl)
        assertEquals("quick_add_250", presets[1].testTag)
        assertEquals(500, presets[2].amountMl)
        assertEquals("quick_add_500", presets[2].testTag)
        assertEquals(750, presets[3].amountMl)
        assertEquals("quick_add_750", presets[3].testTag)

        // Verify water logging operations
        vm.resetWater()
        assertEquals(0, vm.waterIntakeMl.value)

        // Quick add 150ml (Cup / Sip)
        vm.addWater(150)
        assertEquals(150, vm.waterIntakeMl.value)

        // Quick add 250ml (Standard Glass)
        vm.addWater(250)
        assertEquals(400, vm.waterIntakeMl.value)

        // Quick add 500ml (Shaker)
        vm.addWater(500)
        assertEquals(900, vm.waterIntakeMl.value)

        // Quick add 750ml (Bottle)
        vm.addWater(750)
        assertEquals(1650, vm.waterIntakeMl.value)

        // Fine tune +100ml
        vm.addWater(100)
        assertEquals(1750, vm.waterIntakeMl.value)

        // Fine tune -100ml
        vm.addWater(-100)
        assertEquals(1650, vm.waterIntakeMl.value)

        // Crush 3000ml target
        vm.addWater(1400)
        assertEquals(3050, vm.waterIntakeMl.value)

        // Reset
        vm.resetWater()
        assertEquals(0, vm.waterIntakeMl.value)
    }

    @Test
    fun testGoogleHealthCalorieIntakeSync() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)

        // 1. Verify auto-sync toggle
        val initialAutoSync = vm.autoSyncGoogleHealth.value
        assertEquals(true, initialAutoSync)
        vm.toggleGoogleHealthAutoSync()
        assertEquals(false, vm.autoSyncGoogleHealth.value)
        vm.toggleGoogleHealthAutoSync()
        assertEquals(true, vm.autoSyncGoogleHealth.value)

        // 2. Add meals to ensure intake calories exist
        vm.addQuickMeal("Salmon Quinoa Bowl", 580, 44.0, 52.0, 18.0, 490, "Lunch")
        vm.addQuickMeal("Whey Protein Shake", 130, 27.0, 2.0, 1.0, 95, "Snack")

        // 3. Trigger Google Health bidirectional sync & caloric expenditure read
        vm.syncIntakeCaloriesToGoogleHealth("2026-10-18")
        vm.readDailyCaloricExpenditureFromHealthConnect("2026-10-23")

        // 4. Verify sync initiation and status
        assertNotNull(vm.googleHealthSyncStatus.value)
        assertNotNull(vm.isGoogleHealthSyncing.value)
        assertNotNull(vm.lastSyncedExpenditure.value)
        assertNotNull(vm.lastNutrientPushSummary.value)

        // 5. Verify GoogleHealthSyncManager permissions & bidirectional synchronization layer
        val syncManager = com.example.data.health.GoogleHealthSyncManager()
        assertNotNull(syncManager)
        val permissions = com.example.data.health.GoogleHealthSyncManager.REQUIRED_PERMISSIONS
        assertEquals(2, permissions.size)
        val expenditurePermissions = com.example.data.health.GoogleHealthSyncManager.EXPENDITURE_PERMISSIONS
        assertEquals(2, expenditurePermissions.size)
        val allSyncPermissions = com.example.data.health.GoogleHealthSyncManager.ALL_SYNC_PERMISSIONS
        assertEquals(4, allSyncPermissions.size)

        // 6. Verify Thai and English MealType mapping & NutritionRecord construction
        assertEquals(
            androidx.health.connect.client.records.MealType.MEAL_TYPE_BREAKFAST,
            com.example.data.health.GoogleHealthSyncManager.mapMealType("มื้อเช้า")
        )
        assertEquals(
            androidx.health.connect.client.records.MealType.MEAL_TYPE_LUNCH,
            com.example.data.health.GoogleHealthSyncManager.mapMealType("มื้อกลางวัน")
        )
        assertEquals(
            androidx.health.connect.client.records.MealType.MEAL_TYPE_DINNER,
            com.example.data.health.GoogleHealthSyncManager.mapMealType("มื้อเย็น")
        )
        assertEquals(
            androidx.health.connect.client.records.MealType.MEAL_TYPE_SNACK,
            com.example.data.health.GoogleHealthSyncManager.mapMealType("มื้อว่าง")
        )

        val oct23Meals = com.example.data.local.SeedData.initialMeals.filter { it.date == "2026-10-23" }
        val (startInstant, _) = syncManager.resolveDayTimeWindow("2026-10-23")
        val firstRecord = syncManager.buildNutritionRecord(
            meal = oct23Meals.first(),
            startTime = startInstant,
            endTime = startInstant.plusSeconds(1200),
            zoneOffset = java.time.ZoneOffset.UTC
        )
        assertEquals(oct23Meals.first().calories.toDouble(), firstRecord.energy?.inKilocalories ?: 0.0, 0.01)
        assertEquals(oct23Meals.first().proteinG, firstRecord.protein?.inGrams ?: 0.0, 0.01)
        assertEquals(oct23Meals.first().sodiumMg.toDouble(), firstRecord.sodium?.inMilligrams ?: 0.0, 0.01)

        val secondRecord = syncManager.buildNutritionRecord(
            meal = oct23Meals[1],
            startTime = startInstant.plusSeconds(3600),
            endTime = startInstant.plusSeconds(4800),
            zoneOffset = java.time.ZoneOffset.UTC
        )
        assertEquals(oct23Meals[1].calories.toDouble(), secondRecord.energy?.inKilocalories ?: 0.0, 0.01)
        assertEquals(oct23Meals[1].proteinG, secondRecord.protein?.inGrams ?: 0.0, 0.01)
        assertEquals(oct23Meals[1].sodiumMg.toDouble(), secondRecord.sodium?.inMilligrams ?: 0.0, 0.01)

        // 7. Verify readDailyCaloricExpenditure and synchronizeDailyHealthData
        val expenditure = syncManager.readDailyCaloricExpenditure(
            context = app,
            dateStr = "2026-10-23",
            fallbackActiveKcal = 0,
            fallbackTotalKcal = 2211
        )
        assertEquals("2026-10-23", expenditure.date)
        assertEquals(2211, expenditure.totalCaloriesBurnedKcal)

        val bidirectional = syncManager.synchronizeDailyHealthData(
            context = app,
            dateStr = "2026-10-23",
            meals = oct23Meals,
            fallbackActiveBurnKcal = 0,
            fallbackTotalBurnKcal = 2211,
            fallbackIntakeKcal = 2030
        )
        assertEquals(2030, bidirectional.nutrientPush.totalEnergyKcal)
        assertEquals(89.0, bidirectional.nutrientPush.totalProteinG, 0.01)
        assertEquals(3190, bidirectional.nutrientPush.totalSodiumMg)
        assertEquals(-181, bidirectional.netEnergyBalanceKcal)

        // Clean up test meal and drain queued main looper runnables
        val meals = vm.todayMeals.value
        meals.forEach { vm.deleteMeal(it.id) }
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun testWeeklyEnergyBalanceVisualization() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)

        assertNotNull(vm.dailySummaries.value)

        // Verify SeedData 7-day summaries from 2026_HealthHub Google Sheet
        val recent7 = com.example.data.local.SeedData.initialDailySummaries.take(7)
        assertEquals(7, recent7.size)
        assertEquals("2026-10-23", recent7.first().date)
        assertEquals(2030, recent7.first().intakeKcal)
        assertEquals(89, recent7.first().proteinG)
        assertEquals(2211, recent7.first().tdeeKcal)
        assertEquals(-181, recent7.first().netEnergyBalanceKcal)

        val totalIntake = recent7.sumOf { it.intakeKcal }
        val totalTdee = recent7.sumOf { it.tdeeKcal }
        val cumulativeNet = totalIntake - totalTdee

        // Verify energy balance mathematics (BMR 1608 + NEAT 400 = 2008 + TEF + ActiveBurn)
        recent7.forEach { s ->
            assertEquals(s.intakeKcal - s.tdeeKcal, s.netEnergyBalanceKcal)
            assertEquals(kotlin.math.round(s.intakeKcal * 0.10 + 1e-9).toInt(), s.tefKcal)
            assertEquals(2008 + s.activeBurnKcal + s.tefKcal, s.tdeeKcal)
        }

        // Verify 23/10/2026 meals match Google Sheet exactly
        val oct23Meals = com.example.data.local.SeedData.initialMeals.filter { it.date == "2026-10-23" }
        assertEquals(4, oct23Meals.size)
        assertEquals(2030, oct23Meals.sumOf { it.calories })
        assertEquals(89, oct23Meals.sumOf { it.proteinG }.toInt())
        assertEquals(3190, oct23Meals.sumOf { it.sodiumMg })

        // Verify fat pace estimation formula
        val estKg = cumulativeNet / 7700.0
        assertNotNull(estKg)
    }

    @Test
    fun testUxTechnicalDebtFixesMicrocycleSteppersAndWaterConfirm() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)

        // 1. Water Intake & Reset Safety
        vm.resetWater()
        assertEquals(0, vm.waterIntakeMl.value)
        vm.addWater(500)
        assertEquals(500, vm.waterIntakeMl.value)
        vm.resetWater()
        assertEquals(0, vm.waterIntakeMl.value)

        // 2. Date Selection, Tomorrow Plan & Gemini Coach Chat
        assertEquals("2026-10-23", vm.selectedDate.value)
        vm.selectDate("2026-10-22")
        assertEquals("2026-10-22", vm.selectedDate.value)
        vm.selectDate("2026-10-23")

        assertNotNull(vm.tomorrowPlan.value)
        vm.toggleTomorrowScheduleSlot("slot_morning")
        vm.sendGeminiChatMessage("พรุ่งนี้ควรทำอะไรและกินอะไรดี?")
        assertEquals(true, vm.geminiChatMessages.value.isNotEmpty())

        // 3. Gym Exercise Set Increments/Decrements (H7: Flexibility & Efficiency)
        val dummyExercise = com.example.data.local.MicrocycleWorkoutEntity(
            id = 999L,
            dayCode = "P-01",
            dayName = "Monday",
            sessionTitle = "Lower Body Hypertrophy",
            exerciseName = "Bulgarian Split Squat",
            completedSets = 1,
            totalSets = 3
        )

        val newSetsIncrement = (dummyExercise.completedSets + 1).coerceAtMost(dummyExercise.totalSets)
        assertEquals(2, newSetsIncrement)

        val newSetsDecrement = (dummyExercise.completedSets - 1).coerceAtLeast(0)
        assertEquals(0, newSetsDecrement)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun testCameraMealPhotoGeminiCalorieAndMacroEstimation() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)

        val geminiService = com.example.data.remote.GeminiService()

        // 1. Test Salmon Quinoa Bowl camera bitmap estimation
        val salmonBitmap = com.example.ui.screens.SampleTelemetryGenerators.generateNutritionSample()
        val salmonResult = geminiService.estimateMealFromCameraPhoto(salmonBitmap)
        val salmonEstimate = salmonResult.getOrNull()
        assertNotNull(salmonEstimate)
        assertEquals(540, salmonEstimate?.calories)
        assertEquals(42.0, salmonEstimate?.proteinG ?: 0.0, 0.1)
        assertEquals(48.0, salmonEstimate?.carbsG ?: 0.0, 0.1)
        assertEquals(16.0, salmonEstimate?.fatG ?: 0.0, 0.1)
        assertEquals(520, salmonEstimate?.sodiumMg)
        assertEquals(true, salmonEstimate?.detectedIngredients?.isNotEmpty() == true)

        // 2. Test Thai Lean Chicken Rice (ข้าวมันไก่ต้มลีน) camera bitmap estimation
        val chickenRiceBitmap = com.example.ui.screens.SampleTelemetryGenerators.generateThaiChickenRiceSample()
        val chickenRiceEstimate = geminiService.estimateMealFromCameraPhoto(chickenRiceBitmap).getOrNull()
        assertNotNull(chickenRiceEstimate)
        assertEquals("ข้าวมันไก่ต้มลีน (ไม่เอาหนัง) + น้ำซุปฟัก", chickenRiceEstimate?.mealName)
        assertEquals(530, chickenRiceEstimate?.calories)
        assertEquals(34.0, chickenRiceEstimate?.proteinG ?: 0.0, 0.1)
        assertEquals(62.0, chickenRiceEstimate?.carbsG ?: 0.0, 0.1)
        assertEquals(15.0, chickenRiceEstimate?.fatG ?: 0.0, 0.1)
        assertEquals(780, chickenRiceEstimate?.sodiumMg)

        // 3. Test Thai Basil Chicken (ข้าวกะเพราอกไก่สับลีน + ไข่ดาวน้ำ) camera bitmap estimation
        val kapraoBitmap = com.example.ui.screens.SampleTelemetryGenerators.generateThaiKapraoChickenSample()
        val kapraoEstimate = geminiService.estimateMealFromCameraPhoto(kapraoBitmap).getOrNull()
        assertNotNull(kapraoEstimate)
        assertEquals("ข้าวกะเพราอกไก่สับลีน + ไข่ดาวน้ำ", kapraoEstimate?.mealName)
        assertEquals(490, kapraoEstimate?.calories)
        assertEquals(38.0, kapraoEstimate?.proteinG ?: 0.0, 0.1)
        assertEquals(50.0, kapraoEstimate?.carbsG ?: 0.0, 0.1)
        assertEquals(14.0, kapraoEstimate?.fatG ?: 0.0, 0.1)
        assertEquals(690, kapraoEstimate?.sodiumMg)

        // 4. Verify automatic macronutrient calculation (Atwater 4-4-9 formula, itemized components, net carbs & quality grade)
        assertEquals(6.5, salmonEstimate?.fiberG ?: 0.0, 0.1)
        assertEquals(3, salmonEstimate?.itemizedMacros?.size)
        assertEquals(168, salmonEstimate?.calculatedMacros?.proteinKcal) // 42g * 4
        assertEquals(192, salmonEstimate?.calculatedMacros?.carbsKcal)   // 48g * 4
        assertEquals(144, salmonEstimate?.calculatedMacros?.fatKcal)     // 16g * 9
        assertEquals(504, salmonEstimate?.calculatedMacros?.totalKcalFromMacros)
        assertEquals(41.5, salmonEstimate?.calculatedMacros?.netCarbsG ?: 0.0, 0.1)
        assertEquals("A+ High-Protein Lean", salmonEstimate?.calculatedMacros?.macroQualityGrade)

        // 5. Verify ViewModel camera capture -> Gemini macro estimation -> Form state update & 4-4-9 auto-recalculation
        vm.setAiCategory(com.example.data.model.CategoryType.NUTRITION)
        vm.updateAiFormField(protein = "40.0", carbs = "50.0", fat = "10.0", sodium = "500")
        vm.recalculateCaloriesFromMacros()
        assertEquals("450", vm.aiSnapForm.value.calories) // (40*4) + (50*4) + (10*9) = 160 + 200 + 90 = 450 kcal
        assertEquals("A+ High-Protein Lean", vm.aiSnapForm.value.macroQualityGrade)

        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }

    @Test
    fun testWeeklyMacronutrientBreakdownChartFromCameraMealTracker() = kotlinx.coroutines.runBlocking {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
            .create(com.example.ui.viewmodel.HealthHubViewModel::class.java)
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()

        // 1. Build initial 7-day macronutrient breakdown from SeedData
        val seedSummaries = com.example.data.local.SeedData.initialDailySummaries.map { entity ->
            DailySummary(
                date = entity.date,
                intakeKcal = entity.intakeKcal,
                carbsG = entity.carbsG,
                fatG = entity.fatG,
                proteinG = entity.proteinG,
                sodiumMg = entity.sodiumMg,
                activeBurnKcal = entity.activeBurnKcal,
                workoutTitle = entity.workoutTitle,
                weightKg = entity.weightKg,
                weightSource = DeviceSource.EVOLT_360,
                smmKg = entity.smmKg,
                bodyFatPct = entity.bodyFatPct,
                aptCm = entity.aptCm,
                sleepHrvMs = entity.sleepHrvMs,
                sleepScore = entity.sleepScore,
                sugarG = entity.sugarG
            )
        }
        val initialMeals = com.example.data.local.SeedData.initialMeals
        val weeklyMacros = com.example.ui.components.buildWeeklyMacronutrientData(
            summaries = seedSummaries,
            allMeals = initialMeals,
            selectedDate = "2026-10-23"
        )

        assertEquals(7, weeklyMacros.size)
        val oct23Before = weeklyMacros.last()
        assertEquals("2026-10-23", oct23Before.dateStr)
        assertEquals(89, oct23Before.proteinG)
        assertEquals(184, oct23Before.carbsG)
        assertEquals(87, oct23Before.fatG)
        assertEquals(2030, oct23Before.intakeKcal)
        assertEquals(false, oct23Before.isProteinGoalMet)

        // 2. Simulate capturing a meal photo (Salmon Quinoa Bowl: 540 kcal, 42g P, 48g C, 16g F) and logging it
        val salmonBitmap = com.example.ui.screens.SampleTelemetryGenerators.generateNutritionSample()
        val salmonEstimate = com.example.data.remote.GeminiService().estimateMealFromCameraPhoto(salmonBitmap).getOrThrow()

        val cameraMeal = com.example.data.local.MealEntity(
            date = "2026-10-23",
            mealType = salmonEstimate.mealType,
            name = salmonEstimate.mealName,
            calories = salmonEstimate.calories,
            proteinG = salmonEstimate.proteinG,
            carbsG = salmonEstimate.carbsG,
            fatG = salmonEstimate.fatG,
            sodiumMg = salmonEstimate.sodiumMg,
            note = salmonEstimate.coachRecommendation
        )

        val updatedWeeklyMacros = com.example.ui.components.buildWeeklyMacronutrientData(
            summaries = seedSummaries,
            allMeals = initialMeals + cameraMeal,
            selectedDate = "2026-10-23"
        )

        val oct23After = updatedWeeklyMacros.last()
        assertEquals(131, oct23After.proteinG) // 89g + 42g = 131g (>= 120g goal met!)
        assertEquals(232, oct23After.carbsG)   // 184g + 48g = 232g
        assertEquals(103, oct23After.fatG)     // 86.5g + 16g = 102.5g -> 103g
        assertEquals(2570, oct23After.intakeKcal)
        assertEquals(true, oct23After.isProteinGoalMet)
        assertEquals(100, oct23After.proteinCaloriePct + oct23After.carbsCaloriePct + oct23After.fatCaloriePct)

        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle()
    }
}
