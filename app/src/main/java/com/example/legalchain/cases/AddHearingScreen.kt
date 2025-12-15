package com.example.legalchain.cases

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

private val DarkGreen = Color(0xFF0B5D2E)
private val PageBg = Color(0xFFF4F6F5)
private val CardWhite = Color.White

data class Hearing(
    val caseId: String,
    val caseTitle: String,
    val caseNumber: String,
    val date: String,
    val time: String,
    val courtRoom: String,
    val purpose: String,
    val notes: String,
    val reminders: Reminders
)

data class Reminders(
    val oneDay: Boolean = true,
    val twoHours: Boolean = true,
    val thirtyMin: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHearingScreen(
    availableCases: List<Triple<String, String, String>> = listOf(
        Triple("1", "Singh vs. State of Maharashtra", "CR/2024/1234"),
        Triple("2", "TechCorp vs. Zee", "CR/2024/5678")
    ),
    onBack: () -> Unit = {},
    onAddHearing: (Hearing) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedCaseIndex by remember { mutableStateOf(0) }
    val (caseId, caseTitle, caseNumber) = availableCases.getOrElse(selectedCaseIndex) { availableCases.first() }

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var courtRoom by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var reminders by remember { mutableStateOf(Reminders()) }

    var showCaseDropdown by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        val mm = (m + 1).toString().padStart(2, '0')
        val dd = d.toString().padStart(2, '0')
        date = "${y}-$mm-$dd"
    }, year, month, day)

    val timePicker = TimePickerDialog(context, { _: TimePicker, h: Int, min: Int ->
        val hh = h.toString().padStart(2, '0')
        val mm = min.toString().padStart(2, '0')
        time = "$hh:$mm"
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)

    Surface(modifier = Modifier.fillMaxSize(), color = PageBg) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Add Hearing", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkGreen) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )

            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)) {

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen)
                    .padding(14.dp)) {
                    Column {
                        Text(text = "Adding hearing for", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = caseTitle, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = caseNumber, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Date & Time", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = date,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hearing Date", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = DarkGreen) },
                            trailingIcon = {
                                IconButton(onClick = { datePicker.show() }) { Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date", tint = DarkGreen) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = time,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hearing Time", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Filled.AccessTime, contentDescription = null, tint = DarkGreen) },
                            trailingIcon = {
                                IconButton(onClick = { timePicker.show() }) { Icon(Icons.Filled.Schedule, contentDescription = "Pick time", tint = DarkGreen) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Location", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = courtRoom,
                            onValueChange = { courtRoom = it },
                            label = { Text("Court Room", fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("e.g., Court Room 5") },
                            leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = DarkGreen) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hearing Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = purpose,
                            onValueChange = { purpose = it },
                            label = { Text("Purpose", fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("e.g., Arguments on Evidence") },
                            leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null, tint = DarkGreen) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes", fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("Add any notes or preparation items...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = DarkGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Set Reminders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ReminderRow("1 day before", reminders.oneDay) { checked -> reminders = reminders.copy(oneDay = checked) }
                        Spacer(modifier = Modifier.height(8.dp))
                        ReminderRow("2 hours before", reminders.twoHours) { checked -> reminders = reminders.copy(twoHours = checked) }
                        Spacer(modifier = Modifier.height(8.dp))
                        ReminderRow("30 minutes before", reminders.thirtyMin) { checked -> reminders = reminders.copy(thirtyMin = checked) }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(56.dp)) {
                        Text("Cancel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                    }

                    Button(onClick = {
                        if (date.isBlank()) {
                            Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (time.isBlank()) {
                            Toast.makeText(context, "Please select time", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (courtRoom.isBlank()) {
                            Toast.makeText(context, "Please enter court room", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val hearing = Hearing(
                            caseId = caseId,
                            caseTitle = caseTitle,
                            caseNumber = caseNumber,
                            date = date,
                            time = time,
                            courtRoom = courtRoom,
                            purpose = purpose,
                            notes = notes,
                            reminders = reminders
                        )

                        onAddHearing(hearing)
                        Toast.makeText(context, "Hearing added", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)) {
                        Text("Add Hearing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReminderRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F7F8))
            .clickable { onChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937))
        Checkbox(checked = checked, onCheckedChange = onChange, colors = CheckboxDefaults.colors(checkedColor = DarkGreen))
    }
}
