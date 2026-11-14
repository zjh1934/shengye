package com.shengye.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Shell() }
    }
}

/**
 * P2 骨架页：只用来验证包名、构建链路与 CI→Release 通路。
 * P3 起由 design:glass + feature:* 接管，本文件退回只做导航组装。
 */
@Composable
private fun Shell() {
    val wall = Brush.linearGradient(
        listOf(Color(0xFFDCE3F6), Color(0xFFEAD9F2), Color(0xFFCFEAF3))
    )
    Box(Modifier.fillMaxSize().background(wall), contentAlignment = Alignment.Center) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White.copy(alpha = 0.62f),
            tonalElevation = 0.dp,
            shadowElevation = 10.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("声页", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2030))
                Text("2.0.0-alpha1", fontSize = 12.sp, color = Color(0xFF6B7280))
            }
        }
    }
}
