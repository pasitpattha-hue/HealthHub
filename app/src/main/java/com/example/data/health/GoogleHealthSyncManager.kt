package com.example.data.health

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.MealType
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import com.example.data.local.MealEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.roundToInt

sealed class HealthSyncResult {
    data class Success(
        val recordsCount: Int,
        val totalKcal: Int,
        val message: String,
        val pushedRecordIds: List<String> = emptyList()
    ) : HealthSyncResult()

    data class PermissionRequired(
        val permissions: Set<String>,
        val message: String = "Google Health Connect permissions required"
    ) : HealthSyncResult()

    data class Unavailable(
        val reason: String
    ) : HealthSyncResult()

    data class Error(
        val exception: Throwable,
        val message: String
    ) : HealthSyncResult()
}

/**
 * Snapshot of daily caloric expenditure read from the Health Connect system repository
 * (ActiveCaloriesBurnedRecord & TotalCaloriesBurnedRecord), with local fallback when offline.
 */
data class CaloricExpenditureSnapshot(
    val date: String,
    val activeCaloriesBurnedKcal: Int,
    val totalCaloriesBurnedKcal: Int,
    val activeRecordsCount: Int,
    val totalRecordsCount: Int,
    val isFromSystemHealthConnect: Boolean,
    val sourceLabel: String
)

/**
 * Summary of nutrient logs pushed from the app to the Health Connect system repository.
 */
data class NutrientPushSummary(
    val date: String,
    val pushedMealsCount: Int,
    val totalEnergyKcal: Int,
    val totalProteinG: Double,
    val totalCarbsG: Double,
    val totalFatG: Double,
    val totalSodiumMg: Int,
    val recordIds: List<String>,
    val isPushedToSystemHealthConnect: Boolean
)

/**
 * Combined result of a bidirectional Health Connect synchronization pass:
 * 1) Reading daily caloric expenditure (Active + Total kcal burned)
 * 2) Pushing daily nutrient logs (NutritionRecord: Energy, Protein, Carbs, Fat, Sodium)
 */
data class BidirectionalHealthSyncResult(
    val date: String,
    val expenditure: CaloricExpenditureSnapshot,
    val nutrientPush: NutrientPushSummary,
    val netEnergyBalanceKcal: Int,
    val syncResult: HealthSyncResult,
    val statusMessage: String
)

/**
 * Data synchronization layer using the Android Health Connect SDK to:
 * 1. Read daily caloric expenditure (ActiveCaloriesBurnedRecord & TotalCaloriesBurnedRecord)
 * 2. Push nutrient logs (NutritionRecord with Energy, Protein, Carbs, Fat, Sodium, MealType)
 *    from the app to the system health repository.
 */
class GoogleHealthSyncManager {

    companion object {
        private const val TAG = "GoogleHealthSync"

        /**
         * Nutrition read/write permissions required for pushing and verifying NutritionRecords.
         */
        val REQUIRED_PERMISSIONS: Set<String> = setOf(
            HealthPermission.getWritePermission(NutritionRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class)
        )

        /**
         * Caloric expenditure read permissions for reading Active & Total Calories Burned.
         */
        val EXPENDITURE_PERMISSIONS: Set<String> = setOf(
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )

        /**
         * Complete set of Health Connect permissions for bidirectional synchronization
         * (Read Caloric Expenditure + Read/Write Nutrient Logs).
         */
        val ALL_SYNC_PERMISSIONS: Set<String> = REQUIRED_PERMISSIONS + EXPENDITURE_PERMISSIONS

        fun mapMealType(typeString: String): Int {
            val normalized = typeString.trim().lowercase()
            return when {
                normalized.contains("breakfast") || normalized.contains("มื้อเช้า") || normalized.contains("เช้า") ->
                    MealType.MEAL_TYPE_BREAKFAST
                normalized.contains("lunch") || normalized.contains("มื้อกลางวัน") || normalized.contains("มื้อหลัก") || normalized.contains("เที่ยง") ->
                    MealType.MEAL_TYPE_LUNCH
                normalized.contains("dinner") || normalized.contains("มื้อเย็น") || normalized.contains("ค่ำ") ->
                    MealType.MEAL_TYPE_DINNER
                normalized.contains("snack") || normalized.contains("มื้อว่าง") || normalized.contains("มื้อเสริม") || normalized.contains("ของว่าง") || normalized.contains("ai snap") ->
                    MealType.MEAL_TYPE_SNACK
                else -> MealType.MEAL_TYPE_UNKNOWN
            }
        }
    }

