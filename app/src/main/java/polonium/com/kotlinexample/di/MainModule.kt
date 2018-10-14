package polonium.com.kotlinexample.di

import android.content.Context.MODE_PRIVATE
import dagger.Module
import dagger.Provides
import polonium.com.kotlinexample.IRouter
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.Router
import polonium.com.kotlinexample.data.Preferences

@Module
class MainModule(private val mainActivity: MainActivity) {

    @Provides
    fun provideRouter(): IRouter {
        return Router(mainActivity.supportFragmentManager, mainActivity)
    }

    @Provides
    fun providePref(): Preferences {
        return Preferences(mainActivity.getSharedPreferences(PREFS_NAME,MODE_PRIVATE))
    }

    companion object {
        const val PREFS_NAME: String = "PREFS_NAME"
    }
}