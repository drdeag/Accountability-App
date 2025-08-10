
package com.dev.accountability

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent {
            MaterialTheme {
                DashboardScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen(vm: MainViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accountability") },
                navigationIcon = {
                    TextButton(onClick = { vm.togglePause() }) {
                        Text(if (state.paused) "Resume" else "Pause")
                    }
                },
                actions = {
                    TextButton(onClick = { vm.openSettings() }) { Text("Settings") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.openCameraOrLog() }) {
                Text("📷")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Current Streak: ${state.streak} days", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Next Meal: ${state.nextMealLabel} in ${state.nextMealCountdown}")
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                items(state.meals.size) { i ->
                    val meal = state.meals[i]
                    MealRow(
                        label = meal.label,
                        status = meal.status,
                        onCamera = { vm.logMeal(meal.label, withPhoto = true) },
                        onManual = { vm.logMeal(meal.label, withPhoto = false) },
                        onSkip = { vm.skipMeal(meal.label) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.openHistory() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("📆 History")
            }
        }
    }
}

@Composable
fun MealRow(label: String, status: MealStatus, onCamera: ()->Unit, onManual: ()->Unit, onSkip: ()->Unit) {
    val color = when(status) {
        MealStatus.DONE -> Color(0xFF2E7D32)
        MealStatus.MISSED -> Color(0xFFC62828)
        MealStatus.SKIPPED -> Color(0xFF9E9E9E)
        MealStatus.UPCOMING -> Color.Unspecified
    }
    Surface(tonalElevation = 2.dp) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    when(status){
                        MealStatus.DONE -> "✅"
                        MealStatus.MISSED -> "🔴"
                        MealStatus.SKIPPED -> "⚪"
                        MealStatus.UPCOMING -> "⏳"
                    }, modifier = Modifier.padding(end = 12.dp), color = color
                )
                Text("📷", modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { onCamera() })
                Text("✏", modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { onManual() })
                Text("❌", modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .clickable { onSkip() })
            }
        }
    }
}
