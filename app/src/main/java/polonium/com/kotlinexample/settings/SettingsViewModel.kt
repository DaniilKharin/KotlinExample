package polonium.com.kotlinexample.settings

import androidx.lifecycle.ViewModel
import polonium.com.kotlinexample.data.Preferences
import javax.inject.Inject

class SettingsViewModel: ViewModel(){
    @Inject
    lateinit var preferences: Preferences
}