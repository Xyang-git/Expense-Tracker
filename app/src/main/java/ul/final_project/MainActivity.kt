package ul.final_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ul.final_project.data.ExpenseUiState
import ul.final_project.ui.AddExpenseScreen
import ul.final_project.ui.ExpenseAppBar
import ul.final_project.ui.ExpenseTrackerScreen
import androidx.navigation.compose.dialog
import kotlinx.coroutines.launch
import ul.final_project.data.Expense
import ul.final_project.ui.ModuleAlertDialog
import ul.final_project.ui.ModuleDeleteDialog
import ul.final_project.ui.ViewModel
import ul.final_project.ui.theme.ExpenseTrackerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExpenseApp(modifier = Modifier)
                }
            }
        }
    }
}


@Composable
private fun ExpenseApp(
    viewModel: ViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier
) {
    viewModel.initialize()
    val uiState by viewModel.expenseUiState.collectAsState()

    ExpenseApp(
        uiState = uiState,
        viewModel = viewModel,
        navController = rememberNavController(),
        modifier = Modifier
    )
}

@Composable
fun ExpenseApp(
    uiState: ExpenseUiState,
    viewModel: ViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = QcaAppScreen.valueOf(
        backStackEntry?.destination?.route ?: QcaAppScreen.ExpenseTrackScreen.name
    )

    Scaffold(
        topBar = {
            ExpenseAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val coroutineScope = rememberCoroutineScope()
        NavHost(
            navController = navController,
            startDestination = QcaAppScreen.ExpenseTrackScreen.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = QcaAppScreen.ExpenseTrackScreen.name) {
                ExpenseTrackerScreen(
                    expenseList = viewModel.getExpenseInMonth(),
                    currentMonth = viewModel.currentMonth,
                    onAddButtonClicked = {
                        navController.navigate(QcaAppScreen.AddExpenseScreen.name)
                    },
                    onNextMonthClicked = {
                        viewModel.nextMonth()
                        navController.navigate(QcaAppScreen.ExpenseTrackScreen.name)
                    },
                    onPreviousMonthClicked = {
                        viewModel.previousMonth()
                        navController.navigate(QcaAppScreen.ExpenseTrackScreen.name)
                    },
                    onRowDelete = { expense ->
                        viewModel.updateState(expense)
                        navController.navigate(QcaAppScreen.ExpenseDeleteDialog.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = QcaAppScreen.AddExpenseScreen.name) {
                AddExpenseScreen(
                    null,
                    onAddExpenseClicked = { expense ->
                        when (true) {
                            !isDetailValid(expense)
                                -> navController.navigate(QcaAppScreen.DetailErrorDialog.name)

                            !isCategoryValid(expense)
                                -> navController.navigate(QcaAppScreen.CategoryErrorDialog.name)

                            !isSpendValid(expense)
                                -> navController.navigate(QcaAppScreen.SpendAmountErrorDialog.name)

                            !isDateValid(expense)
                                -> navController.navigate(QcaAppScreen.DateErrorDialog.name)

                            else -> {
                                viewModel.updateState(expense)
                                coroutineScope.launch {
                                    viewModel.addExpense()
                                }
                                navController.navigate(QcaAppScreen.ExpenseTrackScreen.name)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    onCancelClicked = {
                        navController.navigate(QcaAppScreen.ExpenseTrackScreen.name)
                    }
                )
            }
            dialog(route = QcaAppScreen.ExpenseDeleteDialog.name) {
                ModuleDeleteDialog(
                    onDismissRequest = { navController.popBackStack() },
                    onDelete = {
                        coroutineScope.launch {
                            viewModel.deleteExpense()
                        }
                        navController.popBackStack()
                        navController.navigate(QcaAppScreen.ExpenseTrackScreen.name)
                    },
                    dialogTitle = stringResource (R.string.dialog_delete_title),
                    dialogText = stringResource(R.string.dialog_delete_text)
                )
            }

            dialog(
                route = QcaAppScreen.DetailErrorDialog.name) {
                    ModuleAlertDialog(
                        onDismissRequest = {navController.popBackStack()},
                    dialogTitle = stringResource(R.string.dialog_error_detail_title),
                    dialogText = stringResource(R.string.dialog_error_detail_text)
                    )
                }
            dialog(
                route = QcaAppScreen.CategoryErrorDialog.name) {
                ModuleAlertDialog(
                    onDismissRequest = {navController.popBackStack()},
                    dialogTitle = stringResource(R.string.dialog_error_category_title),
                    dialogText = stringResource(R.string.dialog_error_category_text)
                )
            }
            dialog(
                route = QcaAppScreen.DateErrorDialog.name) {
                ModuleAlertDialog(
                    onDismissRequest = {navController.popBackStack()},
                    dialogTitle = stringResource(R.string.dialog_error_date_title),
                    dialogText = stringResource(R.string.dialog_error_date_text)
                )

            }
            dialog(
                route = QcaAppScreen.SpendAmountErrorDialog.name) {
                ModuleAlertDialog(
                    onDismissRequest = {navController.popBackStack()},
                    dialogTitle = stringResource(R.string.dialog_error_spend_title),
                    dialogText = stringResource(R.string.dialog_error_spend_text)
                )
            }
        }
    }
}


fun isDetailValid(expense: Expense): Boolean {
    return expense.detail.isNotBlank()
}
fun isCategoryValid(expense: Expense): Boolean {
    return expense.category.isNotBlank()
}
fun isSpendValid(expense: Expense): Boolean {
    return expense.spend > 0.0
}
fun isDateValid(expense: Expense): Boolean {
    try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = LocalDate.parse(expense.date, formatter)
        return true
    } catch (e: Exception) {
        return false
    }
}






@Preview(showBackground = true)
@Composable
fun QcaAppPreview() {
    ExpenseTrackerTheme {
        ExpenseApp(modifier = Modifier)
    }
}

/**
 * enum values that represent the screens in the app
 */
enum class QcaAppScreen(@StringRes val title: Int) {
    ExpenseTrackScreen(title = R.string.app_name),
    AddExpenseScreen(title = R.string.add_expense),
    DetailErrorDialog(title = R.string.dialog_error_detail),
    CategoryErrorDialog(title = R.string.dialog_error_category),
    DateErrorDialog(title = R.string.dialog_error_date),
    SpendAmountErrorDialog(title = R.string.dialog_error_spend),
    ExpenseDeleteDialog(title = R.string.dialog_delete),
}