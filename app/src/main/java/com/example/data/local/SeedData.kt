package com.example.data.local

/**
 * Seed dataset synchronized 100% with the user's attached Google Sheet:
 * `PASIT HEALTH & PERFORMANCE COMMAND CENTER (2026_HealthHub)`
 * Latest Update: 23/10/2026 | Reference BMR: 1608 kcal | Target Protein: >=120g/day
 */
object SeedData {
    val initialDailySummaries = listOf(
        DailyLogEntity(
            date = "2026-10-23",
            intakeKcal = 2030,
            carbsG = 184,
            fatG = 87,
            proteinG = 89,
            sodiumMg = 3190,
            sugarG = 18.0,
            activeBurnKcal = 0,
            workoutTitle = "พัก (Rest Day)",
            weightKg = 74.5,
            weightSource = "Evolt 360",
            smmKg = 32.2,
            bodyFatPct = 22.1,
            aptCm = 11.7,
            sleepHrvMs = 84,
            sleepScore = 88
        ),
        DailyLogEntity(
            date = "2026-10-22",
            intakeKcal = 2165,
            carbsG = 205,
            fatG = 82,
            proteinG = 144,
            sodiumMg = 3650,
            sugarG = 0.0,
            activeBurnKcal = 218,
            workoutTitle = "Pilates",
            weightKg = 74.5,
            weightSource = "Evolt 360",
            smmKg = 32.2,
            bodyFatPct = 22.1,
            aptCm = 11.7,
            sleepHrvMs = 83,
            sleepScore = 87
        ),
        DailyLogEntity(
            date = "2026-10-21",
            intakeKcal = 2635,
            carbsG = 273,
            fatG = 123,
            proteinG = 115,
            sodiumMg = 4680,
            sugarG = 91.5,
            activeBurnKcal = 0,
            workoutTitle = "พัก (Rest Day)",
            weightKg = 74.6,
            weightSource = "Evolt 360",
            smmKg = 32.1,
            bodyFatPct = 22.2,
            aptCm = 11.7,
            sleepHrvMs = 80,
            sleepScore = 84
        ),
        DailyLogEntity(
            date = "2026-10-20",
            intakeKcal = 2380,
            carbsG = 262,
            fatG = 81,
            proteinG = 136,
            sodiumMg = 5071,
            sugarG = 51.0,
            activeBurnKcal = 189,
            workoutTitle = "Strength Training",
            weightKg = 74.6,
            weightSource = "Evolt 360",
            smmKg = 32.1,
            bodyFatPct = 22.2,
            aptCm = 11.7,
            sleepHrvMs = 82,
            sleepScore = 86
        ),
        DailyLogEntity(
            date = "2026-10-19",
            intakeKcal = 2490,
            carbsG = 279,
            fatG = 102,
            proteinG = 107,
            sodiumMg = 4190,
            sugarG = 46.0,
            activeBurnKcal = 514,
            workoutTitle = "Aerobics + Strength Training",
            weightKg = 74.6,
            weightSource = "Visbody 3D",
            smmKg = 32.0,
            bodyFatPct = 22.3,
            aptCm = 11.7,
            sleepHrvMs = 85,
            sleepScore = 89
        ),
        DailyLogEntity(
            date = "2026-10-18",
            intakeKcal = 2420,
            carbsG = 242,
            fatG = 113,
            proteinG = 103,
            sodiumMg = 3640,
            sugarG = 0.0,
            activeBurnKcal = 0,
            workoutTitle = "พัก (Rest Day)",
            weightKg = 74.6,
            weightSource = "Visbody 3D",
            smmKg = 26.7,
            bodyFatPct = 35.4,
            aptCm = 11.7,
            sleepHrvMs = 84,
            sleepScore = 85
        ),
        DailyLogEntity(
            date = "2026-10-17",
            intakeKcal = 3380,
            carbsG = 522,
            fatG = 98,
            proteinG = 104,
            sodiumMg = 3520,
            sugarG = 5.0,
            activeBurnKcal = 604,
            workoutTitle = "Outdoor Run",
            weightKg = 74.7,
            weightSource = "Visbody 3D",
            smmKg = 31.9,
            bodyFatPct = 22.5,
            aptCm = 11.8,
            sleepHrvMs = 81,
            sleepScore = 85
        )
    )

