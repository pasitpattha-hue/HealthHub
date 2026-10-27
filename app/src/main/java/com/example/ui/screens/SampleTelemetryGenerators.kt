package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.data.model.CategoryType

object SampleTelemetryGenerators {

    fun generateSample(category: CategoryType): Bitmap {
        return when (category) {
            CategoryType.NUTRITION -> generateNutritionSample()
            CategoryType.WORKOUT -> generateWorkoutSample()
            CategoryType.BODYCOMP -> generateBodyCompSample()
            CategoryType.SLEEP -> generateSleepSample()
        }
    }

    fun generateNutritionSample(): Bitmap {
        val width = 640
        val height = 400
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Background: Dark slate kitchen / plate setup
        val bgPaint = Paint().apply { color = Color.rgb(15, 23, 42) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Plate circle
        val platePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(30, 41, 59)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f - 20f, 150f, platePaint)

        val plateRimPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(width / 2f, height / 2f - 20f, 148f, plateRimPaint)

        // Salmon portion (coral-rose)
        val salmonPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(244, 63, 94)
        }
        canvas.drawRoundRect(RectF(width / 2f - 90f, height / 2f - 80f, width / 2f + 10f, height / 2f - 10f), 16f, 16f, salmonPaint)

        // Brown rice portion (amber)
        val ricePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(245, 158, 11)
        }
        canvas.drawCircle(width / 2f + 50f, height / 2f - 30f, 50f, ricePaint)

        // Greens portion (emerald)
        val greensPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(16, 185, 129)
        }
        canvas.drawCircle(width / 2f - 40f, height / 2f + 40f, 45f, greensPaint)

        // Text banner overlay
        val bannerPaint = Paint().apply {
            color = Color.argb(220, 11, 15, 23)
        }
        canvas.drawRect(0f, height - 90f, width.toFloat(), height.toFloat(), bannerPaint)

        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(241, 245, 249)
            textSize = 24f
            isFakeBoldText = true
        }
        canvas.drawText("🥗 Grilled Salmon Brown Rice Bowl", 24f, height - 52f, titlePaint)

        val subPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            textSize = 18f
        }
        canvas.drawText("540 kcal  •  Protein 42g  •  Carbs 48g  •  Fat 16g  •  Sodium 520mg", 24f, height - 20f, subPaint)
        bmp.setPixel(width / 2, height / 2, Color.rgb(244, 114, 94))

        return bmp
    }

    fun generateThaiChickenRiceSample(): Bitmap {
        val width = 640
        val height = 400
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val bgPaint = Paint().apply { color = Color.rgb(18, 26, 44) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Plate circle
        val platePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(34, 47, 68)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f - 20f, 150f, platePaint)

        // Warm golden chicken & jasmine rice center (R > 200, G > 180)
        val chickenRicePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(245, 208, 125)
        }
        canvas.drawCircle(width / 2f, height / 2f, 75f, chickenRicePaint)

        // Cucumber slices
        val cucumberPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(52, 211, 153)
        }
        canvas.drawCircle(width / 2f - 85f, height / 2f - 30f, 28f, cucumberPaint)

        val bannerPaint = Paint().apply { color = Color.argb(225, 11, 15, 23) }
        canvas.drawRect(0f, height - 90f, width.toFloat(), height.toFloat(), bannerPaint)

        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(241, 245, 249)
            textSize = 23f
            isFakeBoldText = true
        }
        canvas.drawText("🍗 ข้าวมันไก่ต้มลีน (ไม่เอาหนัง) + น้ำซุปฟัก", 24f, height - 52f, titlePaint)

        val subPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(251, 191, 36)
            textSize = 18f
        }
        canvas.drawText("530 kcal  •  Protein 34g  •  Carbs 62g  •  Fat 15g  •  Sodium 780mg", 24f, height - 20f, subPaint)
        bmp.setPixel(width / 2, height / 2, Color.rgb(245, 208, 125))

        return bmp
    }

    fun generateThaiKapraoChickenSample(): Bitmap {
        val width = 640
        val height = 400
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val bgPaint = Paint().apply { color = Color.rgb(15, 23, 42) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val platePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(30, 41, 59)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f - 20f, 150f, platePaint)

        // Holy basil & lean chicken center (G > 140, R < 120)
        val basilPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(34, 197, 94)
        }
        canvas.drawCircle(width / 2f, height / 2f, 75f, basilPaint)

        val bannerPaint = Paint().apply { color = Color.argb(225, 11, 15, 23) }
        canvas.drawRect(0f, height - 90f, width.toFloat(), height.toFloat(), bannerPaint)

        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(241, 245, 249)
            textSize = 23f
            isFakeBoldText = true
        }
        canvas.drawText("🍳 ข้าวกะเพราอกไก่สับลีน + ไข่ดาวน้ำ", 24f, height - 52f, titlePaint)

        val subPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(16, 185, 129)
            textSize = 18f
        }
        canvas.drawText("490 kcal  •  Protein 38g  •  Carbs 50g  •  Fat 14g  •  Sodium 690mg", 24f, height - 20f, subPaint)
        bmp.setPixel(width / 2, height / 2, Color.rgb(34, 197, 94))

        return bmp
    }

    fun generateWorkoutSample(): Bitmap {
        val width = 640
        val height = 400
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Background
        val bgPaint = Paint().apply { color = Color.rgb(11, 15, 23) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Watch bezel circle
        val bezelPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(22, 31, 51)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f, 170f, bezelPaint)

        val bezelBorder = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawCircle(width / 2f, height / 2f, 168f, bezelBorder)

        // Title
        val headPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(148, 163, 184)
            textSize = 18f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("HUAWEI WATCH 4 PRO • TRAIL RUN", width / 2f, height / 2f - 90f, headPaint)

        // Large distance
        val distPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(255, 255, 255)
            textSize = 52f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("10.20 km", width / 2f, height / 2f - 25f, distPaint)

        // Duration & Pace
        val metricsPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("56:12  |  Pace 5'30\"/km", width / 2f, height / 2f + 15f, metricsPaint)

        // Burn & HR
        val hrPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(244, 63, 94)
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        canvas.drawText("🔥 590 kcal  •  ❤️ 152 bpm avg  •  ⛰️ 180m D+", width / 2f, height / 2f + 60f, hrPaint)

        val cadencePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(16, 185, 129)
            textSize = 16f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Cadence 172 spm  •  GCT 220 ms", width / 2f, height / 2f + 95f, cadencePaint)

        return bmp
    }

    fun generateBodyCompSample(): Bitmap {
        val width = 640
        val height = 400
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Dark background
        val bgPaint = Paint().apply { color = Color.rgb(15, 23, 42) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Card container
        val cardPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(26, 38, 64)
        }
        canvas.drawRoundRect(RectF(30f, 20f, width - 30f, height - 20f), 24f, 24f, cardPaint)

        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(RectF(30f, 20f, width - 30f, height - 20f), 24f, 24f, borderPaint)

        // Header
        val headerPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            textSize = 22f
            isFakeBoldText = true
        }
        canvas.drawText("VISBODY 3D • BIOMECHANICAL & BODY COMP", 50f, 65f, headerPaint)

        // Metrics grid
        val valPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 34f
            isFakeBoldText = true
        }
        val labelPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(148, 163, 184)
            textSize = 16f
        }

        // Weight
        canvas.drawText("68.4 kg", 60f, 140f, valPaint)
        canvas.drawText("Target: 65.0 kg", 60f, 170f, labelPaint)

        // Body Fat %
        canvas.drawText("13.8 %", 260f, 140f, valPaint)
        canvas.drawText("Target: 15.0 %", 260f, 170f, labelPaint)

        // SMM
        canvas.drawText("32.8 kg", 440f, 140f, valPaint)
        canvas.drawText("Skeletal Muscle", 440f, 170f, labelPaint)

        // Posture / Anterior Pelvic Tilt
        val aptHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(244, 63, 94)
            textSize = 26f
            isFakeBoldText = true
        }
        canvas.drawText("⚠️ Anterior Pelvic Tilt (APT): 11.7 cm", 60f, 245f, aptHeaderPaint)

        val aptSubPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(226, 232, 240)
            textSize = 18f
        }
        canvas.drawText("Optimal target < 5.0 cm. Glute/Core posterior tilt protocol required.", 60f, 280f, aptSubPaint)
        canvas.drawText("Water: 56.4%  •  Visceral Fat: Level 4", 60f, 330f, labelPaint)

        return bmp
    }

    fun generateSleepSample(): Bitmap {
        val width = 640
        val height = 400
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Background
        val bgPaint = Paint().apply { color = Color.rgb(10, 15, 30) }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Card container
        val cardPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(20, 29, 52)
        }
        canvas.drawRoundRect(RectF(30f, 20f, width - 30f, height - 20f), 24f, 24f, cardPaint)

        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(168, 85, 247)
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(RectF(30f, 20f, width - 30f, height - 20f), 24f, 24f, borderPaint)

        // Header
        val headerPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(192, 132, 252)
            textSize = 22f
            isFakeBoldText = true
        }
        canvas.drawText("HUAWEI TRUSLEEP 3.0 • RECOVERY REPORT", 50f, 65f, headerPaint)

        val scorePaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(52, 211, 153)
            textSize = 48f
            isFakeBoldText = true
        }
        canvas.drawText("88", 60f, 150f, scorePaint)

        val scoreLabel = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(226, 232, 240)
            textSize = 20f
            isFakeBoldText = true
        }
        canvas.drawText("Sleep Score (Excellent)", 135f, 130f, scoreLabel)

        val descLabel = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(148, 163, 184)
            textSize = 16f
        }
        canvas.drawText("Duration: 7 hrs 48 mins  •  Deep Sleep: 24%", 135f, 155f, descLabel)

        // HRV & HR
        val hrvPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(56, 189, 248)
            textSize = 30f
            isFakeBoldText = true
        }
        canvas.drawText("HRV: 84 ms", 60f, 235f, hrvPaint)
        canvas.drawText("Resting Heart Rate: 48 bpm", 320f, 235f, hrvPaint)

        val recoveryPaint = Paint().apply {
            isAntiAlias = true
            color = Color.rgb(251, 191, 36)
            textSize = 18f
        }
        canvas.drawText("⚡ Recovery Readiness: High Parasympathetic Tone (Optimal for Hard Workout)", 60f, 290f, recoveryPaint)
        canvas.drawText("Awakenings: 1  •  Breathing Quality: 95/100", 60f, 330f, descLabel)

        return bmp
    }
}
