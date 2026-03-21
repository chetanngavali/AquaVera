package com.aquavera.aquavera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aquavera.aquavera.navigation.AquaNavGraph
import com.aquavera.aquavera.ui.theme.AquaVeraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AquaVeraTheme {
                AquaNavGraph()
            }
        }
    }
}