    val initialMeals = listOf(
        // === 23/10/2026 (Exact meals from Google Sheet 2026_HealthHub) ===
        NutritionEntryEntity(
            date = "2026-10-23",
            mealType = "มื้อว่าง",
            name = "ธัญพืชรวม KC (ไม่ใส่น้ำตาล)",
            calories = 150,
            proteinG = 5.0,
            carbsG = 26.0,
            fatG = 3.5,
            sodiumMg = 140,
            note = "หักซองน้ำตาลออกเรียบร้อย"
        ),
        NutritionEntryEntity(
            date = "2026-10-23",
            mealType = "มื้อหลัก (เช้า/เที่ยง)",
            name = "ข้าวผัดพริกแกงหมู + ไข่ดาว",
            calories = 640,
            proteinG = 25.0,
            carbsG = 53.0,
            fatG = 30.0,
            sodiumMg = 750,
            note = "ไข่ดาวทอดกรอบ"
        ),
        NutritionEntryEntity(
            date = "2026-10-23",
            mealType = "มื้อหลัก (บ่าย)",
            name = "ข้าวมันไก่ผสม (ต้ม+ทอด)",
            calories = 620,
            proteinG = 28.0,
            carbsG = 60.0,
            fatG = 25.0,
            sodiumMg = 900,
            note = "รวมน้ำจิ้มและน้ำซุป"
        ),
        NutritionEntryEntity(
            date = "2026-10-23",
            mealType = "มื้อหลัก (เย็น)",
            name = "ก๋วยเตี๋ยวรวมเนื้อต้มยำหมี่เหลือง",
            calories = 620,
            proteinG = 31.0,
            carbsG = 45.0,
            fatG = 28.0,
            sodiumMg = 1400,
            note = "รวมเกี๊ยวกรอบและเครื่องต้มยำ"
        ),

        // === 22/10/2026 (2,165 kcal | 143.5P / 204.5C / 81.5F | Na 3,650mg | Pilates 218 kcal) ===
        NutritionEntryEntity(
            date = "2026-10-22",
            mealType = "มื้อเช้า",
            name = "เวย์โปรตีนไอโซเลท 2 สกู๊ป + ข้าวโอ๊ตไม่หวาน",
            calories = 425,
            proteinG = 54.0,
            carbsG = 38.5,
            fatG = 6.5,
            sodiumMg = 320,
            note = "น้ำตาล 0g ก่อนซ้อม Pilates"
        ),
        NutritionEntryEntity(
            date = "2026-10-22",
            mealType = "มื้อกลางวัน",
            name = "สเต็กอกไก่ย่างพริกไทยดำ + ข้าวกล้อง + ไข่ต้ม 2 ฟอง",
            calories = 720,
            proteinG = 52.5,
            carbsG = 68.0,
            fatG = 26.0,
            sodiumMg = 1180,
            note = "โปรตีนสูงทะลุเป้า ฟื้นฟูกล้ามเนื้อแกนกลาง"
        ),
        NutritionEntryEntity(
            date = "2026-10-22",
            mealType = "มื้อเย็น",
            name = "เกาเหลาเนื้อตุ๋นพิเศษ + ข้าวสวย",
            calories = 680,
            proteinG = 29.0,
            carbsG = 62.0,
            fatG = 34.0,
            sodiumMg = 1650,
            note = "น้ำซุปเนื้อตุ๋นโซเดียมค่อนข้างสูง"
        ),
        NutritionEntryEntity(
            date = "2026-10-22",
            mealType = "มื้อว่าง",
            name = "กรีกโยเกิร์ตธรรมชาติ + อัลมอนด์อบ",
            calories = 340,
            proteinG = 8.0,
            carbsG = 36.0,
            fatG = 15.0,
            sodiumMg = 500,
            note = "สูตรไม่เติมน้ำตาล (Sugar 0g)"
        ),

        // === 21/10/2026 (2,635 kcal | 115P / 273C / 123F | Sugar 91.5g | Na 4,680mg) ===
        NutritionEntryEntity(
            date = "2026-10-21",
            mealType = "มื้อเช้า",
            name = "โจ๊กหมูสับใส่ไข่ลวก 2 ฟอง + ปาท่องโก๋",
            calories = 615,
            proteinG = 26.0,
            carbsG = 68.0,
            fatG = 26.0,
            sodiumMg = 1180,
            note = "มื้อเช้าวันพักซ้อม"
        ),
        NutritionEntryEntity(
            date = "2026-10-21",
            mealType = "มื้อกลางวัน",
            name = "ข้าวขาหมูเนื้อหนังพิเศษ + ไข่พะโล้",
            calories = 890,
            proteinG = 38.0,
            carbsG = 78.0,
            fatG = 46.0,
            sodiumMg = 1650,
            note = "ไขมันและโซเดียมจากน้ำพะโล้"
        ),
        NutritionEntryEntity(
            date = "2026-10-21",
            mealType = "มื้อว่าง",
            name = "ชานมไข่มุก + ขนมปังเนยสด",
            calories = 540,
            proteinG = 9.0,
            carbsG = 82.0,
            fatG = 19.0,
            sodiumMg = 450,
            note = "น้ำตาลสูง (รวมน้ำตาล 91.5g ของวัน)"
        ),
        NutritionEntryEntity(
            date = "2026-10-21",
            mealType = "มื้อเย็น",
            name = "ผัดไทยกุ้งสดห่อไข่พิเศษ",
            calories = 590,
            proteinG = 42.0,
            carbsG = 45.0,
            fatG = 32.0,
            sodiumMg = 1400,
            note = "ซอสผัดไทยโซเดียมสูง"
        ),

        // === 20/10/2026 (2,380 kcal | 136P / 261.5C / 80.8F | Sugar 51g | Na 5,071mg | Strength 189 kcal) ===
        NutritionEntryEntity(
            date = "2026-10-20",
            mealType = "มื้อเช้า",
            name = "ข้าวต้มปลาเก๋า + ไข่ลวก 2 ฟอง",
            calories = 490,
            proteinG = 36.0,
            carbsG = 54.0,
            fatG = 14.0,
            sodiumMg = 1120,
            note = "มื้อเช้าก่อนซ้อม Strength Training"
        ),
        NutritionEntryEntity(
            date = "2026-10-20",
            mealType = "มื้อกลางวัน",
            name = "ข้าวกะเพราเนื้อโคขุนสับ + ไข่ดาว + ต้มจืด",
            calories = 780,
            proteinG = 42.0,
            carbsG = 72.5,
            fatG = 34.8,
            sodiumMg = 1851,
            note = "โซเดียมสูงจากซอสผัดและน้ำซุป"
        ),
        NutritionEntryEntity(
            date = "2026-10-20",
            mealType = "มื้อว่างหลังซ้อม",
            name = "เวย์โปรตีน 1.5 สกู๊ป + กล้วยหอม + กาแฟเย็น",
            calories = 420,
            proteinG = 30.0,
            carbsG = 55.0,
            fatG = 8.0,
            sodiumMg = 300,
            note = "เติมโปรตีนหลังยกเวท"
        ),
        NutritionEntryEntity(
            date = "2026-10-20",
            mealType = "มื้อเย็น",
            name = "ส้มตำไทย + ไก่ย่างครึ่งตัว + ข้าวเหนียว",
            calories = 690,
            proteinG = 28.0,
            carbsG = 80.0,
            fatG = 24.0,
            sodiumMg = 1800,
            note = "โซเดียมจากน้ำปลาร้า/น้ำปลาส้มตำ"
        ),

        // === 19/10/2026 (2,490 kcal | 106.5P / 278.5C / 101.5F | Sugar 46g | Na 4,190mg | Aerobics+Strength 514 kcal) ===
        NutritionEntryEntity(
            date = "2026-10-19",
            mealType = "มื้อเช้า",
            name = "แซนด์วิชอกไก่ไข่ดาว + นมโปรตีนสูง",
            calories = 580,
            proteinG = 36.5,
            carbsG = 58.5,
            fatG = 21.5,
            sodiumMg = 890,
            note = "เติมพลังก่อนซ้อม Aerobics + Strength 514 kcal"
        ),
        NutritionEntryEntity(
            date = "2026-10-19",
            mealType = "มื้อกลางวัน",
            name = "ข้าวหมูแดงหมูกรอบพิเศษ + บะหมี่เกี๊ยว",
            calories = 960,
            proteinG = 38.0,
            carbsG = 112.0,
            fatG = 42.0,
            sodiumMg = 1750,
            note = "รวมน้ำราดหมูแดงและซุป"
        ),
        NutritionEntryEntity(
            date = "2026-10-19",
            mealType = "มื้อเย็น",
            name = "ชาบูน้ำดำหมูสันนอก + ผักรวม + วุ้นเส้น",
            calories = 950,
            proteinG = 32.0,
            carbsG = 108.0,
            fatG = 38.0,
            sodiumMg = 1550,
            note = "มื้อฟื้นฟูหลังซ้อมหนัก"
        ),

        // === 18/10/2026 (2,420 kcal | 103P / 242C / 113F | Sugar 0g | Na 3,640mg) ===
        NutritionEntryEntity(
            date = "2026-10-18",
            mealType = "มื้อเช้า",
            name = "ไข่กระทะทรงเครื่อง + ขนมปังปิ้ง + กาแฟดำ",
            calories = 590,
            proteinG = 26.0,
            carbsG = 52.0,
            fatG = 31.0,
            sodiumMg = 880,
            note = "ไม่ใส่น้ำตาล (Sugar 0g)"
        ),
        NutritionEntryEntity(
            date = "2026-10-18",
            mealType = "มื้อกลางวัน",
            name = "ข้าวหน้าเป็ดย่างหนังกรอบ + คะน้าน้ำมันหอย",
            calories = 920,
            proteinG = 41.0,
            carbsG = 96.0,
            fatG = 42.0,
            sodiumMg = 1420,
            note = "น้ำราดเป็ดย่างและน้ำมันหอย"
        ),
        NutritionEntryEntity(
            date = "2026-10-18",
            mealType = "มื้อเย็น",
            name = "ปลากะพงทอดน้ำปลา + ต้มยำกุ้งน้ำข้น + ข้าวสวย",
            calories = 910,
            proteinG = 36.0,
            carbsG = 94.0,
            fatG = 40.0,
            sodiumMg = 1340,
            note = "มื้อเย็นวันพักซ้อม"
        ),

        // === 17/10/2026 (3,380 kcal | 104P / 522C / 98F | Sugar 5g | Na 3,520mg | Outdoor Run 604 kcal) ===
        NutritionEntryEntity(
            date = "2026-10-17",
            mealType = "มื้อเช้า",
            name = "ข้าวต้มหมูสับ + กล้วยหอม 2 ลูก + เกลือแร่",
            calories = 780,
            proteinG = 24.0,
            carbsG = 142.0,
            fatG = 12.0,
            sodiumMg = 820,
            note = "โหลดคาร์บก่อนวิ่ง Outdoor Run 604 kcal"
        ),
        NutritionEntryEntity(
            date = "2026-10-17",
            mealType = "มื้อกลางวัน",
            name = "พาสต้าซีฟู้ดผัดขี้เมา + ข้าวผัดปูจานใหญ่",
            calories = 1350,
            proteinG = 42.0,
            carbsG = 210.0,
            fatG = 38.0,
            sodiumMg = 1450,
            note = "เติมไกลโคเจนหลังวิ่งยาว"
        ),
        NutritionEntryEntity(
            date = "2026-10-17",
            mealType = "มื้อเย็น",
            name = "ชุดเนื้อย่างยากินิกุ + ข้าวญี่ปุ่น 2 ถ้วย",
            calories = 1250,
            proteinG = 38.0,
            carbsG = 170.0,
            fatG = 48.0,
            sodiumMg = 1250,
            note = "มื้อเย็นชดเชยพลังงานหลังวิ่ง Outdoor Run"
        )
    )

