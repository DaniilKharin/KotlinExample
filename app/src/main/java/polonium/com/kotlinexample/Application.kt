package polonium.com.kotlinexample

import android.app.Application
import io.realm.Realm
import timber.log.Timber
import timber.log.Timber.DebugTree



class Application : Application(){

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            //TODO Подключить крашлитикс или что-то подобное
        }
    }

}