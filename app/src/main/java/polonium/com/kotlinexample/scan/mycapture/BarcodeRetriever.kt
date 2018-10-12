package polonium.com.kotlinexample.scan.mycapture

import android.util.SparseArray
import com.google.android.gms.vision.barcode.Barcode

interface BarcodeRetriever {
    fun onRetrieved(barcode: Barcode)

    fun onRetrievedMultiple(closetToClick: Barcode, barcode: List<BarcodeGraphic>)

    fun onBitmapScanned(sparseArray: SparseArray<Barcode>)

    fun onRetrievedFailed(reason: String)

    fun onPermissionRequestDenied()
}