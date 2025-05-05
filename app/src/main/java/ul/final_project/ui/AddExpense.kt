package ul.final_project.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import ul.final_project.R
import ul.final_project.data.Expense
import ul.final_project.ui.theme.ExpenseTrackerTheme


@Composable
fun AddExpenseScreen(
    expense: Expense?,
    onAddExpenseClicked: (Expense)->Unit,
    onCancelClicked: ()->Unit,
    modifier: Modifier = Modifier
) {

    var detailInput by remember { mutableStateOf(expense?.detail?:"") }
    var dateInput by remember { mutableStateOf(expense?.date?:"") }
    var spendAmountInput by remember { mutableStateOf(expense?.spend?.toString()?:"") }
    var categoryInput by remember { mutableStateOf((expense?.category?:"")) }


    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            Text(
                text = stringResource(R.string.add_expense),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            OutlinedTextField(
                value = detailInput,
                label = { Text(stringResource(R.string.expense_detail)) },
                maxLines = 2,
                onValueChange = { detailInput = it },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            CategoryDropdown(
                selectedCategory = categoryInput,
                onCategorySelected = { categoryInput = it },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            OutlinedTextField(
                value = dateInput,
                onValueChange = {
                    it ->
                    dateInput = it.filter { it.isDigit() || it == '-'}.take(10)},
                label = { Text(stringResource(R.string.expense_date)) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.date_format),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
            OutlinedTextField(
                value = spendAmountInput,
                label = { Text(stringResource(R.string.spend_amount)) },
                maxLines = 1,
                onValueChange = { newValue ->
                    spendAmountInput = newValue.filter { it.isDigit() || it == '.' }
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.amount_format),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
            )
        }

        Row (Modifier
            .align(alignment = Alignment.BottomCenter)
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth(0.75f)){
            Button(
                onClick = {
                    onCancelClicked()
                },

                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 3.dp
                ),

                modifier = Modifier.weight(2f)
            )
            {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    onAddExpenseClicked(
                        Expense(
                            date = dateInput,
                            detail = detailInput,
                            spend = spendAmountInput.toDoubleOrNull() ?: 0.0,
                            category = categoryInput
                        )
                    )
                },
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = stringResource(R.string.add),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}


@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Box(Modifier){
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.expense_category)) },
            placeholder = {
                Text(
                    text = stringResource(R.string.select_category),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            modifier = modifier
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable { expanded = true },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.dropdown_menu_description),
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            Category.entries.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category.name)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        }
    }
}



@Composable
fun ModuleAlertDialog(
    onDismissRequest: () -> Unit,
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
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ModuleBoxPreview() {
    ExpenseTrackerTheme {
        AddExpenseScreen(
            expense = Expense(detail = "bus",
                date = "2025-04-01",
                spend = 200.0,
                category = Category.Transport.toString()
            ),
            onAddExpenseClicked = { } ,
            onCancelClicked = { },
            modifier = Modifier
        )
    }
}



