package com.example.data.local

import com.example.data.model.HealthConstants

object SeedData {
    val initialDailySummaries = listOf(
        DailySummaryEntity(
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
            sleepScore = 84
        ),
        DailySummaryEntity(
            date = "2026-10-17",
            intakeKcal = 3380,
            carbsG = 522,
            fatG = 98,
            proteinG = 104,
            sodiumMg = 3520,
            activeBurnKcal = 604,
            workoutTitle = "Outdoor Run",
            weightKg = 74.8,
            weightSource = "Huawei Scale",
            smmKg = 26.6,
            bodyFatPct = 35.6,
            aptCm = 11.7,
            sleepHrvMs = 78,
            sleepScore = 81
        ),
        DailySummaryEntity(
            date = "2026-10-16",
            intakeKcal = 2740,
            carbsG = 281,
            fatG = 129,
            proteinG = 118,
            sodiumMg = 3640,
            activeBurnKcal = 1461,
            workoutTitle = "Trail / Outdoor Run",
            weightKg = 74.7,
            weightSource = "Huawei Scale",
            smmKg = 26.6,
            bodyFatPct = 35.5,
            aptCm = 11.8,
            sleepHrvMs = 75,
            sleepScore = 79
        ),
        DailySummaryEntity(
            date = "2026-10-15",
            intakeKcal = 2670,
            carbsG = 323,
            fatG = 93,
            proteinG = 123,
            sodiumMg = 4205,
            activeBurnKcal = 428,
            workoutTitle = "Outdoor run + Pilates",
            weightKg = 74.9,
            weightSource = "Visbody 3D",
            smmKg = 26.8,
            bodyFatPct = 35.7,
            aptCm = 11.8,
            sleepHrvMs = 91,
            sleepScore = 90
        ),
        DailySummaryEntity(
            date = "2026-10-08",
            intakeKcal = 3025,
            carbsG = 319,
            fatG = 119,
            proteinG = 150,
            sodiumMg = 4085,
            activeBurnKcal = 773,
            workoutTitle = "HIIT + Yoga",
            weightKg = 75.1,
            weightSource = "Huawei Scale",
            smmKg = 26.5,
            bodyFatPct = 35.9,
            aptCm = 12.0,
            sleepHrvMs = 82,
            sleepScore = 85
        ),
        DailySummaryEntity(
            date = "2026-10-07",
            intakeKcal = 2760,
            carbsG = 336,
            fatG = 110,
            proteinG = 105,
            sodiumMg = 4895,
            activeBurnKcal = 691,
            workoutTitle = "Trail / Outdoor Run",
            weightKg = 75.3,
            weightSource = "Visbody 3D",
            smmKg = 26.5,
            bodyFatPct = 36.1,
            aptCm = 12.1,
            sleepHrvMs = 74,
            sleepScore = 78
        ),
        DailySummaryEntity(
            date = "2026-10-06",
            intakeKcal = 2495,
            carbsG = 238,
            fatG = 94,
            proteinG = 157,
            sodiumMg = 3720,
            activeBurnKcal = 370,
            workoutTitle = "Strength",
            weightKg = 75.4,
            weightSource = "Evolt 360",
            smmKg = 26.4,
            bodyFatPct = 36.2,
            aptCm = 12.2,
            sleepHrvMs = 80,
            sleepScore = 82
        )
    )

