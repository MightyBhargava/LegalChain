package com.example.legalchain.cases

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

private val DarkGreen = Color(0xFF0B5D2E)
private val SurfaceGreen = Color(0xFFF2FBF5)
private val CardWhite = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseScreen(
    existingCaseId: String? = null,
    onBack: () -> Unit = {},
    onCreate: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current

    val existingCase = remember(existingCaseId) {
        existingCaseId?.let { CaseRepository.getCase(it) }
    }

    var caseTitle by remember { mutableStateOf(existingCase?.title ?: "") }
    var caseNumber by remember { mutableStateOf(existingCase?.caseNumber ?: "") }
    val initialTypes = listOf(
        "Civil", "Criminal", "Corporate", "Family",
        "Property", "Contract", "IPR", "Labour", "Tax", "Banking", "Cyber", "Consumer", "Custom Type"
    )
    val caseTypes = remember { mutableStateListOf<String>().apply { addAll(initialTypes) } }

    var caseType by remember { mutableStateOf(existingCase?.type ?: "") }
    var customCaseType by remember { mutableStateOf("") }
    var caseDescription by remember { mutableStateOf(existingCase?.description ?: "") }

    var stateText by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }

    var courtType by remember { mutableStateOf("") }
    val courts = listOf("High Court", "District Court", "Sessions Court", "Family Court", "NCLT", "Supreme Court")

    var courtName by remember { mutableStateOf(existingCase?.court ?: "") }
    var filingDate by remember { mutableStateOf(existingCase?.filingDate ?: "") }

    var petitioner by remember { mutableStateOf(existingCase?.petitioner ?: "") }
    var respondent by remember { mutableStateOf(existingCase?.respondent ?: "") }
    var assignedLawyer by remember { mutableStateOf(existingCase?.advocate ?: "") }

    var showCaseTypeDropdown by remember { mutableStateOf(false) }
    var showCourtDropdown by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePicker = DatePickerDialog(LocalContext.current, { _: DatePicker, y: Int, m: Int, d: Int ->
        val mm = (m + 1).toString().padStart(2, '0')
        val dd = d.toString().padStart(2, '0')
        filingDate = "${y}-$mm-$dd"
    }, year, month, day)

    val isEditMode = existingCase != null
    val primaryButtonText = if (isEditMode) "Save Changes" else "Create Case"

    Surface(modifier = Modifier.fillMaxSize(), color = SurfaceGreen) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Case" else "Add New Case",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // BASIC INFO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Basic Information", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen)
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = caseTitle,
                            onValueChange = { caseTitle = it },
                            label = { Text("Case Title", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                            leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null, tint = DarkGreen) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = caseNumber,
                            onValueChange = { caseNumber = it },
                            label = { Text("Case Number (Mandatory)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = showCaseTypeDropdown,
                            onExpandedChange = { showCaseTypeDropdown = !showCaseTypeDropdown }
                        ) {
                            OutlinedTextField(
                                value = caseType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Case Type *", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = DarkGreen) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCaseTypeDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = showCaseTypeDropdown,
                                onDismissRequest = { showCaseTypeDropdown = false }
                            ) {
                                caseTypes.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(t, fontWeight = FontWeight.Medium, fontSize = 15.sp) },
                                        onClick = {
                                            caseType = t
                                            showCaseTypeDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        if (caseType == "Custom Type") {
                            OutlinedTextField(
                                value = customCaseType,
                                onValueChange = { customCaseType = it },
                                label = { Text("Enter Custom Case Type", fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                        OutlinedTextField(
                            value = caseDescription,
                            onValueChange = { caseDescription = it },
                            label = { Text("Case Description", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // COURT DETAILS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Court Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen)
                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = stateText,
                                onValueChange = { stateText = it },
                                label = { Text("State", fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("District", fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = showCourtDropdown,
                            onExpandedChange = { showCourtDropdown = !showCourtDropdown }
                        ) {
                            OutlinedTextField(
                                value = courtType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Court Type", fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Filled.BusinessCenter, contentDescription = null, tint = DarkGreen) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCourtDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = showCourtDropdown,
                                onDismissRequest = { showCourtDropdown = false }
                            ) {
                                courts.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c, fontSize = 15.sp) },
                                        onClick = {
                                            courtType = c
                                            showCourtDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = courtName,
                            onValueChange = { courtName = it },
                            label = { Text("Court Name / Hall Number", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = DarkGreen) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = filingDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filing Date", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = DarkGreen) },
                            trailingIcon = {
                                IconButton(onClick = { datePicker.show() }) {
                                    Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date", tint = DarkGreen)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // PARTIES
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Parties & Representation", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen)
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = petitioner,
                            onValueChange = { petitioner = it },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = DarkGreen) },
                            label = { Text("Petitioner / Plaintiff", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = respondent,
                            onValueChange = { respondent = it },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = DarkGreen) },
                            label = { Text("Respondent / Defendant", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = assignedLawyer,
                            onValueChange = { assignedLawyer = it },
                            label = { Text("Assigned Lawyer", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Filled.Work, contentDescription = null, tint = DarkGreen) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Cancel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                    }

                    Button(
                        onClick = {
                            if (caseNumber.isBlank()) {
                                Toast.makeText(context, "Case Number required", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (caseType.isBlank()) {
                                Toast.makeText(context, "Select Case Type", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val finalType = if (caseType == "Custom Type" && customCaseType.isNotBlank()) {
                                if (!caseTypes.contains(customCaseType)) {
                                    caseTypes.add(caseTypes.size - 1, customCaseType)
                                }
                                customCaseType
                            } else caseType

                            if (isEditMode && existingCase != null) {
                                val updatedCase = existingCase.copy(
                                    title = caseTitle.ifBlank { caseNumber },
                                    caseNumber = caseNumber,
                                    type = finalType,
                                    court = if (courtName.isNotBlank()) courtName else courtType,
                                    filingDate = filingDate,
                                    description = caseDescription,
                                    petitioner = petitioner,
                                    respondent = respondent,
                                    advocate = assignedLawyer
                                )
                                CaseRepository.updateCase(updatedCase)
                                Toast.makeText(context, "Case updated", Toast.LENGTH_SHORT).show()
                            } else {
                                val newCase = CaseModel(
                                    id = System.currentTimeMillis().toString(),
                                    title = caseTitle.ifBlank { caseNumber },
                                    caseNumber = caseNumber,
                                    type = finalType,
                                    status = "active",
                                    court = if (courtName.isNotBlank()) courtName else courtType,
                                    judge = "",
                                    filingDate = filingDate,
                                    nextHearing = "",
                                    hearingTime = "",
                                    courtRoom = "",
                                    description = caseDescription,
                                    petitioner = petitioner,
                                    respondent = respondent,
                                    advocate = assignedLawyer
                                )
                                CaseRepository.addCase(newCase)
                                Toast.makeText(context, "Case created", Toast.LENGTH_SHORT).show()
                            }

                            onCreate()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                    ) {
                        Text(primaryButtonText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
