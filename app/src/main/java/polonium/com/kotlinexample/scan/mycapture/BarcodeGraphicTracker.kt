package polonium.com.kotlinexample.scan.mycapture

import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
internal class BarcodeGraphicTracker(private val mOverlay: GraphicOverlay, private val mGraphic: BarcodeGraphic) : Tracker<Barcode>() {

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    override fun onNewItem(id: Int, item: Barcode) {
        mGraphic.setId(id)
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    override fun onUpdate(detectionResults: Detector.Detections<Barcode>?, item: Barcode) {
        mOverlay.add(mGraphic)
        mGraphic.updateItem(item)
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    override fun onMissing(detectionResults: Detector.Detections<Barcode>?) {
        mOverlay.remove(mGraphic)
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    override fun onDone() {
        mOverlay.remove(mGraphic)
    }
}
