package com.simats.legalchain.cases

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
    val tasks: Int = 0,
    val addedByLawyer: Boolean = false
)

object CaseRepository {

    // Start with no cases by default — cases must be explicitly added by a lawyer to appear.
    private val initialCases = emptyList<CaseModel>()

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

    fun deleteCase(id: String) {
        _cases.value = _cases.value.filterNot { it.id == id }
    }
}
