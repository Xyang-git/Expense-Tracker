package ul.final_project.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ul.final_project.QcaAppScreen
import ul.final_project.R
import ul.final_project.data.DataSource
import ul.final_project.data.Expense
import ul.final_project.ui.theme.CategoryIndicator
import ul.final_project.ui.theme.ExpenseTrackerTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseAppBar(
    currentScreen: QcaAppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun ExpenseTrackerScreen(
    expenseList: Flow<List<Expense>>,
    currentMonth: String,
    onNextMonthClicked: () -> Unit,
    onPreviousMonthClicked: () -> Unit,
    onAddButtonClicked: () -> Unit,
    onRowDelete: (Expense) -> Unit,
    modifier: Modifier) {

    var expenses = expenseList.collectAsState(initial = listOf()).value
    val pieChartData = PieChartData(
        slices = Category.entries.map { category ->
            PieChartData.Slice(
                category.name,
                expenses.filter { it.category == category.name }
                    .sumOf { it.spend }
                    .toFloat(),
                CategoryIndicator(category.name)
            )
        },
        plotType = PlotType.Pie
    )

    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        showSliceLabels = true,
        animationDuration = 1500,
        backgroundColor = MaterialTheme.colorScheme.background
    )

    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = dimensionResource(R.dimen.padding_small))
            .safeDrawingPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.my_expenses),
            style = MaterialTheme.typography.headlineSmall
        )

        Row (modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            IconButton (
                onClick = onPreviousMonthClicked,
                modifier = Modifier.weight(1f)
            ){
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(R.string.previous_month),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = getMonthName(currentMonth.substring(5,7)) + " " + currentMonth.substring(0,4),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(15.dp))
            IconButton (
                onClick = onNextMonthClicked,
                modifier = Modifier.weight(1f)
            ){
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.next_month),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

        }
        Box(Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f)
            .align(alignment = Alignment.CenterHorizontally)){
            // if there is no expense in the month,
            // show a message instead of the pie chart
            if (expenses.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_expenses_warning),
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .padding(dimensionResource(R.dimen.padding_large)),
                    style = MaterialTheme.typography.headlineSmall,
                )
            } else {
                PieChart(
                    pieChartData = pieChartData,
                    modifier = Modifier
                        .fillMaxWidth(),
                    pieChartConfig = pieChartConfig,
                    onSliceClick = {},
                )
            }
        }
        Text(
            text = stringResource(R.string.total) +
                    stringResource(R.string.euro_sign) +
                    expenses.sumOf { expense -> expense.spend }.toString(),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
        )

        CategoryList(
            expenseList = expenses,
            onRowDelete = onRowDelete,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .fillMaxWidth()
                .testTag("ExpenseList"),
        )
    }
    Box(Modifier.fillMaxSize()){
        Button (
            onClick = { onAddButtonClicked() },
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.add_expense),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun CategoryList(expenseList: List<Expense>, onRowDelete: (Expense)-> Unit, modifier: Modifier) {
    LazyColumn (modifier = modifier) {
        items(Category.entries.toTypedArray()) {
            CategoryItem(
                category = it,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small)),
                expenseList = expenseList,
                onRowDelete = onRowDelete,
            )
            }
        }
}


@Composable
fun CategoryItem(
    category: Category,
    expenseList: List<Expense>,
    onRowDelete: (Expense)-> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val sumOfExpensesInCategory = expenseList.sumOf { expense ->
        if (expense.category == category.name) expense.spend
        else 0.0
    }
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = (CategoryIndicator(category.name)),
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.euro_sign) +
                            sumOfExpensesInCategory.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                )
                CategoryItemButton(
                    expanded = expanded,
                    // only expend if there are expenses in the category (none 0)
                    onClick = { if(sumOfExpensesInCategory != 0.0) expanded = !expanded }
                )
            }
            if (expanded) {
                ExpenseListInCategory(
                    expenseList = expenseList,
                    category = category,
                    onRowDelete = onRowDelete,
                    modifier = Modifier,
                )
            }
        }
    }

}

@Composable
private fun CategoryItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(R.string.expand_button_content_description),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseListInCategory(expenseList: List<Expense>,
                          category: Category,
                          onRowDelete: (Expense)-> Unit,
                          modifier: Modifier) {
    Column (modifier = modifier) {
        expenseList.filter { it.category == category.name}.forEach {
            expense ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp, top = 5.dp)
                    .combinedClickable(
                        onClick = { },
                        onLongClick = { onRowDelete(expense) }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = expense.detail,
                    modifier = Modifier
                        .weight(2f)
                        .padding(dimensionResource(R.dimen.padding_small)),
                )
                Text(
                    text = expense.date,
                    modifier = Modifier
                        .weight(2f),
                )
                Text(
                    text = expense.spend.toString(),
                    modifier = Modifier
                        .weight(1f),
                )
            }
        }
    }
}



@Composable
fun ModuleDeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

fun getMonthName (monthInt : String) : String{
    val monthMap = mapOf(
        "01" to "January",
        "02" to "February",
        "03" to "March",
        "04" to "April",
        "05" to "May",
        "06" to "June",
        "07" to "July",
        "08" to "August",
        "09" to "September",
        "10" to "October",
        "11" to "November",
        "12" to "December"
    )
    return monthMap.getValue(monthInt)
}



@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ExpenseTrackerTheme () {
        ExpenseTrackerScreen(
            expenseList = flow { emit(DataSource.dummyList) },
            currentMonth = "2025-04",
            onNextMonthClicked = {},
            onPreviousMonthClicked = {},
            onAddButtonClicked = {},
            onRowDelete = {},
            modifier = Modifier)
    }
}

