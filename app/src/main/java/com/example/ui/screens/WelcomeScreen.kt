package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.DeveloperCreditTag
import com.example.ui.components.HighQWordmark

@Composable
fun WelcomeScreen(
    onContinueWithGoogle: () -> Unit,
    onContinueWithEmail: () -> Unit,
    onExploreAsGuest: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxHeight < 680.dp

        // Cinematic Background
        Image(
            painter = painterResource(id = R.drawable.welcome_bg),
            contentDescription = "Cinematic Sunset Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Gradient Scrim Overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC080A0E),
                            Color(0x40080A0E),
                            Color(0xE6080A0E),
                            Color(0xFF0C0E12)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = if (isCompact) 18.dp else 24.dp, vertical = if (isCompact) 12.dp else 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (isCompact) Arrangement.spacedBy(16.dp) else Arrangement.SpaceBetween
        ) {
            // Header: HIGHQ Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = if (isCompact) 8.dp else 16.dp)
            ) {
                HighQWordmark(fontSize = if (isCompact) 28.sp else 34.sp)
                Text(
                    text = "Video Quality Enhancer",
                    color = Color(0x99FFFFFF),
                    fontSize = if (isCompact) 11.sp else 12.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp
                )
            }

            if (!isCompact) {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Headline & Supporting Copy
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isCompact) 8.dp else 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Turn Moments\nInto Masterpieces",
                    style = if (isCompact) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    lineHeight = if (isCompact) 28.sp else 36.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enhance. Upscale. Restore.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xEEFFFFFF),
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isCompact) 14.sp else 16.sp
                )

                Text(
                    text = "Make your videos look their best.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0x99FFFFFF),
                    fontSize = if (isCompact) 12.sp else 14.sp
                )
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Sign In Pill Button
                Button(
                    onClick = onContinueWithGoogle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 48.dp else 54.dp)
                        .testTag("welcome_google_button"),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "G",
                            fontSize = if (isCompact) 16.sp else 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4285F4)
                        )
                        Text(
                            text = "Continue with Google",
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }

                // Email Sign In Capsule Button
                Button(
                    onClick = onContinueWithEmail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isCompact) 48.dp else 54.dp)
                        .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(50))
                        .testTag("welcome_email_button"),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x331C202B),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mail,
                            contentDescription = "Email",
                            tint = Color.White,
                            modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                        )
                        Text(
                            text = "Continue with Email",
                            fontSize = if (isCompact) 14.sp else 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Guest Explorer Option
                TextButton(
                    onClick = onExploreAsGuest,
                    modifier = Modifier.testTag("welcome_guest_button")
                ) {
                    Text(
                        text = "Continue as Guest",
                        color = Color(0xAAFFFFFF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Developer Credit at bottom
                DeveloperCreditTag(isDark = true)
            }
        }
    }
}
