package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.media.ExportProgress
import com.example.media.VideoProcessor
import com.example.model.ExportConfiguration
import com.example.model.VideoProject
import com.example.ui.components.InbuiltVideoPlayer
import com.example.ui.theme.HighQBorderSubtle
import com.example.ui.theme.HighQEnhanceGradient
import com.example.ui.theme.HighQMint
import com.example.ui.theme.HighQTextMuted
import com.example.ui.theme.HighQTextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ExportScreen(
    videoUri: Uri?,
    project: VideoProject?,
    targetResolution: String = "4K",
    onBack: () -> Unit,
    onExportComplete: (VideoProject) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedResolution by remember { mutableStateOf(targetResolution) }
    var selectedFps by remember { mutableIntStateOf(30) }
    var selectedBitrate by remember { mutableIntStateOf(if (targetResolution == "8K") 50 else 20) }
    var selectedFormat by remember { mutableStateOf("MP4") }
    var keepAudio by remember { mutableStateOf(true) }

    var isExporting by remember { mutableStateOf(false) }
    var exportProgressValue by remember { mutableIntStateOf(0) }
    var exportStageText by remember { mutableStateOf("Starting...") }
    var exportJob by remember { mutableStateOf<Job?>(null) }
    var exportedResult by remember { mutableStateOf<ExportProgress.Success?>(null) }

    var durationSec by remember { mutableIntStateOf(project?.durationSeconds ?: 45) }
    var sourceRes by remember { mutableStateOf(project?.sourceResolution ?: "1080p") }

    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            val meta = VideoProcessor.extractMetadata(context, videoUri)
            durationSec = meta.durationSeconds
            sourceRes = meta.resolutionLabel
        }
    }

    val estimatedSize = (selectedBitrate * durationSec / 8).coerceAtLeast(24)
    val durationFormatted = String.format("%02d:%02d", durationSec / 60, durationSec % 60)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isCompact = maxHeight < 700.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = if (isCompact) 16.dp else 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isCompact) 8.dp else 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(if (isCompact) 36.dp else 40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("export_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Export Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isCompact) 15.sp else 17.sp
                )

                Spacer(modifier = Modifier.size(if (isCompact) 36.dp else 40.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Output Video Preview / Live Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 160.dp else 190.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                    .background(Color.Black)
            ) {
                if (videoUri != null) {
                    InbuiltVideoPlayer(
                        videoUri = videoUri,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.VideoLibrary,
                            contentDescription = null,
                            tint = HighQMint,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                // Resolution Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xD9000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedResolution,
                        color = HighQMint,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))

            // Resolution Segmented Control (720p, 1080p, 2K, 4K, 8K)
            SettingSection(title = "Output Resolution", isCompact = isCompact) {
                SegmentedPillRow(
                    options = listOf("720p", "1080p", "2K", "4K", "8K"),
                    selectedOption = selectedResolution,
                    onOptionSelect = { selectedResolution = it },
                    isCompact = isCompact,
                    testTagPrefix = "res_opt"
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))

            // Frame Rate (FPS) Segmented Control
            SettingSection(title = "Frame Rate (FPS)", isCompact = isCompact) {
                SegmentedPillRow(
                    options = listOf("24", "30", "60"),
                    selectedOption = selectedFps.toString(),
                    onOptionSelect = { selectedFps = it.toInt() },
                    isCompact = isCompact,
                    testTagPrefix = "fps_opt"
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))

            // Bitrate (Mbps) Segmented Control
            SettingSection(title = "Bitrate (Mbps)", isCompact = isCompact) {
                SegmentedPillRow(
                    options = listOf("5", "10", "20", "50"),
                    selectedOption = selectedBitrate.toString(),
                    onOptionSelect = { selectedBitrate = it.toInt() },
                    isCompact = isCompact,
                    testTagPrefix = "bitrate_opt"
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))

            // Output Format Segmented Control
            SettingSection(title = "Output Format", isCompact = isCompact) {
                SegmentedPillRow(
                    options = listOf("MP4", "MOV", "MKV"),
                    selectedOption = selectedFormat,
                    onOptionSelect = { selectedFormat = it },
                    isCompact = isCompact,
                    testTagPrefix = "format_opt"
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 10.dp else 14.dp))

            // Keep Original Audio Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = if (isCompact) 6.dp else 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Keep Original Audio",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Switch(
                    checked = keepAudio,
                    onCheckedChange = { keepAudio = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = HighQMint,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.testTag("export_keep_audio_switch")
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 18.dp else 24.dp))

            // Export Button (Mint-to-Peach Gradient Capsule)
            Button(
                onClick = {
                    isExporting = true
                    exportProgressValue = 0
                    exportStageText = "Initializing Pipeline..."

                    val config = ExportConfiguration(
                        resolution = selectedResolution,
                        fps = selectedFps,
                        bitrateMbps = selectedBitrate,
                        format = selectedFormat,
                        keepAudio = keepAudio
                    )

                    exportJob = coroutineScope.launch {
                        VideoProcessor.exportVideo(
                            context = context,
                            sourceUri = videoUri,
                            config = config,
                            durationSeconds = durationSec,
                            videoTitle = project?.title ?: "Enhanced_Video"
                        ).collect { progress ->
                            when (progress) {
                                is ExportProgress.Processing -> {
                                    exportProgressValue = progress.percent
                                    exportStageText = progress.stage
                                }
                                is ExportProgress.Success -> {
                                    exportedResult = progress
                                    isExporting = false
                                }
                                is ExportProgress.Failure -> {
                                    isExporting = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 48.dp else 54.dp)
                    .clip(RoundedCornerShape(50))
                    .background(HighQEnhanceGradient)
                    .testTag("export_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF11141A)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FileUpload,
                        contentDescription = "Export",
                        tint = Color(0xFF11141A),
                        modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                    )
                    Text(
                        text = "Export ($selectedResolution)",
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF11141A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estimated size footer
            Text(
                text = "Estimated size: $estimatedSize MB • $durationFormatted",
                color = HighQTextMuted,
                fontSize = if (isCompact) 11.sp else 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Export In-Progress Dialog
    if (isExporting) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Rendering in $selectedResolution",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = exportStageText,
                        color = HighQTextSecondary,
                        fontSize = 12.sp
                    )

                    LinearProgressIndicator(
                        progress = { exportProgressValue / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HighQMint,
                        trackColor = Color(0x33FFFFFF)
                    )

                    Text(
                        text = "$exportProgressValue%",
                        color = HighQMint,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        exportJob?.cancel()
                        isExporting = false
                    }
                ) {
                    Text("Cancel", color = Color(0xFFFF6B6B))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Export Success Dialog
    exportedResult?.let { success ->
        AlertDialog(
            onDismissRequest = { exportedResult = null },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Success",
                    tint = HighQMint,
                    modifier = Modifier.size(42.dp)
                )
            },
            title = {
                Text(
                    text = "Export Completed!",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Your genuine $selectedResolution video is ready and saved to disk.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Output: ${success.outputFile.name}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Size: ${success.fileSizeFormatted} • Duration: ${success.durationFormatted}",
                        color = HighQTextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newProject = VideoProject(
                            id = "proj_${System.currentTimeMillis()}",
                            title = success.outputFile.name,
                            sourceUriString = videoUri?.toString(),
                            durationSeconds = durationSec,
                            sourceResolution = sourceRes,
                            targetResolution = selectedResolution,
                            thumbnailDrawableName = null,
                            thumbnailUri = videoUri?.toString(),
                            createdAtMillis = System.currentTimeMillis(),
                            isEnhanced = true,
                            enhancementType = "Upscale",
                            enhancementPreset = "Normal",
                            fps = selectedFps,
                            bitrateMbps = selectedBitrate,
                            format = selectedFormat,
                            fileSizeFormatted = success.fileSizeFormatted,
                            exportPath = success.outputFile.absolutePath
                        )
                        exportedResult = null
                        onExportComplete(newProject)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HighQMint,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Save to Library", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            shareVideo(context, success.outputFile)
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Share")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

private fun shareVideo(context: Context, file: File) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Enhanced Video"))
    } catch (ignored: Exception) {}
}

@Composable
private fun SettingSection(
    title: String,
    isCompact: Boolean,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = if (isCompact) 12.sp else 13.sp
        )
        content()
    }
}

@Composable
private fun SegmentedPillRow(
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit,
    isCompact: Boolean,
    testTagPrefix: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) HighQMint else Color.Transparent)
                    .clickable { onOptionSelect(option) }
                    .padding(vertical = if (isCompact) 7.dp else 9.dp)
                    .testTag("${testTagPrefix}_$option"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = if (isCompact) 11.sp else 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
