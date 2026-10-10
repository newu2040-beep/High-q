package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighQOrange
import com.example.ui.theme.HighQPeach
import com.example.ui.theme.HighQTextMuted

@Composable
fun HighQWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    textColor: Color = Color.White
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    letterSpacing = (-0.5).sp
                )
            ) {
                append("HIGH")
            }
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Black,
                    color = HighQPeach,
                    letterSpacing = (-0.5).sp
                )
            ) {
                append("Q")
            }
        },
        fontSize = fontSize,
        modifier = modifier
    )
}

@Composable
fun DeveloperCreditTag(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (isDark) Color(0x22FFFFFF) else Color(0x14000000))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "❤️",
            fontSize = 11.sp
        )
        Text(
            text = "Made with love by Rahul Shah",
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) Color(0xCCFFFFFF) else Color(0x99000000),
            fontWeight = FontWeight.Medium
        )
    }
}
