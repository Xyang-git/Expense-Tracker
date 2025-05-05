package ul.final_project

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ul.final_project.ui.ViewModel

/**
 * Provides Factory to create instance of ViewModel
 **/
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ViewModel(
                moduleApplication().container.expenseRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ExpenseApplication].
 */
fun CreationExtras.moduleApplication(): ExpenseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ExpenseApplication)
