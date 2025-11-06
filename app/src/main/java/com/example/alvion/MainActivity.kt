package com.example.alvion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alvion.ui.StartScreen
import com.example.alvion.ui.SessionScreen
import com.example.alvion.ui.theme.ALVIONTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ALVIONTheme {
                AppNav()
            }
        }
    }
}

@Composable
private fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "start") {
        composable("start") {
            StartScreen(onStart = { nav.navigate("session") })
        }
        composable("session") {
            SessionScreen(onEnd = { nav.popBackStack() })
        }
    }
}
