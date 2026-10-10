package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.VideoProject

@Entity(tableName = "video_projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceUriString: String?,
    val durationSeconds: Int,
    val sourceResolution: String,
    val targetResolution: String,
    val thumbnailDrawableName: String?,
    val thumbnailUri: String?,
    val createdAtMillis: Long,
    val isEnhanced: Boolean,
    val enhancementType: String,
    val enhancementPreset: String,
    val fps: Int,
    val bitrateMbps: Int,
    val format: String,
    val fileSizeFormatted: String,
    val exportPath: String?
) {
    fun toDomain(): VideoProject {
        return VideoProject(
            id = id,
            title = title,
            sourceUriString = sourceUriString,
            durationSeconds = durationSeconds,
            sourceResolution = sourceResolution,
            targetResolution = targetResolution,
            thumbnailDrawableName = thumbnailDrawableName,
            thumbnailUri = thumbnailUri,
            createdAtMillis = createdAtMillis,
            isEnhanced = isEnhanced,
            enhancementType = enhancementType,
            enhancementPreset = enhancementPreset,
            fps = fps,
            bitrateMbps = bitrateMbps,
            format = format,
            fileSizeFormatted = fileSizeFormatted,
            exportPath = exportPath
        )
    }

    companion object {
        fun fromDomain(project: VideoProject): ProjectEntity {
            return ProjectEntity(
                id = project.id,
                title = project.title,
                sourceUriString = project.sourceUriString,
                durationSeconds = project.durationSeconds,
                sourceResolution = project.sourceResolution,
                targetResolution = project.targetResolution,
                thumbnailDrawableName = project.thumbnailDrawableName,
                thumbnailUri = project.thumbnailUri,
                createdAtMillis = project.createdAtMillis,
                isEnhanced = project.isEnhanced,
                enhancementType = project.enhancementType,
                enhancementPreset = project.enhancementPreset,
                fps = project.fps,
                bitrateMbps = project.bitrateMbps,
                format = project.format,
                fileSizeFormatted = project.fileSizeFormatted,
                exportPath = project.exportPath
            )
        }
    }
}
