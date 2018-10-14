package polonium.com.kotlinexample.scan


import android.util.SparseArray
import androidx.lifecycle.ViewModel
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import polonium.com.kotlinexample.IRouter
import polonium.com.kotlinexample.data.Preferences
import polonium.com.kotlinexample.scan.mycapture.BarcodeGraphic
import polonium.com.kotlinexample.scan.mycapture.BarcodeRetriever
import javax.inject.Inject


class ScanViewModel : ViewModel() , BarcodeRetriever {

    @Inject
    lateinit var router: IRouter
    @Inject
    lateinit var preferences: Preferences

    var barcodeFormat: Int = 0
    var barcodeFormatUpdate = false
    var pause = false
    var barcodeDetector: BarcodeDetector? = null

    override fun onRetrieved(barcode: Barcode) {
        router.openOverview(barcode)
    }

    override fun onRetrievedMultiple(closetToClick: Barcode, barcode: List<BarcodeGraphic>) {

    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>) {
    }

    override fun onRetrievedFailed(reason: String) {
    }

    override fun onPermissionRequestDenied() {
    }

}