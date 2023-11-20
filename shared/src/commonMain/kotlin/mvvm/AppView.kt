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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import datasources.HabitRowData
import mvvm.MainViewModel

@Composable
fun AppScreen(viewModel: MainViewModel) {
    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader()
        AddHabitField(viewModel.newHabit.value.name) { newHabitName ->
            viewModel.newHabit.value = viewModel.newHabit.value.copy(name = newHabitName)
        }
        AddHabitButton(viewModel.newHabit) { viewModel.addHabit() }
        DayHeader(viewModel.lastFiveDays)
        HabitList(viewModel)
    }
}

//TODO Louis idea here for View<Screen thing, I forgot what it was
@Composable
fun AppView(
    newHabit: MutableState<HabitRowData>,
    addHabit: () -> Unit
) {
    AddHabitButton(newHabit = newHabit, onClick = addHabit)
}

@Composable
fun AppHeader() {
    Text("Habit Tracker", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AddHabitField(newHabitName: String, onValueChange: (String) -> Unit) {
    TextField(value = newHabitName, onValueChange = onValueChange, label = { Text("New Habit") })
}

@Composable
fun AddHabitButton(newHabit: State<HabitRowData>, onClick: () -> Unit) {
    Button(onClick = onClick,
    enabled = newHabit.value.name.isNotEmpty()) { Text("+") }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun DayHeader(lastFiveDays: List<String>) {
    Spacer(modifier = Modifier.height(8.dp))
    HabitRowHeader("Habit", lastFiveDays)
    Spacer(modifier = Modifier.height(8.dp))
}

//TODO: WHY DOES THIS NOT ALIGN WITH THE STATUS CIRCLE BUTTONS...
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
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HabitList(viewModel: MainViewModel) {
    LazyColumn {
        itemsIndexed(viewModel.habitRows.value) { index, habit ->
            HabitRow(
                habit = habit,
                deleteFunction = { viewModel.removeHabit(index) },
                updateHabitStatus = { dayColumnIndex: Int, status: Boolean -> viewModel.updateHabitStatus(habit, dayColumnIndex, status) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HabitRow(
    habit: HabitRowData,
    deleteFunction: () -> Unit,
    updateHabitStatus: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeleteHabitButton{ deleteFunction() }

        val habitName = habit.name
        Text(
            text = habitName,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )

        val isDoneList = habit.lastFiveDatesStatuses

        StatusCirclesRow(
            updateHabitFunction = { statusCircleIndex: Int, status: Boolean -> updateHabitStatus(statusCircleIndex, status) },
            isDoneList = isDoneList,
            modifier = Modifier.weight(4f)
        )
    }
}

@Composable
fun DeleteHabitButton(removeHabitFunction: () -> Unit) {
    IconButton(onClick = { removeHabitFunction() }) {
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
    updateHabitFunction: (Int, Boolean) -> Unit,
    isDoneList: List<Boolean>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        isDoneList.forEachIndexed { index, isDone ->
            StatusCircle(isDone) { newStatus ->
                updateHabitFunction(index, newStatus)
            }
        }
    }
}

@Composable
fun StatusCircle(isDone: Boolean, updateHabitFunction: (Boolean) -> Unit) {
    Box(
        Modifier
            .size(24.dp)
            .clickable { updateHabitFunction(!isDone) } // toggle the status
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