    val initialGymExercises = listOf(
        // P-01: Tomorrow's Recommended Session (Pilates / Core APT + Upper Push)
        MicrocycleWorkoutEntity(
            dayCode = "P-01",
            dayName = "Tomorrow (24/10)",
            sessionTitle = "Upper Push + Pelvis APT Reset & Zone 2",
            exerciseName = "Incline Dumbbell Press",
            setsReps = "4 Sets × 8-10 Reps",
            topWeightKg = 30.0,
            targetRpe = 8.0,
            restSeconds = 90,
            completedSets = 0,
            totalSets = 4,
            isCompleted = false
        ),
        MicrocycleWorkoutEntity(
            dayCode = "P-01",
            dayName = "Tomorrow (24/10)",
            sessionTitle = "Upper Push + Pelvis APT Reset & Zone 2",
            exerciseName = "Dead Bug Core Pelvis Lock (แก้ APT 11.7cm)",
            setsReps = "3 Sets × 12 Reps/side",
            topWeightKg = 0.0,
            targetRpe = 7.5,
            restSeconds = 60,
            completedSets = 0,
            totalSets = 3,
            isCompleted = false
        ),
        MicrocycleWorkoutEntity(
            dayCode = "P-01",
            dayName = "Tomorrow (24/10)",
            sessionTitle = "Upper Push + Pelvis APT Reset & Zone 2",
            exerciseName = "Zone 2 Incline Walk / Run (เผาผลาญไขมัน)",
            setsReps = "1 Session × 35 Min",
            topWeightKg = 0.0,
            targetRpe = 7.0,
            restSeconds = 0,
            completedSets = 0,
            totalSets = 1,
            isCompleted = false
        ),
        // P-02: Upper Pull & Rear Delt
        MicrocycleWorkoutEntity(
            dayCode = "P-02",
            dayName = "Day 2",
            sessionTitle = "Upper Pull & Posture Alignment",
            exerciseName = "Wide-Grip Lat Pulldown",
            setsReps = "4 Sets × 10 Reps",
            topWeightKg = 65.0,
            targetRpe = 8.5,
            restSeconds = 90,
            completedSets = 4,
            totalSets = 4,
            isCompleted = true
        ),
        MicrocycleWorkoutEntity(
            dayCode = "P-02",
            dayName = "Day 2",
            sessionTitle = "Upper Pull & Posture Alignment",
            exerciseName = "Chest-Supported Row",
            setsReps = "3 Sets × 10 Reps",
            topWeightKg = 55.0,
            targetRpe = 8.0,
            restSeconds = 90,
            completedSets = 3,
            totalSets = 3,
            isCompleted = true
        ),
        // P-03: Lower Body & Pelvic Tilt Correction
        MicrocycleWorkoutEntity(
            dayCode = "P-03",
            dayName = "Day 3",
            sessionTitle = "Heavy Leg Day & APT Glute Drive",
            exerciseName = "Romanian Deadlift (Hamstring/Glute)",
            setsReps = "4 Sets × 8 Reps",
            topWeightKg = 100.0,
            targetRpe = 8.5,
            restSeconds = 120,
            completedSets = 4,
            totalSets = 4,
            isCompleted = true
        ),
        MicrocycleWorkoutEntity(
            dayCode = "P-03",
            dayName = "Day 3",
            sessionTitle = "Heavy Leg Day & APT Glute Drive",
            exerciseName = "Barbell Hip Thrust (Pelvis Posterior Tilt)",
            setsReps = "4 Sets × 10 Reps",
            topWeightKg = 110.0,
            targetRpe = 8.5,
            restSeconds = 90,
            completedSets = 3,
            totalSets = 4,
            isCompleted = false
        ),
        // P-04: Pilates Reformer (22/10 Session)
        MicrocycleWorkoutEntity(
            dayCode = "P-04",
            dayName = "Pilates",
            sessionTitle = "Pilates Reformer & Hip Flexor Release (218 kcal)",
            exerciseName = "90/90 Posterior Pelvic Tilt Breathing",
            setsReps = "3 Sets × 60 Sec",
            topWeightKg = 0.0,
            targetRpe = 6.5,
            restSeconds = 45,
            completedSets = 3,
            totalSets = 3,
            isCompleted = true
        ),
        // P-05: Strength Training (20/10 Session)
        MicrocycleWorkoutEntity(
            dayCode = "P-05",
            dayName = "Strength",
            sessionTitle = "Full Body Strength Training (189 kcal)",
            exerciseName = "Weighted Pull-Ups & Dumbbell Press",
            setsReps = "4 Sets × 8 Reps",
            topWeightKg = 10.0,
            targetRpe = 8.5,
            restSeconds = 90,
            completedSets = 4,
            totalSets = 4,
            isCompleted = true
        ),
        // P-06: Outdoor Run (17/10 Session)
        MicrocycleWorkoutEntity(
            dayCode = "P-06",
            dayName = "Run",
            sessionTitle = "Outdoor Endurance Run (604 kcal)",
            exerciseName = "Outdoor Run Zone 2-3 (172+ spm Cadence)",
            setsReps = "1 Session × 55 Min",
            topWeightKg = 0.0,
            targetRpe = 7.5,
            restSeconds = 0,
            completedSets = 1,
            totalSets = 1,
            isCompleted = true
        ),
        // P-07: Full Recovery & Mobility
        MicrocycleWorkoutEntity(
            dayCode = "P-07",
            dayName = "Rest",
            sessionTitle = "Rest Day & Sodium Flush Mobility",
            exerciseName = "Thoracic Spine Extension + Glute Activation",
            setsReps = "2 Sets × 10 Min",
            topWeightKg = 0.0,
            targetRpe = 5.5,
            restSeconds = 30,
            completedSets = 2,
            totalSets = 2,
            isCompleted = true
        )
    )

