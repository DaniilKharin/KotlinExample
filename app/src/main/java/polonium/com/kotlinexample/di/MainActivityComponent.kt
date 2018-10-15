package polonium.com.kotlinexample.di

import dagger.Component
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.codeOverview.CodeOverviewViewModel
import polonium.com.kotlinexample.history.HistoryViewModel
import polonium.com.kotlinexample.scan.ScanViewModel
import polonium.com.kotlinexample.settings.SettingsViewModel

@Component(modules = [
    MainModule::class
])
interface MainActivityComponent {
    fun inject(scanViewModel: ScanViewModel)
    fun inject(CodeOverviewViewViewModel: CodeOverviewViewModel)
    fun inject(mainActivity: MainActivity)
    fun inject(settingsModel: SettingsViewModel)
    fun inject(viewModel: HistoryViewModel)
}