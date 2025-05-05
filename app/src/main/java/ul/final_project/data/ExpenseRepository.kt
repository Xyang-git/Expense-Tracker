package ul.final_project.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getAllExpensesStream(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpenseInMonth(date: String) : Flow<List<Expense>> = expenseDao.getExpenseInMonth(date)

    suspend fun getModuleCount(): Int = expenseDao.getCount()

    suspend fun insertExpense(expense: Expense) = expenseDao.insert(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)
}