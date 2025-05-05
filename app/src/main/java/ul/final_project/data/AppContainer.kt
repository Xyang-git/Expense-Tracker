package ul.final_project.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val expenseRepository: ExpenseRepository
}

/**
 * [AppContainer] implementation that provides instance of [ExpenseRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(ExpenseDatabase.getDatabase(context).moduleDao())
    }
}