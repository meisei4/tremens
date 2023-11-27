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
        AddHabitField(viewModel.newHabit.value) { newHabitName ->
            viewModel.newHabit.value = newHabitName
        }
        AddHabitButton(viewModel.newHabit.value) { viewModel.addHabit() }
        DayHeader(viewModel.lastFiveDays)
        HabitList(viewModel)
    }
}

//TODO Louis idea here for View<Screen thing, I forgot what it was
@Composable
fun AppView(
    newHabit: MutableState<String>,
    addHabit: () -> Unit
) {
    AddHabitButton(newHabitName = newHabit.value, onClick = addHabit)
}

@Composable
fun AppHeader() {
    Text("Habit Tracker", style = MaterialTheme.typography.h4)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AddHabitField(newHabitName: String, onValueChange: (String) -> Unit) {
    TextField(
        value = newHabitName,
        onValueChange = onValueChange,
        label = { Text("New Habit") },
        singleLine = true // Add this to ensure the TextField does not expand to multiple lines
    )
}

@Composable
fun AddHabitButton(newHabitName: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = newHabitName.isNotEmpty() // Check if the habit name is not empty
    ) {
        Text("+")
    }
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
    val habitRows by viewModel.habitRows.collectAsState()

    LaunchedEffect(habitRows) {
        //Logger.log("Habit list updated (View level): $habitRows")
    }

    LazyColumn {
        itemsIndexed(habitRows) { index, habit ->
            HabitRow(
                habit = habit,
                deleteFunction = { viewModel.removeHabit(index) },
                updateHabitStatus = { dayIndex: Int, status: Boolean -> viewModel.updateHabitStatus(habit, dayIndex, status) }
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
