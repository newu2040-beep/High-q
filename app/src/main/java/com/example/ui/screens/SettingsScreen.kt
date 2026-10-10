package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.core.permissions.PermissionHelper
import com.example.firebase.FirebaseManager
import com.example.ui.components.DeveloperCreditTag
import com.example.ui.components.FloatingNavBar
import com.example.ui.components.NavigationTab
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.HighQBorderSubtle
import com.example.ui.theme.HighQMint
import com.example.ui.theme.HighQPeach
import com.example.ui.theme.HighQTextMuted
import com.example.ui.theme.HighQTextSecondary

@Composable
fun SettingsScreen(
    firebaseManager: FirebaseManager,
    currentTheme: AppThemeMode,
    onSelectTheme: (AppThemeMode) -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenAuth: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by firebaseManager.currentUserState.collectAsState()

    var hardwareAccelEnabled by remember { mutableStateOf(true) }
    var cloudSyncEnabled by remember { mutableStateOf(true) }

    // Real Permission States
    var notificationGranted by remember {
        mutableStateOf(PermissionHelper.hasNotificationPermission(context))
    }
    var mediaGranted by remember {
        mutableStateOf(PermissionHelper.hasMediaVideoPermission(context))
    }
    var allFilesGranted by remember {
        mutableStateOf(PermissionHelper.hasAllFilesAccess(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        notificationGranted = PermissionHelper.hasNotificationPermission(context)
        mediaGranted = PermissionHelper.hasMediaVideoPermission(context)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (isCompact) 16.dp else 20.dp)
                .padding(bottom = 95.dp)
        ) {
            // Header
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 20.sp else 24.sp,
                modifier = Modifier.padding(vertical = if (isCompact) 10.dp else 16.dp)
            )

            // User Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(if (isCompact) 12.dp else 16.dp)
            ) {
                if (currentUser != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isCompact) 40.dp else 48.dp)
                                    .clip(CircleShape)
                                    .background(HighQMint.copy(alpha = 0.2f))
                                    .border(1.dp, HighQMint, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser?.displayName?.firstOrNull()?.uppercase() ?: "U",
                                    color = HighQMint,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isCompact) 16.sp else 18.sp
                                )
                            }

                            Column {
                                Text(
                                    text = currentUser?.displayName ?: "HIGHQ User",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = if (isCompact) 14.sp else 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = currentUser?.email ?: "Signed in",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { firebaseManager.signOut() },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("settings_sign_out_button")
                        ) {
                            Text("Sign Out", color = Color(0xFFFF7A7A), fontSize = 11.sp)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Guest Mode",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = if (isCompact) 14.sp else 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Sign in to sync 4K / 8K projects with Firestore",
                                color = HighQTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onOpenAuth,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HighQMint,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("settings_sign_in_button")
                        ) {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 18.dp))

            // Pastel Theme Switcher
            SettingsGroup(title = "Theme & Aesthetics", isCompact = isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppThemeMode.values().forEach { mode ->
                        val isSelected = currentTheme == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) HighQMint.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { onSelectTheme(mode) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (mode) {
                                                AppThemeMode.OBSIDIAN_DARK -> Color(0xFF14171E)
                                                AppThemeMode.MINT_PASTEL -> Color(0xFF65F3BD)
                                                AppThemeMode.PEACH_PASTEL -> Color(0xFFFFA978)
                                                AppThemeMode.LAVENDER_PASTEL -> Color(0xFF9E86E6)
                                            }
                                        )
                                )
                                Text(
                                    text = mode.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = HighQMint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 18.dp))

            // Permissions Manager (Full Access, Gallery, Notifications)
            SettingsGroup(title = "Privacy & File Permissions", isCompact = isCompact) {
                // 1. Full All Files Access
                PermissionItem(
                    icon = Icons.Filled.FolderSpecial,
                    name = "Full Files Access (Storage Manager)",
                    description = "Enables saving and exporting 4K/8K videos anywhere without restriction.",
                    status = if (allFilesGranted) "Granted" else "Grant Access",
                    isGranted = allFilesGranted,
                    onAction = {
                        PermissionHelper.openAllFilesAccessSettings(context)
                    }
                )

                // 2. Media Videos & Gallery
                PermissionItem(
                    icon = Icons.Filled.PermMedia,
                    name = "Gallery & Video Media Access",
                    description = "Direct system access to read high resolution source clips.",
                    status = if (mediaGranted) "Granted" else "Allow",
                    isGranted = mediaGranted,
                    onAction = {
                        permissionLauncher.launch(PermissionHelper.getRequiredMediaPermissions())
                    }
                )

                // 3. Notifications
                PermissionItem(
                    icon = Icons.Filled.Notifications,
                    name = "Background Export Alerts",
                    description = "Alerts when heavy 4K/8K rendering finishes in the background.",
                    status = if (notificationGranted) "Granted" else "Allow",
                    isGranted = notificationGranted,
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 18.dp))

            // Video Engine Preferences
            SettingsGroup(title = "Processing Engine", isCompact = isCompact) {
                SettingsToggleRow(
                    icon = Icons.Filled.Memory,
                    title = "GPU Acceleration (Vulkan / OGL)",
                    subtitle = "High-speed hardware texture pipeline",
                    checked = hardwareAccelEnabled,
                    onCheckedChange = { hardwareAccelEnabled = it }
                )

                SettingsToggleRow(
                    icon = Icons.Filled.CloudDone,
                    title = "Cloud Project Sync",
                    subtitle = "Real-time sync to Firebase Firestore",
                    checked = cloudSyncEnabled,
                    onCheckedChange = { cloudSyncEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 22.dp))

            // App Identity & Developer Credit
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(if (isCompact) 14.dp else 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "HIGHQ — Native Video Enhancer",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Application ID: highq.apps • Version 1.0",
                    color = HighQTextMuted,
                    fontSize = 11.sp
                )

                DeveloperCreditTag(isDark = currentTheme == AppThemeMode.OBSIDIAN_DARK)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Floating Bottom Nav
        FloatingNavBar(
            currentTab = NavigationTab.SETTINGS,
            onTabSelected = onNavigateTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    isCompact: Boolean,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = if (isCompact) 13.sp else 14.sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .padding(if (isCompact) 10.dp else 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun PermissionItem(
    icon: ImageVector,
    name: String,
    description: String,
    status: String,
    isGranted: Boolean,
    onAction: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x1A65F3BD)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = HighQMint,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                color = HighQTextMuted,
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }

        if (onAction != null && !isGranted) {
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HighQMint,
                    contentColor = Color.Black
                ),
                modifier = Modifier.height(30.dp)
            ) {
                Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x2665F3BD))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = status,
                    color = HighQMint,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x1FFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = HighQTextMuted,
                fontSize = 10.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HighQMint,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}