    val initialMeals = listOf(
        MealEntity(
            date = "2026-10-18",
            time = "08:30",
            mealType = "อาหารจานหลัก",
            name = "ข้าวกะเพราไก่ + ไข่ดาว",
            calories = 650,
            proteinG = 32.0,
            carbsG = 68.0,
            fatG = 27.0,
            sodiumMg = 950
        ),
        MealEntity(
            date = "2026-10-18",
            time = "12:30",
            mealType = "อาหารจานหลัก",
            name = "ข้าวกะเพราหมูสับ + ไข่ดาว",
            calories = 730,
            proteinG = 28.0,
            carbsG = 68.0,
            fatG = 37.0,
            sodiumMg = 1100
        ),
        MealEntity(
            date = "2026-10-18",
            time = "15:45",
            mealType = "ของว่าง",
            name = "ลูกชิ้นทอด 4 ไม้ (แดง 2, หมู 2 ราดน้ำจิ้ม)",
            calories = 360,
            proteinG = 14.0,
            carbsG = 36.0,
            fatG = 18.0,
            sodiumMg = 650
        ),
        MealEntity(
            date = "2026-10-18",
            time = "16:00",
            mealType = "เครื่องดื่ม",
            name = "โค้กไม่มีน้ำตาล (Coke Zero)",
            calories = 0,
            proteinG = 0.0,
            carbsG = 0.0,
            fatG = 0.0,
            sodiumMg = 40
        ),
        MealEntity(
            date = "2026-10-18",
            time = "19:15",
            mealType = "อาหารจานหลัก",
            name = "ข้าวราดพริกแกงไก่ถั่วฝักยาว + ไข่เจียว",
            calories = 680,
            proteinG = 29.0,
            carbsG = 70.0,
            fatG = 31.0,
            sodiumMg = 900
        )
    )

    val initialWorkouts = listOf(
        WorkoutEntity(
            date = "2026-10-18",
            title = "พัก (Active Recovery)",
            category = "Rest",
            activeKcal = 0,
            durationMin = 0,
            distanceKm = 0.0,
            avgHr = 65,
            elevationDPlus = 0,
            cadence = 0,
            gctMs = 0,
            notes = "พักเต็มวัน ฟื้นฟูกล้ามเนื้อและข้อต่อ"
        ),
        WorkoutEntity(
            date = "2026-10-17",
            title = "Outdoor Run (Bang Phra)",
            category = "Outdoor Run",
            activeKcal = 604,
            durationMin = 58,
            distanceKm = 9.8,
            avgHr = 152,
            elevationDPlus = 140,
            cadence = 172,
            gctMs = 218,
            notes = "Outdoor Run วิ่งคุมโซน แซนดัลคาร์บอนกระชับดี"
        ),
        WorkoutEntity(
            date = "2026-10-16",
            title = "Trail / Outdoor Run (Khao Chalak)",
            category = "Trail Run",
            activeKcal = 1461,
            durationMin = 135,
            distanceKm = 18.6,
            avgHr = 158,
            elevationDPlus = 680,
            cadence = 166,
            gctMs = 224,
            notes = "Trail เขาฉลากทางเทคนิค เผาผลาญเต็มเหนี่ยว 1461 kcal ไม่มีอาการ Shin Splints"
        ),
        WorkoutEntity(
            date = "2026-10-15",
            title = "Outdoor run + Pilates",
            category = "Pilates & Run",
            activeKcal = 428,
            durationMin = 65,
            distanceKm = 5.2,
            avgHr = 124,
            elevationDPlus = 45,
            cadence = 168,
            gctMs = 216,
            notes = "Reformer Pilates จัดแนวกระดูกเชิงกราน APT ลดเหลือ 11.7cm"
        ),
        WorkoutEntity(
            date = "2026-10-08",
            title = "HIIT + Yoga",
            category = "HIIT",
            activeKcal = 773,
            durationMin = 75,
            distanceKm = 0.0,
            avgHr = 148,
            elevationDPlus = 0,
            cadence = 0,
            gctMs = 0,
            notes = "HIIT interval training + ยืดเหยียดโยคะ"
        ),
        WorkoutEntity(
            date = "2026-10-07",
            title = "Trail / Outdoor Run",
            category = "Trail Run",
            activeKcal = 691,
            durationMin = 68,
            distanceKm = 11.2,
            avgHr = 155,
            elevationDPlus = 320,
            cadence = 170,
            gctMs = 220,
            notes = "ซ้อมเทรลไต่ความชัน"
        ),
        WorkoutEntity(
            date = "2026-10-06",
            title = "Strength (Leg Day & Tibialis)",
            category = "Strength",
            activeKcal = 370,
            durationMin = 50,
            distanceKm = 0.0,
            avgHr = 132,
            elevationDPlus = 0,
            cadence = 0,
            gctMs = 0,
            notes = "สควอทและฝึก Tibialis ป้องกันหน้าแข้ง"
        )
    )

