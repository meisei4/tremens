import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

@Composable
fun App() {
    var habitStatus by remember { mutableStateOf(listOf<Pair<String, List<Boolean>>>()) }
    var newHabit by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader()
        AddHabitField(newHabit) { newHabit = it }
        AddHabitButton {
            addNewHabit(newHabit, habitStatus) { updatedStatus ->
                habitStatus = updatedStatus
                newHabit = TextFieldValue("")
            }
        }
        DayHeader(getLastFiveDays())
        HabitList(
            habitStatus = habitStatus,
            onStatusUpdate = { updatedHabitStatus -> habitStatus = updatedHabitStatus },
            onRemoveHabit = { index -> habitStatus = removeHabit(index, habitStatus) }
        )
    }
}

@Composable
fun AppHeader() {
    Text("Habit Tracker", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AddHabitField(newHabit: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    TextField(value = newHabit, onValueChange = onValueChange, label = { Text("New Habit") })
}

@Composable
fun AddHabitButton(onClick: () -> Unit) {
    Button(onClick = onClick) { Text("Add Habit") }
    Spacer(modifier = Modifier.height(16.dp))
}

//TODO: WHY DOES THIS NOT ALIGN WITH THE STATUS CIRCLE BUTTONS.......
@Composable
fun HabitRowHeader(title: String, lastFiveDays: List<String>) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(4f)
        ) {
            lastFiveDays.forEach { day ->
                Text(
                    text = day,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)  // This ensures that each day takes up equal space.
                )
            }
        }
    }
}

@Composable
fun DayHeader(lastFiveDays: List<String>) {
    Spacer(modifier = Modifier.height(8.dp))
    HabitRowHeader("Habit", lastFiveDays)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun HabitList(
    habitStatus: List<Pair<String, List<Boolean>>>,
    onStatusUpdate: (List<Pair<String, List<Boolean>>>) -> Unit,
    onRemoveHabit: (Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(habitStatus) { index, habit ->
            HabitRow(
                habitName = habit.first,
                isDoneList = habit.second,
                onRemoveHabit = { onRemoveHabit(index) },
                onToggleStatus = { idx, updatedStatus ->
                    val updatedHabitStatus = updateHabitStatus(habit, idx, updatedStatus, habitStatus)
                    onStatusUpdate(updatedHabitStatus)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HabitRow(
    habitName: String,
    isDoneList: List<Boolean>,
    onRemoveHabit: () -> Unit,
    onToggleStatus: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeleteHabitButton(onRemoveHabit)

        Text(
            text = habitName,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        StatusCirclesRow(isDoneList, onToggleStatus, modifier = Modifier.weight(4f))
    }
}

@Composable
fun DeleteHabitButton(onRemoveHabit: () -> Unit) {
    IconButton(onClick = onRemoveHabit) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Black // Set a color tint
        )
    }
}

@Composable
fun StatusCirclesRow(
    isDoneList: List<Boolean>,
    onToggleStatus: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        isDoneList.forEachIndexed { index, isDone ->
            StatusCircle(isDone) {
                onToggleStatus(index, !isDone)
            }
        }
    }
}

@Composable
fun StatusCircle(isDone: Boolean, onToggleStatus: () -> Unit) {
    Box(
        Modifier
            .size(24.dp)
            .clickable { onToggleStatus() }
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

fun updateHabitStatus(
    currentHabit: Pair<String, List<Boolean>>,
    index: Int,
    updatedStatus: Boolean,
    habitStatus: List<Pair<String, List<Boolean>>>
): List<Pair<String, List<Boolean>>> {
    return habitStatus.map { habit ->
        if (habit.first == currentHabit.first) {
            habit.first to habit.second.mapIndexed { idx, status ->
                if (idx == index) updatedStatus else status
            }
        } else habit
    }
}

fun removeHabit(index: Int, habitStatus: List<Pair<String, List<Boolean>>>): List<Pair<String, List<Boolean>>> {
    return habitStatus.toMutableList().apply { removeAt(index) }
}

fun addNewHabit(newHabit: TextFieldValue, habitStatus: List<Pair<String, List<Boolean>>>, onUpdate: (List<Pair<String, List<Boolean>>>) -> Unit) {
    onUpdate(habitStatus + (newHabit.text to List(5) { false }))
}

fun getLastFiveDays(): List<String> {
    val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
    return List(5) { i ->
        current.minus(i, DateTimeUnit.DAY).dayOfMonth.toString()
    }.reversed()
}

expect fun getPlatformName(): String
