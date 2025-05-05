package ul.final_project.ui

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ul.final_project.data.DataSource
import ul.final_project.data.ExpenseUiState
import ul.final_project.data.Expense
import ul.final_project.data.ExpenseRepository

class ViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {
    private val _expenseUiState = MutableStateFlow(ExpenseUiState())
    val expenseUiState: MutableStateFlow<ExpenseUiState> = _expenseUiState
    var currentMonth = "2025-04"

    private var initializeCalled = false
    // This function is idempotent provided it is only called from the UI thread.
    @MainThread
    fun initialize() {
        if(initializeCalled) return
        initializeCalled = true

        viewModelScope.launch {
            if (expenseRepository.getModuleCount()==0) {
                initDB()
            }
            expenseRepository.getAllExpensesStream().collect { element ->
                _expenseUiState.value.allExpenses = element
            }
        }

    }

    fun updateState(expenseDetails: Expense) {
        _expenseUiState.value.currentExpense = expenseDetails
    }

    fun getExpenseInMonth() : Flow<List<Expense>> {
        return expenseRepository.getExpenseInMonth(currentMonth)
    }

    fun nextMonth(){
        var year = currentMonth.substring(0,4).toInt()
        var month = currentMonth.substring(5,7).toInt()
        if (month == 12){
            year ++
            month = 1
        }else{
            month ++
        }
        if (month < 10){
            currentMonth = "$year-0$month"
        }else{
            currentMonth = "$year-$month"
        }
    }

    fun previousMonth(){
        var year = currentMonth.substring(0,4).toInt()
        var month = currentMonth.substring(5,7).toInt()
        if (month == 1){
            year --
            month = 12
        }else{
            month --
        }
        if (month < 10){
            currentMonth = "$year-0$month"
        }else{
            currentMonth = "$year-$month"
        }
    }

    suspend fun addExpense() {
        expenseRepository.insertExpense(expenseUiState.value.currentExpense)
    }

    suspend fun deleteExpense() {
        expenseRepository.deleteExpense(expenseUiState.value.currentExpense)
    }

    suspend fun initDB() {
        DataSource.dummyList.forEach({expense ->
            expenseRepository.insertExpense(expense)

        })
    }

}