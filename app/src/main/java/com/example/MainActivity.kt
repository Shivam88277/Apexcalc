package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CalculatorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModel.Factory(application)
            )
            val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(preset = currentTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CalculatorScreen(viewModel = viewModel)
                }
            }
        }
    }
}
