package ul.final_project

import android.app.Application
import ul.final_project.data.AppContainer
import ul.final_project.data.AppDataContainer

class ExpenseApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