    val initialWorkouts = listOf(
        WorkoutEntity(
            date = "2026-10-22",
            type = "Pilates",
            title = "Pilates Core & Pelvic Tilt Reset",
            distanceKm = 0.0,
            durationMin = 50,
            activeBurnKcal = 218,
            avgHr = 118,
            elevationDPlus = 0,
            cadence = 0,
            gctMs = 0
        ),
        WorkoutEntity(
            date = "2026-10-20",
            type = "Strength Training",
            title = "Strength Training Hypertrophy",
            distanceKm = 0.0,
            durationMin = 45,
            activeBurnKcal = 189,
            avgHr = 126,
            elevationDPlus = 0,
            cadence = 0,
            gctMs = 0
        ),
        WorkoutEntity(
            date = "2026-10-19",
            type = "Aerobics + Strength",
            title = "Aerobics + Strength Training",
            distanceKm = 4.2,
            durationMin = 65,
            activeBurnKcal = 514,
            avgHr = 148,
            elevationDPlus = 40,
            cadence = 168,
            gctMs = 225
        ),
        WorkoutEntity(
            date = "2026-10-17",
            type = "Outdoor Run",
            title = "Outdoor Run Endurance Session",
            distanceKm = 9.5,
            durationMin = 58,
            activeBurnKcal = 604,
            avgHr = 154,
            elevationDPlus = 120,
            cadence = 173,
            gctMs = 218
        )
    )

    val initialBodyComp = listOf(
        BodyCompEntity(
            date = "2026-10-23",
            sourceDevice = "Evolt 360",
            weightKg = 74.5,
            bodyFatPct = 22.1,
            smmKg = 32.2,
            waterPct = 56.4,
            aptCm = 11.7,
            visceralFat = 6
        ),
        BodyCompEntity(
            date = "2026-10-18",
            sourceDevice = "Visbody 3D",
            weightKg = 74.6,
            bodyFatPct = 35.4,
            smmKg = 26.7,
            waterPct = 55.2,
            aptCm = 11.7,
            visceralFat = 7
        )
    )

    val initialSleep = listOf(
        SleepEntity(
            date = "2026-10-23",
            durationHours = 7.8,
            sleepScore = 88,
            deepSleepPct = 24,
            avgHr = 48,
            avgHrvMs = 84
        ),
        SleepEntity(
            date = "2026-10-22",
            durationHours = 7.6,
            sleepScore = 87,
            deepSleepPct = 23,
            avgHr = 49,
            avgHrvMs = 83
        )
    )
}