    val initialBodyComp = listOf(
        BodyCompEntity(
            date = "2026-10-18",
            source = "Visbody 3D",
            weightKg = 74.6,
            bodyFatPct = 35.4,
            smmKg = 26.7,
            waterPct = 52.4,
            bmrKcal = 1608,
            visceralFat = 7,
            aptCm = 11.7,
            shoulderImbalanceDeg = 1.2
        ),
        BodyCompEntity(
            date = "2026-10-17",
            source = "Huawei Scale",
            weightKg = 74.8,
            bodyFatPct = 35.6,
            smmKg = 26.6,
            waterPct = 52.3,
            bmrKcal = 1608,
            visceralFat = 7,
            aptCm = 11.7,
            shoulderImbalanceDeg = 1.3
        ),
        BodyCompEntity(
            date = "2026-10-16",
            source = "Huawei Scale",
            weightKg = 74.7,
            bodyFatPct = 35.5,
            smmKg = 26.6,
            waterPct = 52.5,
            bmrKcal = 1608,
            visceralFat = 7,
            aptCm = 11.8,
            shoulderImbalanceDeg = 1.3
        ),
        BodyCompEntity(
            date = "2026-10-15",
            source = "Visbody 3D",
            weightKg = 74.9,
            bodyFatPct = 35.7,
            smmKg = 26.8,
            waterPct = 52.6,
            bmrKcal = 1608,
            visceralFat = 7,
            aptCm = 11.8,
            shoulderImbalanceDeg = 1.4
        ),
        BodyCompEntity(
            date = "2026-10-08",
            source = "Evolt 360",
            weightKg = 75.1,
            bodyFatPct = 35.9,
            smmKg = 26.5,
            waterPct = 52.1,
            bmrKcal = 1608,
            visceralFat = 8,
            aptCm = 12.0,
            shoulderImbalanceDeg = 1.5
        ),
        BodyCompEntity(
            date = "2026-10-07",
            source = "Visbody 3D",
            weightKg = 75.3,
            bodyFatPct = 36.1,
            smmKg = 26.5,
            waterPct = 52.0,
            bmrKcal = 1608,
            visceralFat = 8,
            aptCm = 12.1,
            shoulderImbalanceDeg = 1.6
        ),
        BodyCompEntity(
            date = "2026-10-06",
            source = "Evolt 360",
            weightKg = 75.4,
            bodyFatPct = 36.2,
            smmKg = 26.4,
            waterPct = 51.9,
            bmrKcal = 1608,
            visceralFat = 8,
            aptCm = 12.2,
            shoulderImbalanceDeg = 1.6
        )
    )

    val initialSleep = listOf(
        SleepEntity(
            date = "2026-10-18",
            durationHours = 7.6,
            score = 84,
            deepSleepPct = 22,
            avgHr = 49,
            avgHrvMs = 84
        ),
        SleepEntity(
            date = "2026-10-17",
            durationHours = 7.2,
            score = 81,
            deepSleepPct = 19,
            avgHr = 52,
            avgHrvMs = 78
        ),
        SleepEntity(
            date = "2026-10-16",
            durationHours = 6.8,
            score = 79,
            deepSleepPct = 18,
            avgHr = 54,
            avgHrvMs = 75
        ),
        SleepEntity(
            date = "2026-10-15",
            durationHours = 8.2,
            score = 90,
            deepSleepPct = 26,
            avgHr = 47,
            avgHrvMs = 91
        ),
        SleepEntity(
            date = "2026-10-08",
            durationHours = 7.5,
            score = 85,
            deepSleepPct = 23,
            avgHr = 50,
            avgHrvMs = 82
        ),
        SleepEntity(
            date = "2026-10-07",
            durationHours = 7.1,
            score = 78,
            deepSleepPct = 17,
            avgHr = 53,
            avgHrvMs = 74
        ),
        SleepEntity(
            date = "2026-10-06",
            durationHours = 7.4,
            score = 82,
            deepSleepPct = 20,
            avgHr = 51,
            avgHrvMs = 80
        )
    )

