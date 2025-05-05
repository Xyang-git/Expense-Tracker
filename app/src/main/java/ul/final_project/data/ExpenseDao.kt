package ul.final_project.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expense WHERE date LIKE :date || '-%'")
    fun getExpenseInMonth(date: String): Flow<List<Expense>>

    @Query("SELECT * from expense ORDER BY date ASC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT COUNT(*) from expense")
    suspend fun getCount(): Int

}