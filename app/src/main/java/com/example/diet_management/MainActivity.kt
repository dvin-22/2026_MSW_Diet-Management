package com.example.diet_management

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.diet_management.ui.DietApp
import com.example.diet_management.ui.theme.DietmanagementTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DietmanagementTheme {
                DietApp()
            }
        }
    }
}
