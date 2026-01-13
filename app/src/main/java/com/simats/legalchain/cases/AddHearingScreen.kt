package com.simats.legalchain.cases

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
    onSave: (Hearing) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedCaseIndex by remember { mutableStateOf(0) }
    val (caseId, caseTitle, caseNumber) =
        availableCases.getOrElse(selectedCaseIndex) { availableCases.first() }

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var courtRoom by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var reminders by remember { mutableStateOf(Reminders()) }

    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            date = "%04d-%02d-%02d".format(y, m + 1, d)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePicker = TimePickerDialog(
        context,
        { _: TimePicker, h: Int, min: Int ->
            time = "%02d:%02d".format(h, min)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Surface(modifier = Modifier.fillMaxSize(), color = PageBg) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        "Add Hearing",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                /* ---- CASE INFO ---- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkGreen)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("Adding hearing for", color = Color.White.copy(0.9f), fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(caseTitle, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(caseNumber, color = Color.White.copy(0.8f), fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                /* ---- DATE & TIME ---- */
                SectionCard("Date & Time") {
                    PickerField("Hearing Date", date, Icons.Filled.CalendarToday) { datePicker.show() }
                    Spacer(Modifier.height(12.dp))
                    PickerField("Hearing Time", time, Icons.Filled.Schedule) { timePicker.show() }
                }

                Spacer(Modifier.height(12.dp))

                /* ---- LOCATION ---- */
                SectionCard("Location") {
                    OutlinedTextField(
                        value = courtRoom,
                        onValueChange = { courtRoom = it },
                        label = { Text("Court Room") },
                        leadingIcon = { Icon(Icons.Filled.Place, null, tint = DarkGreen) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(12.dp))

                /* ---- DETAILS ---- */
                SectionCard("Hearing Details") {
                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Purpose") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                /* ---- REMINDERS ---- */
                SectionCard("Set Reminders") {
                    ReminderRow("1 day before", reminders.oneDay) {
                        reminders = reminders.copy(oneDay = it)
                    }
                    ReminderRow("2 hours before", reminders.twoHours) {
                        reminders = reminders.copy(twoHours = it)
                    }
                    ReminderRow("30 minutes before", reminders.thirtyMin) {
                        reminders = reminders.copy(thirtyMin = it)
                    }
                }

                Spacer(Modifier.height(20.dp))

                /* ---- ACTION BUTTONS ---- */
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (date.isBlank() || time.isBlank() || courtRoom.isBlank()) {
                                Toast.makeText(context, "Fill all required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            onSave(
                                Hearing(
                                    caseId,
                                    caseTitle,
                                    caseNumber,
                                    date,
                                    time,
                                    courtRoom,
                                    purpose,
                                    notes,
                                    reminders
                                )
                            )

                            Toast.makeText(context, "Hearing added", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Text("Add Hearing", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

/* ---------------- HELPERS ---------------- */

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardWhite)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PickerField(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = DarkGreen) },
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(Icons.Filled.Edit, null, tint = DarkGreen)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ReminderRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Checkbox(checked = checked, onCheckedChange = onChange)
    }
}
