package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.VideoProcessor
import com.example.model.EnhancementPreset
import com.example.model.EnhancementTool
import com.example.model.VideoProject
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.InbuiltVideoPlayer
import com.example.ui.theme.HighQBorderSubtle
import com.example.ui.theme.HighQMint
import com.example.ui.theme.HighQMintAquaGradient
import com.example.ui.theme.HighQPeach
import com.example.ui.theme.HighQTextMuted
import com.example.ui.theme.HighQTextSecondary

@Composable
fun EditorScreen(
    videoUri: Uri?,
    project: VideoProject?,
    initialTool: EnhancementTool = EnhancementTool.UPSCALE,
    onBack: () -> Unit,
    onStartEnhancing: (EnhancementTool, EnhancementPreset, Float, String) -> Unit
) {
    val context = LocalContext.current

    var selectedTool by remember { mutableStateOf(initialTool) }
    var selectedPreset by remember { mutableStateOf(EnhancementPreset.NORMAL) }
    var selectedTargetRes by remember { mutableStateOf("4K") }
    var enhancementStrength by remember { mutableFloatStateOf(0.85f) }
    var showAdvancedSliders by remember { mutableStateOf(false) }
    var showCompareMode by remember { mutableStateOf(false) }

    // Real extracted video metadata
    var extractedDuration by remember { mutableIntStateOf(project?.durationSeconds ?: 45) }
    var extractedSourceRes by remember { mutableStateOf(project?.sourceResolution ?: "1080p") }
    var isFullscreen by remember { mutableStateOf(false) }

    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            val info = VideoProcessor.extractMetadata(context, videoUri)
            extractedDuration = info.durationSeconds
            extractedSourceRes = info.resolutionLabel
        }
    }

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
                        .testTag("editor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Enhance Video",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isCompact) 15.sp else 17.sp
                    )
                    Text(
                        text = "$extractedSourceRes ➔ $selectedTargetRes",
                        fontSize = 11.sp,
                        color = HighQMint,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Toggle Compare Mode
                    IconButton(
                        onClick = { showCompareMode = !showCompareMode },
                        modifier = Modifier
                            .size(if (isCompact) 36.dp else 40.dp)
                            .clip(CircleShape)
                            .background(if (showCompareMode) HighQMint.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("editor_compare_button")
                    ) {
                        Icon(
                            imageVector = if (showCompareMode) Icons.Filled.PlayCircle else Icons.Filled.Compare,
                            contentDescription = "Compare",
                            tint = if (showCompareMode) HighQMint else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Tuning sliders toggle
                    IconButton(
                        onClick = { showAdvancedSliders = !showAdvancedSliders },
                        modifier = Modifier
                            .size(if (isCompact) 36.dp else 40.dp)
                            .clip(CircleShape)
                            .background(if (showAdvancedSliders) HighQMint.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("editor_tune_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Tune",
                            tint = if (showAdvancedSliders) HighQMint else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Central Video Area: Inbuilt Live Video Player OR Before/After Split Slider
            if (!showCompareMode && videoUri != null) {
                InbuiltVideoPlayer(
                    videoUri = videoUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 210.dp else 260.dp),
                    isFullscreen = isFullscreen,
                    onToggleFullscreen = { isFullscreen = !isFullscreen }
                )
            } else {
                BeforeAfterSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 210.dp else 260.dp),
                    sourceResLabel = extractedSourceRes,
                    targetResLabel = selectedTargetRes,
                    durationSeconds = extractedDuration
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))

            // Tool Selectors (Upscale, Stabilize, Denoise, Color)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditorToolItem(
                    title = "Upscale",
                    badge = "8K",
                    isSelected = selectedTool == EnhancementTool.UPSCALE,
                    onClick = { selectedTool = EnhancementTool.UPSCALE },
                    isCompact = isCompact,
                    testTag = "tool_upscale"
                )
                EditorToolItem(
                    title = "Stabilize",
                    icon = Icons.Filled.GraphicEq,
                    isSelected = selectedTool == EnhancementTool.STABILIZE,
                    onClick = { selectedTool = EnhancementTool.STABILIZE },
                    isCompact = isCompact,
                    testTag = "tool_stabilize"
                )
                EditorToolItem(
                    title = "Denoise",
                    icon = Icons.Filled.Grain,
                    isSelected = selectedTool == EnhancementTool.DENOISE,
                    onClick = { selectedTool = EnhancementTool.DENOISE },
                    isCompact = isCompact,
                    testTag = "tool_denoise"
                )
                EditorToolItem(
                    title = "Color",
                    icon = Icons.Filled.Palette,
                    isSelected = selectedTool == EnhancementTool.COLOR,
                    onClick = { selectedTool = EnhancementTool.COLOR },
                    isCompact = isCompact,
                    testTag = "tool_color"
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))

            // Target Resolution pills (720p, 1080p, 2K, 4K, 8K)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Target Super-Resolution",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isCompact) 13.sp else 14.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    listOf("720p", "1080p", "2K", "4K", "8K").forEach { res ->
                        val isSelected = selectedTargetRes == res
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) HighQMint else Color.Transparent)
                                .clickable { selectedTargetRes = res }
                                .padding(vertical = if (isCompact) 6.dp else 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = if (isCompact) 11.sp else 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 16.dp))

            // Enhancement Level / Presets
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Enhancement Level",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isCompact) 13.sp else 14.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PresetPill(
                        label = "Normal\n(Recommended)",
                        isSelected = selectedPreset == EnhancementPreset.NORMAL,
                        modifier = Modifier.weight(1.3f),
                        onClick = { selectedPreset = EnhancementPreset.NORMAL },
                        isCompact = isCompact,
                        testTag = "preset_normal"
                    )
                    PresetPill(
                        label = "High",
                        isSelected = selectedPreset == EnhancementPreset.HIGH,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPreset = EnhancementPreset.HIGH },
                        isCompact = isCompact,
                        testTag = "preset_high"
                    )
                    PresetPill(
                        label = "AI Max",
                        isSelected = selectedPreset == EnhancementPreset.AI_MAX,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPreset = EnhancementPreset.AI_MAX },
                        isCompact = isCompact,
                        testTag = "preset_aimax"
                    )
                }
            }

            // Advanced Tuning Sliders
            AnimatedVisibility(visible = showAdvancedSliders) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Super-Resolution Intensity",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(enhancementStrength * 100).toInt()}%",
                            color = HighQMint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = enhancementStrength,
                        onValueChange = { enhancementStrength = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = HighQMint,
                            activeTrackColor = HighQMint,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 18.dp else 26.dp))

            // Start Enhancing Button
            Button(
                onClick = {
                    onStartEnhancing(selectedTool, selectedPreset, enhancementStrength, selectedTargetRes)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 48.dp else 54.dp)
                    .clip(RoundedCornerShape(50))
                    .background(HighQMintAquaGradient)
                    .testTag("editor_start_enhancing_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF0C0E12)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "Enhance",
                        tint = Color(0xFF0C0E12),
                        modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                    )
                    Text(
                        text = "Start Enhancing ($selectedTargetRes)",
                        fontSize = if (isCompact) 14.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0C0E12)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun EditorToolItem(
    title: String,
    badge: String? = null,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    isCompact: Boolean,
    testTag: String
) {
    val containerBg by animateColorAsState(
        targetValue = if (isSelected) Color(0x3365F3BD) else MaterialTheme.colorScheme.surfaceVariant,
        label = "tool_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) HighQMint else MaterialTheme.colorScheme.outline,
        label = "tool_border"
    )
    val tintColor by animateColorAsState(
        targetValue = if (isSelected) HighQMint else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tool_tint"
    )

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCompact) 44.dp else 52.dp)
                .clip(CircleShape)
                .background(containerBg)
                .border(1.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (badge != null) {
                Text(
                    text = badge,
                    color = tintColor,
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    fontWeight = FontWeight.Bold
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tintColor,
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                )
            }
        }

        Text(
            text = title,
            color = if (isSelected) HighQMint else HighQTextMuted,
            fontSize = if (isCompact) 10.sp else 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun PresetPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isCompact: Boolean,
    testTag: String
) {
    val bg = if (isSelected) Color(0x3365F3BD) else Color.Transparent
    val border = if (isSelected) HighQMint else Color.Transparent
    val textColor = if (isSelected) HighQMint else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = if (isCompact) 6.dp else 8.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (isCompact) 10.sp else 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = if (isCompact) 12.sp else 14.sp
        )
    }
}
