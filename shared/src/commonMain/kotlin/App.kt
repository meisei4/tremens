import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        // Initialize a list of habits and their statuses
        var habitStatus by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
        var newHabit by remember { mutableStateOf(TextFieldValue("")) }

        // UI layout
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Habit Tracker", style = MaterialTheme.typography.h4)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = newHabit,
                onValueChange = { newHabit = it },
                label = { Text("New Habit") }
            )

            Button(onClick = {
                habitStatus = habitStatus + (newHabit.text to false)
                newHabit = TextFieldValue("")
            }) {
                Text("Add Habit")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(habitStatus) { habit ->
                    HabitRow(
                        habitName = habit.first,
                        isDone = habit.second,
                        onToggleStatus = { updatedStatus ->
                            // Update habit status
                            habitStatus = habitStatus.map { if (it.first == habit.first) it.first to updatedStatus else it }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HabitRow(habitName: String, isDone: Boolean, onToggleStatus: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = habitName, style = MaterialTheme.typography.body1)
        Box(
            Modifier
                .size(24.dp)
                .clickable { onToggleStatus(!isDone) }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    color = if (isDone) Color.Green else Color.Gray,
                    radius = size.minDimension / 2f,
                    center = Offset(size.minDimension / 2f, size.minDimension / 2f)
                )
            }
        }
    }
}

expect fun getPlatformName(): String