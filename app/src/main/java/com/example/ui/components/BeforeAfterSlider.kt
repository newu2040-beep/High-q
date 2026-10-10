package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.HighQBorderSubtle
import com.example.ui.theme.HighQMint
import com.example.ui.theme.HighQTextMuted
import kotlin.math.roundToInt

@Composable
fun BeforeAfterSlider(
    modifier: Modifier = Modifier,
    thumbnailResId: Int = R.drawable.demo_lake,
    sourceResLabel: String = "480p",
    targetResLabel: String = "4K",
    durationSeconds: Int = 84,
    onFullscreenToggle: () -> Unit = {}
) {
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0.38f) } // 00:32 / 01:24

    val currentSeconds = (durationSeconds * playbackProgress).toInt()
    val formattedCurrent = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60)
    val formattedTotal = String.format("%02d:%02d", durationSeconds / 60, durationSeconds % 60)

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF10131B))
            .border(1.dp, HighQBorderSubtle, RoundedCornerShape(26.dp))
    ) {
        val totalWidthPx = constraints.maxWidth.toFloat()
        val density = LocalDensity.current

        // Layer 1: Enhanced 4K background (full width base)
        val enhancedMatrix = remember {
            ColorMatrix().apply {
                setToSaturation(1.35f)
            }
        }
        Image(
            painter = painterResource(id = thumbnailResId),
            contentDescription = "Enhanced Preview",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(enhancedMatrix),
            modifier = Modifier.fillMaxSize()
        )

        // Layer 2: "Before" softer/original layer clipped to splitFraction
        val splitWidthDp = with(density) { (totalWidthPx * splitFraction).toDp() }
        val softMatrix = remember {
            ColorMatrix().apply {
                setToSaturation(0.85f)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(splitWidthDp)
                .clip(RoundedCornerShape(topStart = 26.dp, bottomStart = 26.dp))
        ) {
            Image(
                painter = painterResource(id = thumbnailResId),
                contentDescription = "Original Preview",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(softMatrix),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(density) { totalWidthPx.toDp() })
                    .blur(2.dp)
            )
        }

        // Badges: "Before" and "After"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Before Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x8A000000))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Before\n$sourceResLabel",
                    color = Color(0xEEFFFFFF),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 12.sp
                )
            }

            // After Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x8A000000))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "After\n$targetResLabel",
                    color = HighQMint,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 12.sp
                )
            }
        }

        // Draggable vertical divider line and handle
        val handleOffsetPx = totalWidthPx * splitFraction
        val handleOffsetDp = with(density) { (handleOffsetPx - 16.dp.toPx()).toDp() }

        // Divider vertical white line
        Box(
            modifier = Modifier
                .offset { IntOffset(x = handleOffsetPx.roundToInt() - 1, y = 0) }
                .fillMaxHeight()
                .width(2.dp)
                .background(Color.White)
        )

        // Draggable handle pill `< >`
        Box(
            modifier = Modifier
                .offset { IntOffset(x = (handleOffsetPx - 16.dp.toPx()).roundToInt(), y = (constraints.maxHeight / 2 - 16.dp.toPx()).roundToInt()) }
                .size(32.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newFraction = ((handleOffsetPx + dragAmount.x) / totalWidthPx).coerceIn(0.05f, 0.95f)
                        splitFraction = newFraction
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "< >",
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Bottom playback bar inside the video container
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xBF080A0E))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("video_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Slider(
                    value = playbackProgress,
                    onValueChange = { playbackProgress = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color(0x4DFFFFFF)
                    )
                )

                Text(
                    text = "$formattedCurrent / $formattedTotal",
                    color = Color(0xCCFFFFFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                IconButton(
                    onClick = onFullscreenToggle,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("video_fullscreen_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
