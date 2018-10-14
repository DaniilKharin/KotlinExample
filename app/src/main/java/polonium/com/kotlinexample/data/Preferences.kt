package polonium.com.kotlinexample.data

import android.content.SharedPreferences

class Preferences(private val sharedPreferences: SharedPreferences) {

    var autofocus: Boolean
        get() = sharedPreferences.getBoolean(AUTOFOCUS_SETTING_NAME, true)
        set(value) {
            sharedPreferences.edit().putBoolean(AUTOFOCUS_SETTING_NAME, value).apply()
        }

    var shouldShowText: Boolean
        get() = sharedPreferences.getBoolean(SHOW_TEXT_SETTING_NAME, true)
        set(value) {
            sharedPreferences.edit().putBoolean(SHOW_TEXT_SETTING_NAME, value).apply()
        }

    var showDrawRect: Boolean
        get() = sharedPreferences.getBoolean(SHOW_DRAW_RECT_SETTING_NAME, true)
        set(value) {
            sharedPreferences.edit().putBoolean(SHOW_DRAW_RECT_SETTING_NAME, value).apply()
        }
    var showFlash: Boolean
        get() = sharedPreferences.getBoolean(SHOW_FLASH_SETTING_NAME, false)
        set(value) {
            sharedPreferences.edit().putBoolean(SHOW_FLASH_SETTING_NAME, value).apply()
        }
    var multipleScan: Boolean
        get() = sharedPreferences.getBoolean(MULTIPLE_SCAN_SETTING_NAME, false)
        set(value) {
            sharedPreferences.edit().putBoolean(MULTIPLE_SCAN_SETTING_NAME, value).apply()
        }

    var touchAsCallback: Boolean
        get() = sharedPreferences.getBoolean(TOUCH_AS_CALLBACK_SETTING_NAME, true)
        set(value) {
            sharedPreferences.edit().putBoolean(TOUCH_AS_CALLBACK_SETTING_NAME, value).apply()
        }




    companion object {
        const val AUTOFOCUS_SETTING_NAME: String = "AUTOFOCUS_SETTING"
        const val SHOW_TEXT_SETTING_NAME: String = "SHOW_TEXT_SETTING"
        const val SHOW_DRAW_RECT_SETTING_NAME: String = "SHOW_DRAW_RECT_SETTING"
        const val SHOW_FLASH_SETTING_NAME: String = "SHOW_FLASH_SETTING"
        const val MULTIPLE_SCAN_SETTING_NAME: String = "MULTIPLE_SCAN_SETTING"
        const val TOUCH_AS_CALLBACK_SETTING_NAME: String = "TOUCH_AS_CALLBACK_SETTING"
    }

}