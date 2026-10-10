package com.example.model

data class VideoProject(
    val id: String,
    val title: String,
    val sourceUriString: String? = null,
    val durationSeconds: Int = 45,
    val sourceResolution: String = "1080p",
    val targetResolution: String = "4K",
    val thumbnailDrawableName: String? = null,
    val thumbnailUri: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isEnhanced: Boolean = true,
    val enhancementType: String = "Upscale", // Upscale, Stabilize, Denoise, Color
    val enhancementPreset: String = "Normal", // Normal, High, AI Max
    val fps: Int = 30,
    val bitrateMbps: Int = 20,
    val format: String = "MP4",
    val fileSizeFormatted: String = "248 MB",
    val exportPath: String? = null
) {
    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}

enum class EnhancementTool(
    val displayName: String,
    val iconDesc: String,
    val subtitle: String
) {
    UPSCALE("Upscale", "4K", "Higher Resolution"),
    STABILIZE("Stabilize", "Smooth", "Smooth Footage"),
    DENOISE("Denoise", "Clean", "Clean & Clear"),
    COLOR("Color Fix", "Vibrant", "Vibrant Colors")
}

enum class EnhancementPreset(val label: String, val description: String) {
    NORMAL("Normal", "Balanced speed & clarity"),
    HIGH("High", "Deep detail synthesis"),
    AI_MAX("AI Max", "Ultra neural reconstruction")
}

data class ExportConfiguration(
    val resolution: String = "4K",
    val fps: Int = 30,
    val bitrateMbps: Int = 20,
    val format: String = "MP4",
    val keepAudio: Boolean = true
) {
    fun calculateEstimatedSize(durationSeconds: Int): String {
        val multiplier = if (resolution == "8K") 1.8 else if (resolution == "4K") 1.2 else 1.0
        val sizeMb = (bitrateMbps.toDouble() * durationSeconds * multiplier) / 8.0
        val clampedMb = if (sizeMb < 10) 24 else sizeMb.toInt()
        return "$clampedMb MB"
    }
}
