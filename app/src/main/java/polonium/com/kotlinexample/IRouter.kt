package polonium.com.kotlinexample

import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableField
import com.google.android.gms.vision.barcode.Barcode
import polonium.com.kotlinexample.data.BarcodeRealm
import polonium.com.kotlinexample.data.SmsRealm

interface IRouter {

    fun toScanFragment()

    fun toHistoryFragment()

    fun openSettings()

    fun showMessage(message: String)

    fun openOverview(barcode: BarcodeRealm)

    fun sendSMS(sms: SmsRealm)

    fun callTelNumber(tel: String)

    fun shareBitmap(bitmap: Bitmap): ObservableField<Uri?>

    fun shareBitmap(uri: Uri)

    fun saveBitmap(bitmap: Bitmap): ObservableField<Uri?>

    fun openBitmap(bitmap: Bitmap): ObservableField<Uri?>

    fun openBitmap(uri: Uri)

    fun onCleared()

}