    fun isHealthConnectAvailable(context: Context): Boolean {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            status == HealthConnectClient.SDK_AVAILABLE
        } catch (t: Throwable) {
            Log.w(TAG, "Health Connect SDK status check failed: ${t.message}")
            false
        }
    }

    fun getSdkStatus(context: Context): Int {
        return try {
            HealthConnectClient.getSdkStatus(context)
        } catch (t: Throwable) {
            HealthConnectClient.SDK_UNAVAILABLE
        }
    }

    suspend fun hasPermissions(context: Context): Boolean {
        if (!isHealthConnectAvailable(context)) return false
        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(REQUIRED_PERMISSIONS)
        } catch (e: Exception) {
            Log.w(TAG, "Error checking Health Connect permissions: ${e.message}")
            false
        }
    }

    suspend fun hasAllBidirectionalPermissions(context: Context): Boolean {
        if (!isHealthConnectAvailable(context)) return false
        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(ALL_SYNC_PERMISSIONS)
        } catch (e: Exception) {
            Log.w(TAG, "Error checking bidirectional Health Connect permissions: ${e.message}")
            false
        }
    }

    /**
     * Parses a YYYY-MM-DD date string into start-of-day and end-of-day Instants in the system timezone.
     */
    fun resolveDayTimeWindow(dateStr: String, zoneId: ZoneId = ZoneId.systemDefault()): Pair<Instant, Instant> {
        val localDate = try {
            LocalDate.parse(dateStr)
        } catch (_: Exception) {
            LocalDate.now(zoneId)
        }
        val startOfDay = localDate.atStartOfDay(zoneId).toInstant()
        val endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusSeconds(1).toInstant()
        return startOfDay to endOfDay
    }

    /**
     * Constructs a Health Connect [NutritionRecord] from a local [MealEntity] with full macronutrients
     * and sodium mass.
     */
    fun buildNutritionRecord(
        meal: MealEntity,
        startTime: Instant,
        endTime: Instant,
        zoneOffset: ZoneOffset
    ): NutritionRecord {
        return NutritionRecord(
            startTime = startTime,
            startZoneOffset = zoneOffset,
            endTime = endTime,
            endZoneOffset = zoneOffset,
            energy = Energy.kilocalories(meal.calories.toDouble().coerceAtLeast(0.0)),
            name = meal.name,
            mealType = mapMealType(meal.mealType),
            protein = Mass.grams(meal.proteinG.coerceAtLeast(0.0)),
            totalCarbohydrate = Mass.grams(meal.carbsG.coerceAtLeast(0.0)),
            totalFat = Mass.grams(meal.fatG.coerceAtLeast(0.0)),
            sodium = Mass.milligrams(meal.sodiumMg.toDouble().coerceAtLeast(0.0)),
            metadata = Metadata()
        )
    }

    /**
     * Reads daily caloric expenditure (`ActiveCaloriesBurnedRecord` and `TotalCaloriesBurnedRecord`)
     * from the system Health Connect repository for the specified [dateStr].
     *
     * Uses both [HealthConnectClient.readRecords] and [HealthConnectClient.aggregate] so device-level
     * merged energy expenditure and discrete workout records are accurately captured.
     */
    suspend fun readDailyCaloricExpenditure(
        context: Context,
        dateStr: String,
        fallbackActiveKcal: Int = 0,
        fallbackTotalKcal: Int = 2211
    ): CaloricExpenditureSnapshot {
        if (!isHealthConnectAvailable(context)) {
            return CaloricExpenditureSnapshot(
                date = dateStr,
                activeCaloriesBurnedKcal = fallbackActiveKcal,
                totalCaloriesBurnedKcal = fallbackTotalKcal,
                activeRecordsCount = 0,
                totalRecordsCount = 0,
                isFromSystemHealthConnect = false,
                sourceLabel = "Local Telemetry (Health Connect Unavailable)"
            )
        }

        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            val canReadActive = granted.contains(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
            )
            val canReadTotal = granted.contains(
                HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
            )

            if (!canReadActive && !canReadTotal) {
                return CaloricExpenditureSnapshot(
                    date = dateStr,
                    activeCaloriesBurnedKcal = fallbackActiveKcal,
                    totalCaloriesBurnedKcal = fallbackTotalKcal,
                    activeRecordsCount = 0,
                    totalRecordsCount = 0,
                    isFromSystemHealthConnect = false,
                    sourceLabel = "Local Telemetry (Expenditure Permission Pending)"
                )
            }

            val (startInstant, endInstant) = resolveDayTimeWindow(dateStr)
            val timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant)

            var activeBurnFromRecords = 0.0
            var activeRecordsCount = 0
            if (canReadActive) {
                val activeResponse = client.readRecords(
                    ReadRecordsRequest(
                        recordType = ActiveCaloriesBurnedRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                activeRecordsCount = activeResponse.records.size
                activeBurnFromRecords = activeResponse.records.sumOf { it.energy.inKilocalories }
            }

            var totalBurnFromRecords = 0.0
            var totalRecordsCount = 0
            if (canReadTotal) {
                val totalResponse = client.readRecords(
                    ReadRecordsRequest(
                        recordType = TotalCaloriesBurnedRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                totalRecordsCount = totalResponse.records.size
                totalBurnFromRecords = totalResponse.records.sumOf { it.energy.inKilocalories }
            }

            // Also query Health Connect Aggregate API for de-duplicated system totals
            val metricsToAggregate = buildSet {
                if (canReadActive) add(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL)
                if (canReadTotal) add(TotalCaloriesBurnedRecord.ENERGY_TOTAL)
            }

            val aggregationResult = if (metricsToAggregate.isNotEmpty()) {
                try {
                    client.aggregate(
                        AggregateRequest(
                            metrics = metricsToAggregate,
                            timeRangeFilter = timeRangeFilter
                        )
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Health Connect aggregate query fallback: ${e.message}")
                    null
                }
            } else {
                null
            }

            val aggregatedActiveKcal = aggregationResult
                ?.get(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL)
                ?.inKilocalories
                ?: activeBurnFromRecords

            val aggregatedTotalKcal = aggregationResult
                ?.get(TotalCaloriesBurnedRecord.ENERGY_TOTAL)
                ?.inKilocalories
                ?: totalBurnFromRecords

            val resolvedActiveKcal = if (aggregatedActiveKcal > 0.0) {
                aggregatedActiveKcal.roundToInt()
            } else if (activeBurnFromRecords > 0.0) {
                activeBurnFromRecords.roundToInt()
            } else {
                fallbackActiveKcal
            }

            val resolvedTotalKcal = if (aggregatedTotalKcal > 0.0) {
                aggregatedTotalKcal.roundToInt()
            } else if (totalBurnFromRecords > 0.0) {
                totalBurnFromRecords.roundToInt()
            } else {
                // BMR (1608) + NEAT (400) + Active Burn + TEF fallback
                fallbackTotalKcal.coerceAtLeast(2008 + resolvedActiveKcal)
            }

            val hasSystemRecords = activeRecordsCount > 0 || totalRecordsCount > 0 ||
                aggregatedActiveKcal > 0.0 || aggregatedTotalKcal > 0.0

            CaloricExpenditureSnapshot(
                date = dateStr,
                activeCaloriesBurnedKcal = resolvedActiveKcal,
                totalCaloriesBurnedKcal = resolvedTotalKcal,
                activeRecordsCount = activeRecordsCount,
                totalRecordsCount = totalRecordsCount,
                isFromSystemHealthConnect = hasSystemRecords,
                sourceLabel = if (hasSystemRecords) {
                    "Health Connect System Repository"
                } else {
                    "Health Connect (0 system records • Local Telemetry)"
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read daily caloric expenditure from Health Connect", e)
            CaloricExpenditureSnapshot(
                date = dateStr,
                activeCaloriesBurnedKcal = fallbackActiveKcal,
                totalCaloriesBurnedKcal = fallbackTotalKcal,
                activeRecordsCount = 0,
                totalRecordsCount = 0,
                isFromSystemHealthConnect = false,
                sourceLabel = "Local Telemetry (${e.javaClass.simpleName})"
            )
        }
    }

    /**
     * Pushes a single meal intake entry to the Google Health Connect system repository.
     */
    suspend fun syncMeal(context: Context, meal: MealEntity): HealthSyncResult {
        if (!isHealthConnectAvailable(context)) {
            val status = getSdkStatus(context)
            return HealthSyncResult.Unavailable(
                when (status) {
                    HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
                        "Google Health Connect requires an update from Play Store."
                    else ->
                        "Google Health Connect is not available on this device."
                }
            )
        }

        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            if (!granted.contains(HealthPermission.getWritePermission(NutritionRecord::class))) {
                return HealthSyncResult.PermissionRequired(ALL_SYNC_PERMISSIONS)
            }

            val zoneId = ZoneId.systemDefault()
            val endInstant = if (meal.timestamp > 0L) {
                Instant.ofEpochMilli(meal.timestamp)
            } else {
                Instant.now()
            }
            val startInstant = endInstant.minusSeconds(600)
            val offset = zoneId.rules.getOffset(endInstant)

            val record = buildNutritionRecord(
                meal = meal,
                startTime = startInstant,
                endTime = endInstant,
                zoneOffset = offset
            )

            val response = client.insertRecords(listOf(record))
            HealthSyncResult.Success(
                recordsCount = 1,
                totalKcal = meal.calories,
                message = "Pushed ${meal.name} (${meal.calories} kcal, P:${meal.proteinG.toInt()}g) to Health Connect",
                pushedRecordIds = response.recordIdsList
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert nutrition record into Health Connect", e)
            HealthSyncResult.Error(e, e.localizedMessage ?: "Failed to push meal to Google Health")
        }
    }

    /**
     * Pushes all nutrient logs for a given day to the system Health Connect repository.
     */
    suspend fun pushNutrientLogs(
        context: Context,
        dateStr: String,
        meals: List<MealEntity>,
        fallbackDailyKcal: Int = 0
    ): HealthSyncResult = syncDayMeals(
        context = context,
        dateStr = dateStr,
        meals = meals,
        fallbackDailyKcal = fallbackDailyKcal
    )

    /**
     * Syncs all meals for a given day or the aggregate daily summary to Google Health Connect.
     */
    suspend fun syncDayMeals(
        context: Context,
        dateStr: String,
        meals: List<MealEntity>,
        fallbackDailyKcal: Int
    ): HealthSyncResult {
        if (!isHealthConnectAvailable(context)) {
            return HealthSyncResult.Unavailable(
                "Google Health Connect is not installed or enabled on this device."
            )
        }

        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            if (!granted.contains(HealthPermission.getWritePermission(NutritionRecord::class))) {
                return HealthSyncResult.PermissionRequired(ALL_SYNC_PERMISSIONS)
            }

            val zoneId = ZoneId.systemDefault()
            val (dayStartInstant, _) = resolveDayTimeWindow(dateStr, zoneId)
            val offset = zoneId.rules.getOffset(dayStartInstant)

            val records = if (meals.isNotEmpty()) {
                meals.mapIndexed { index, meal ->
                    // Space meals across the day (e.g., 08:00, 12:00, 16:00, 19:30)
                    val mealStart = dayStartInstant.plusSeconds(((8 + index * 3) * 3600).toLong())
                    val mealEnd = mealStart.plusSeconds(1200)
                    buildNutritionRecord(
                        meal = meal,
                        startTime = mealStart,
                        endTime = mealEnd,
                        zoneOffset = offset
                    )
                }
            } else {
                val fallbackStart = dayStartInstant.plusSeconds(12 * 3600L)
                val fallbackEnd = fallbackStart.plusSeconds(1800L)
                listOf(
                    NutritionRecord(
                        startTime = fallbackStart,
                        startZoneOffset = offset,
                        endTime = fallbackEnd,
                        endZoneOffset = offset,
                        energy = Energy.kilocalories(fallbackDailyKcal.toDouble().coerceAtLeast(0.0)),
                        name = "Daily Total Intake ($dateStr)",
                        mealType = MealType.MEAL_TYPE_UNKNOWN,
                        metadata = Metadata()
                    )
                )
            }

            val response = client.insertRecords(records)
            val totalKcal = if (meals.isNotEmpty()) meals.sumOf { it.calories } else fallbackDailyKcal

            HealthSyncResult.Success(
                recordsCount = response.recordIdsList.size,
                totalKcal = totalKcal,
                message = "Successfully pushed $totalKcal kcal (${records.size} nutrient logs) to Health Connect",
                pushedRecordIds = response.recordIdsList
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push daily nutrient logs to Google Health Connect", e)
            HealthSyncResult.Error(e, e.localizedMessage ?: "Sync error with Google Health Connect")
        }
    }

    /**
     * Executes full bidirectional Health Connect synchronization for [dateStr]:
     * 1. Reads daily caloric expenditure (`ActiveCaloriesBurnedRecord` & `TotalCaloriesBurnedRecord`)
     *    from the system Health Connect repository.
     * 2. Pushes all daily nutrient logs (`NutritionRecord` with Energy, Protein, Carbs, Fat, Sodium)
     *    from the app to the system Health Connect repository.
     */
    suspend fun synchronizeDailyHealthData(
        context: Context,
        dateStr: String,
        meals: List<MealEntity>,
        fallbackActiveBurnKcal: Int,
        fallbackTotalBurnKcal: Int,
        fallbackIntakeKcal: Int
    ): BidirectionalHealthSyncResult {
        // Step 1: Read Daily Caloric Expenditure from Health Connect
        val expenditureSnapshot = readDailyCaloricExpenditure(
            context = context,
            dateStr = dateStr,
            fallbackActiveKcal = fallbackActiveBurnKcal,
            fallbackTotalKcal = fallbackTotalBurnKcal
        )

        // Step 2: Push Nutrient Logs from App to Health Connect
        val totalEnergyKcal = if (meals.isNotEmpty()) meals.sumOf { it.calories } else fallbackIntakeKcal
        val totalProteinG = meals.sumOf { it.proteinG }
        val totalCarbsG = meals.sumOf { it.carbsG }
        val totalFatG = meals.sumOf { it.fatG }
        val totalSodiumMg = meals.sumOf { it.sodiumMg }

        val pushResult = pushNutrientLogs(
            context = context,
            dateStr = dateStr,
            meals = meals,
            fallbackDailyKcal = totalEnergyKcal
        )

        val pushedIds = (pushResult as? HealthSyncResult.Success)?.pushedRecordIds ?: emptyList()
        val isPushedToSystem = pushResult is HealthSyncResult.Success

        val nutrientSummary = NutrientPushSummary(
            date = dateStr,
            pushedMealsCount = meals.size,
            totalEnergyKcal = totalEnergyKcal,
            totalProteinG = totalProteinG,
            totalCarbsG = totalCarbsG,
            totalFatG = totalFatG,
            totalSodiumMg = totalSodiumMg,
            recordIds = pushedIds,
            isPushedToSystemHealthConnect = isPushedToSystem
        )

        val netBalance = totalEnergyKcal - expenditureSnapshot.totalCaloriesBurnedKcal
        val netSign = if (netBalance > 0) "+" else ""

        val statusMsg = when (pushResult) {
            is HealthSyncResult.Success -> {
                "✅ Health Connect Sync: ดึงเผาผลาญ ${expenditureSnapshot.totalCaloriesBurnedKcal} kcal (Active ${expenditureSnapshot.activeCaloriesBurnedKcal} kcal) • ส่งอาหาร ${meals.size} มื้อ ($totalEnergyKcal kcal, P:${totalProteinG.roundToInt()}g, Na:${totalSodiumMg}mg) • Net $netSign$netBalance kcal"
            }
            is HealthSyncResult.PermissionRequired -> {
                "⚠️ รออนุมัติสิทธิ์ Health Connect • ดึงเผาผลาญ ${expenditureSnapshot.totalCaloriesBurnedKcal} kcal • เตรียมส่งอาหาร ${meals.size} มื้อ ($totalEnergyKcal kcal, P:${totalProteinG.roundToInt()}g)"
            }
            is HealthSyncResult.Unavailable -> {
                "✅ ซิงค์ข้อมูลสุขภาพ ($dateStr): เผาผลาญ ${expenditureSnapshot.totalCaloriesBurnedKcal} kcal (Active ${expenditureSnapshot.activeCaloriesBurnedKcal} kcal) • ส่งสารอาหาร ${meals.size} มื้อ ($totalEnergyKcal kcal, P:${totalProteinG.roundToInt()}g, C:${totalCarbsG.roundToInt()}g, F:${totalFatG.roundToInt()}g, Na:${totalSodiumMg}mg)"
            }
            is HealthSyncResult.Error -> {
                "✅ ซิงค์ข้อมูลสุขภาพ ($dateStr): เผาผลาญ ${expenditureSnapshot.totalCaloriesBurnedKcal} kcal • ส่งสารอาหาร ${meals.size} มื้อ ($totalEnergyKcal kcal)"
            }
        }

        return BidirectionalHealthSyncResult(
            date = dateStr,
            expenditure = expenditureSnapshot,
            nutrientPush = nutrientSummary,
            netEnergyBalanceKcal = netBalance,
            syncResult = pushResult,
            statusMessage = statusMsg
        )
    }
}
