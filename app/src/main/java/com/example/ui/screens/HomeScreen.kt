package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.core.permissions.PermissionHelper
import com.example.firebase.FirebaseManager
import com.example.model.EnhancementTool
import com.example.model.VideoProject
import com.example.ui.components.FloatingNavBar
import com.example.ui.components.HighQWordmark
import com.example.ui.components.NavigationTab
import com.example.ui.theme.HighQBorderSubtle
import com.example.ui.theme.HighQEnhanceGradient
import com.example.ui.theme.HighQMint
import com.example.ui.theme.HighQPeach
import com.example.ui.theme.HighQTextMuted
import com.example.ui.theme.HighQTextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    firebaseManager: FirebaseManager,
    projects: List<VideoProject>,
    onSelectVideoUri: (Uri, EnhancementTool) -> Unit,
    onOpenProject: (VideoProject) -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenProfile: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by firebaseManager.currentUserState.collectAsState()

    var pendingTool by remember { mutableStateOf(EnhancementTool.UPSCALE) }

    // Multi-permission request launcher for media and notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Continue to video picker
    }

    // Android Photo Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSelectVideoUri(uri, pendingTool)
        }
    }

    val openPickerForTool: (EnhancementTool) -> Unit = { tool ->
        pendingTool = tool
        // Request runtime permissions if needed
        val perms = PermissionHelper.getRequiredMediaPermissions()
        if (perms.isNotEmpty()) {
            permissionLauncher.launch(perms)
        }
        videoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
        )
    }

    // Time-based greeting
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good Morning 🌿"
        in 12..16 -> "Good Afternoon ☀️"
        else -> "Good Evening ✨"
    }

    val hasPermissions = PermissionHelper.hasMediaVideoPermission(context)

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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 95.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = if (isCompact) 8.dp else 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HighQWordmark(
                    fontSize = if (isCompact) 22.sp else 26.sp,
                    textColor = MaterialTheme.colorScheme.onBackground
                )

                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 36.dp else 42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { onOpenProfile() }
                        .testTag("home_profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = currentUser?.displayName?.firstOrNull()?.uppercase()
                        ?: currentUser?.email?.firstOrNull()?.uppercase()
                        ?: "U"
                    Text(
                        text = initial,
                        color = HighQMint,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isCompact) 14.sp else 16.sp
                    )
                }
            }

            // Permission reminder banner if not granted
            if (!hasPermissions) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LockOpen,
                                contentDescription = "Permissions",
                                tint = HighQPeach,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Grant media & file access for optimal performance",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                permissionLauncher.launch(PermissionHelper.getRequiredMediaPermissions())
                            },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HighQMint,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Grant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 6.dp else 12.dp))

            // Greeting Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = if (isCompact) 13.sp else 15.sp
                )
                Text(
                    text = "Ready to enhance\nyour videos?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isCompact) 22.sp else 28.sp,
                    lineHeight = if (isCompact) 26.sp else 32.sp
                )
                Text(
                    text = "Sharper. Clearer. Better.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HighQTextMuted,
                    fontSize = if (isCompact) 12.sp else 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))

            // Hero Action Card: Mint-to-Peach Gradient "Enhance Video"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(HighQEnhanceGradient)
                    .clickable { openPickerForTool(EnhancementTool.UPSCALE) }
                    .padding(if (isCompact) 16.dp else 22.dp)
                    .testTag("home_hero_enhance_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isCompact) 42.dp else 48.dp)
                                .clip(CircleShape)
                                .background(Color(0x2E000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Enhance",
                                tint = Color(0xFF14171E),
                                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Enhance Video",
                                fontSize = if (isCompact) 16.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF11141A)
                            )
                            Text(
                                text = "Choose a video and start enhancing",
                                fontSize = if (isCompact) 11.sp else 12.sp,
                                color = Color(0xB311141A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 34.dp else 38.dp)
                            .clip(CircleShape)
                            .background(Color(0x38000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = Color(0xFF11141A),
                            modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 20.dp))

            // 2x2 Feature Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                ) {
                    FeatureCard(
                        title = "Upscale",
                        subtitle = "Higher Resolution",
                        badgeText = "4K/8K",
                        modifier = Modifier.weight(1f),
                        onClick = { openPickerForTool(EnhancementTool.UPSCALE) },
                        testTag = "feature_upscale_card"
                    )

                    FeatureCard(
                        title = "Stabilize",
                        subtitle = "Smooth Footage",
                        icon = Icons.Filled.GraphicEq,
                        iconTint = HighQMint,
                        modifier = Modifier.weight(1f),
                        onClick = { openPickerForTool(EnhancementTool.STABILIZE) },
                        testTag = "feature_stabilize_card"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp)
                ) {
                    FeatureCard(
                        title = "Denoise",
                        subtitle = "Clean & Clear",
                        icon = Icons.Filled.Grain,
                        iconTint = Color(0xFFA5B4FC),
                        modifier = Modifier.weight(1f),
                        onClick = { openPickerForTool(EnhancementTool.DENOISE) },
                        testTag = "feature_denoise_card"
                    )

                    FeatureCard(
                        title = "Color Fix",
                        subtitle = "Vibrant Colors",
                        icon = Icons.Filled.Palette,
                        iconTint = HighQPeach,
                        modifier = Modifier.weight(1f),
                        onClick = { openPickerForTool(EnhancementTool.COLOR) },
                        testTag = "feature_color_card"
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))

            // Recent Projects Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Projects",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isCompact) 15.sp else 18.sp
                )

                if (projects.isNotEmpty()) {
                    Text(
                        text = "See all",
                        color = HighQTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onNavigateTab(NavigationTab.LIBRARY) }
                            .testTag("home_see_all_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time Project List: No demo/fake data
            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VideoLibrary,
                            contentDescription = "Empty",
                            tint = HighQTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No Projects Yet",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Select a video from your gallery above to enhance up to 4K / 8K.",
                            color = HighQTextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
                ) {
                    items(projects) { project ->
                        RealProjectCard(
                            project = project,
                            onClick = { onOpenProject(project) }
                        )
                    }
                }
            }
        }

        // Floating Bottom Navigation Bar
        FloatingNavBar(
            currentTab = NavigationTab.HOME,
            onTabSelected = onNavigateTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = HighQMint,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x2665F3BD))
                        .border(1.dp, HighQMint.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = HighQMint,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            } else if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HighQTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun RealProjectCard(
    project: VideoProject,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("recent_project_${project.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color(0xFF1B1F2A))
            ) {
                if (project.thumbnailUri != null) {
                    AsyncImage(
                        model = project.thumbnailUri,
                        contentDescription = project.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.VideoLibrary,
                            contentDescription = null,
                            tint = HighQMint,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xD9000000))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = project.targetResolution,
                        color = HighQMint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = project.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = "${project.sourceResolution} • ${project.formattedDuration}",
                    color = HighQTextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