    val initialGymExercises = listOf(
        // P-01 Sunday
        GymExerciseEntity(
            dayCode = "P-01",
            dayName = "Sunday",
            sessionTitle = "Active Recovery & Mobility",
            exerciseName = "Hip Flexor Couch Stretch & Glute Activation",
            warmUpSets = "Bodyweight 2 x 60s per side",
            workingSets = "3 x 90s isometric hold",
            topWeightKg = 0.0,
            completedSets = 3,
            totalSets = 3,
            coachCue = "Tuck pelvis under (posterior pelvic tilt) to unload lumbar spine.",
            isCompleted = true
        ),
        GymExerciseEntity(
            dayCode = "P-01",
            dayName = "Sunday",
            sessionTitle = "Active Recovery & Mobility",
            exerciseName = "Thoracic Spine Foam Rolling & 90/90 Breathing",
            warmUpSets = "1 x 5 min flow",
            workingSets = "3 sets x 10 deep diaphragmatic breaths",
            topWeightKg = 0.0,
            completedSets = 3,
            totalSets = 3,
            coachCue = "Full exhalation down to empty ribcage, expanding lower back.",
            isCompleted = true
        ),

        // P-02 Monday
        GymExerciseEntity(
            dayCode = "P-02",
            dayName = "Monday",
            sessionTitle = "Reformer Pilates for APT",
            exerciseName = "Footwork on Reformer (V-Position & Parallel)",
            warmUpSets = "2 Red Springs x 15 reps",
            workingSets = "3 Red + 1 Blue Springs x 20 reps",
            topWeightKg = 0.0,
            completedSets = 4,
            totalSets = 4,
            coachCue = "Keep sacrum heavy on carriage. Avoid overarching lower back.",
            isCompleted = true
        ),
        GymExerciseEntity(
            dayCode = "P-02",
            dayName = "Monday",
            sessionTitle = "Reformer Pilates for APT",
            exerciseName = "Pelvic Curl & Hamstring Pullbacks",
            warmUpSets = "2 Red Springs x 10 reps",
            workingSets = "2 Red + 1 Green Springs x 12 reps (4 sets)",
            topWeightKg = 0.0,
            completedSets = 4,
            totalSets = 4,
            coachCue = "Initiate movement by scooping abs and driving from hamstrings/glutes.",
            isCompleted = true
        ),

        // P-03 Tuesday
        GymExerciseEntity(
            dayCode = "P-03",
            dayName = "Tuesday",
            sessionTitle = "Heavy Leg Day & Shin Protection",
            exerciseName = "Barbell Back Squat (Warm-up to Top Set)",
            warmUpSets = "11kg x 10, 41kg x 8, 61kg x 5, 71kg x 3",
            workingSets = "Top Set: 121kg x 5 reps (RPE 8.5) + Back-off 95kg 3x8",
            topWeightKg = 121.0,
            completedSets = 5,
            totalSets = 5,
            coachCue = "Root tripod foot into floor, brace 360° core, break hips and knees together.",
            isCompleted = true
        ),
        GymExerciseEntity(
            dayCode = "P-03",
            dayName = "Tuesday",
            sessionTitle = "Heavy Leg Day & Shin Protection",
            exerciseName = "Heavy 45° Leg Press (Includes 75.2kg Sled)",
            warmUpSets = "175.2kg x 12, 275.2kg x 10",
            workingSets = "Top Set: 375.2kg x 8 reps (300kg plates + 75.2kg sled) x 3 sets",
            topWeightKg = 375.2,
            completedSets = 3,
            totalSets = 3,
            coachCue = "Do not lock knees at apex. Keep tailbone glued to pad.",
            isCompleted = true
        ),
        GymExerciseEntity(
            dayCode = "P-03",
            dayName = "Tuesday",
            sessionTitle = "Heavy Leg Day & Shin Protection",
            exerciseName = "Tibialis Anterior DB Wall Raises (Shin Splint Prehab)",
            warmUpSets = "Bodyweight x 25 reps",
            workingSets = "Dumbbell 12kg strapped x 20 reps x 4 sets",
            topWeightKg = 12.0,
            completedSets = 4,
            totalSets = 4,
            coachCue = "Dorsiflex toes all the way up, hold 1s at top for trail running deceleration resilience.",
            isCompleted = true
        ),
        GymExerciseEntity(
            dayCode = "P-03",
            dayName = "Tuesday",
            sessionTitle = "Heavy Leg Day & Shin Protection",
            exerciseName = "Single-Leg Eccentric Calf Raises (Soleus & Achilles)",
            warmUpSets = "Bodyweight x 15 reps",
            workingSets = "24kg Kettlebell x 15 reps each leg (3s slow eccentric) x 3 sets",
            topWeightKg = 24.0,
            completedSets = 3,
            totalSets = 3,
            coachCue = "Drop heel deep past the edge of the block for Achilles elasticity.",
            isCompleted = true
        ),

        // P-04 Wednesday
        GymExerciseEntity(
            dayCode = "P-04",
            dayName = "Wednesday",
            sessionTitle = "Upper Body Hypertrophy @ Jetts",
            exerciseName = "Incline Dumbbell Chest Press",
            warmUpSets = "20kg x 12, 28kg x 8",
            workingSets = "36kg Dumbbells x 8 reps x 4 sets",
            topWeightKg = 36.0,
            completedSets = 4,
            totalSets = 4,
            coachCue = "Retract scapulae, 30° incline bench, control descent 2s.",
            isCompleted = true
        ),
        GymExerciseEntity(
            dayCode = "P-04",
            dayName = "Wednesday",
            sessionTitle = "Upper Body Hypertrophy @ Jetts",
            exerciseName = "Seated Neutral-Grip Cable Rows",
            warmUpSets = "50kg x 12",
            workingSets = "75kg x 10 reps x 4 sets",
            topWeightKg = 75.0,
            completedSets = 4,
            totalSets = 4,
            coachCue = "Pull elbows to waist, squeeze lower lats, no torso swinging.",
            isCompleted = true
        ),

        // P-05 Thursday
        GymExerciseEntity(
            dayCode = "P-05",
            dayName = "Thursday",
            sessionTitle = "Speed / Tempo Intervals",
            exerciseName = "400m Track Repeats @ 3:55/km Pace",
            warmUpSets = "1.5km Jog + Dynamic Drills (High Knees, A-Skips)",
            workingSets = "8 x 400m repeats with 90s slow jog recovery",
            topWeightKg = 0.0,
            completedSets = 8,
            totalSets = 8,
            coachCue = "Maintain cadence at 180 spm, relaxed shoulders, midfoot ground strike.",
            isCompleted = false
        ),

        // P-06 Friday
        GymExerciseEntity(
            dayCode = "P-06",
            dayName = "Friday",
            sessionTitle = "Zone 2 Easy Aerobic Flush Run",
            exerciseName = "Easy Road/Park Continuous Run",
            warmUpSets = "500m Walk & Ankle Rotations",
            workingSets = "6.0 km @ 5:50/km pace (HR cap: 138 bpm)",
            topWeightKg = 0.0,
            completedSets = 1,
            totalSets = 1,
            coachCue = "Nasal breathing test: should be able to hold conversation without gasping.",
            isCompleted = false
        ),

        // P-07 Saturday
        GymExerciseEntity(
            dayCode = "P-07",
            dayName = "Saturday",
            sessionTitle = "Long Trail Run Bang Phra / Khao Chalak",
            exerciseName = "Khao Chalak Elevation Loop",
            warmUpSets = "Glute Bands + Ankle Stretches (10 min)",
            workingSets = "16.0 km trail loop (650m D+ elevation climb)",
            topWeightKg = 0.0,
            completedSets = 1,
            totalSets = 1,
            coachCue = "Short strides on technical descents, hands on knees for steep uphill power hike.",
            isCompleted = false
        )
    )

    val initialSettings = listOf(
        SettingEntity("gas_webhook_url", HealthConstants.DEFAULT_GAS_URL),
        SettingEntity("auto_sync_enabled", "true"),
        SettingEntity("theme_mode", "crystal_dark")
    )
}
