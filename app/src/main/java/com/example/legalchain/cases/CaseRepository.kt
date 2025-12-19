package com.example.legalchain.cases

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Shared repository for cases so AddCaseScreen + CaseListScreen + CaseDetailsScreen use same data. */

data class CaseModel(
    val id: String,
    val title: String,
    val caseNumber: String,
    val type: String,
    val status: String,
    val court: String,
    val judge: String = "",
    val filingDate: String = "",
    val nextHearing: String = "",
    val hearingTime: String = "",
    val courtRoom: String = "",
    val description: String = "",
    val petitioner: String = "",
    val respondent: String = "",
    val advocate: String = "",
    val documents: Int = 0,
    val hearings: Int = 0,
    val tasks: Int = 0
)

object CaseRepository {

    // Initial sample cases (similar to existing CaseListScreen sample)
    private val initialCases = listOf(
        CaseModel(
            id = "1",
            title = "Singh vs. State of Maharashtra",
            caseNumber = "CR/2024/1234",
            type = "Criminal",
            status = "active",
            court = "High Court Mumbai",
            judge = "Hon. Justice R.K. Sharma",
            filingDate = "March 15, 2024",
            nextHearing = "December 15, 2024",
            hearingTime = "10:30 AM",
            courtRoom = "Court Room 5",
            description = "Criminal case involving alleged fraud and misappropriation of funds. The accused Mr. Vikram Singh is charged under sections 420 and 406 of IPC.",
            petitioner = "State of Maharashtra",
            respondent = "Mr. Vikram Singh",
            advocate = "Adv. Rajesh Kumar",
            documents = 12,
            hearings = 5,
            tasks = 3
        ),
        CaseModel(
            id = "2",
            title = "Sharma Property Dispute",
            caseNumber = "CV/2024/5678",
            type = "Civil",
            status = "pending",
            court = "District Court Delhi",
            nextHearing = "Dec 18, 2024",
            petitioner = "Mrs. Priya Sharma"
        ),
        CaseModel(
            id = "3",
            title = "TechCorp Merger Agreement",
            caseNumber = "CO/2024/9012",
            type = "Corporate",
            status = "active",
            court = "NCLT Mumbai",
            nextHearing = "Dec 20, 2024",
            petitioner = "TechCorp Pvt Ltd"
        ),
        CaseModel(
            id = "4",
            title = "Kumar Divorce Settlement",
            caseNumber = "FM/2024/3456",
            type = "Family",
            status = "pending",
            court = "Family Court",
            nextHearing = "Dec 22, 2024",
            petitioner = "Mr. Anil Kumar"
        ),
        CaseModel(
            id = "5",
            title = "Patel vs. Patel",
            caseNumber = "CV/2024/7890",
            type = "Civil",
            status = "closed",
            court = "District Court",
            petitioner = "Mr. Rajesh Patel"
        ),
        CaseModel(
            id = "6",
            title = "State vs. Mehta",
            caseNumber = "CR/2024/2345",
            type = "Criminal",
            status = "active",
            court = "Sessions Court",
            nextHearing = "Dec 25, 2024",
            petitioner = "State",
            respondent = "Mr. Suresh Mehta"
        )
    )

    private val _cases = MutableStateFlow(initialCases)
    val cases: StateFlow<List<CaseModel>> = _cases

    fun addCase(case: CaseModel) {
        _cases.value = _cases.value + case
    }

    fun updateCase(updated: CaseModel) {
        _cases.value = _cases.value.map { existing ->
            if (existing.id == updated.id) updated else existing
        }
    }

    fun removeCase(id: String) {
        _cases.value = _cases.value.filterNot { it.id == id }
    }

    fun getCase(id: String): CaseModel? = _cases.value.find { it.id == id }
}
