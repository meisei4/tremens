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

@Composable
fun AppView(viewModel: MainViewModel) {
//    addNewHabit(TextFieldValue(baseHabit), habitStatus) { updatedStatus ->
//        habitStatus = updatedStatus
//        newHabit = TextFieldValue("")
//    }

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader()
        AddHabitField(viewModel.newHabit.value) { viewModel.newHabit.value = it }
        AddHabitButton {
            viewModel.addNewHabit { updatedStatus ->
                viewModel.habitStatus = updatedStatus.toMutableList()
                viewModel.newHabit.value = TextFieldValue("")
            }
        }
        DayHeader(viewModel.lastFiveDays)
        HabitList(viewModel)
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
fun HabitList(viewModel : MainViewModel) {
    LazyColumn {
        itemsIndexed(viewModel.habitStatus) { index, habit ->
            HabitRow(viewModel, index, habit)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HabitRow(viewModel: MainViewModel, index: Int, habitStatus: Pair<String, List<Boolean>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeleteHabitButton(viewModel, index)
        var habitName = habitStatus.first
        Text(
            text = habitName,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        var isDoneList = habitStatus.second
        StatusCirclesRow(viewModel, habitStatus, isDoneList, modifier = Modifier.weight(4f))
    }
}

@Composable
fun DeleteHabitButton(viewModel: MainViewModel, index: Int) {
    IconButton(onClick = { viewModel.removeHabit(index) }) {
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
    viewModel: MainViewModel,
    currentHabit: Pair<String, List<Boolean>>,
    isDoneList: List<Boolean>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        isDoneList.forEachIndexed { index, isDone ->
            StatusCircle(isDone) {
                viewModel.updateHabitStatus(currentHabit, index, isDone)
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

expect fun getPlatformName(): String