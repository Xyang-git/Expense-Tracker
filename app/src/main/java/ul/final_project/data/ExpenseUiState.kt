package ul.final_project.data


data class ExpenseUiState(
    var currentExpense: Expense = Expense(date = "", detail = "", category = "", spend = 0.0),
    var allExpenses: List<Expense> = listOf(),
)
