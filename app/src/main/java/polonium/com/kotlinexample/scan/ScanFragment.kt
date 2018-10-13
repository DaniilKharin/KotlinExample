package polonium.com.kotlinexample.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_scan.*
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.Utils.RxPermissionsXKtx.RxPermissionsXKtx
import polonium.com.kotlinexample.scan.mycapture.BarcodeGraphic
import polonium.com.kotlinexample.scan.mycapture.BarcodeGraphicTracker
import polonium.com.kotlinexample.scan.mycapture.CameraSource
import polonium.com.kotlinexample.scan.mycapture.GraphicOverlay
import timber.log.Timber
import java.io.IOException
import java.util.*

class ScanFragment : Fragment() {
    var viewModel : ScanViewModel? = null

    private val compositeDisposable = CompositeDisposable()

    private var mCameraSource: CameraSource? = null

    // helper objects for detecting taps and pinches.
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var gestureDetector: GestureDetector? = null

    private var forceRefresh: Boolean = false
    private var pendingPermission:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        viewModel?.barcodeDetector = BarcodeDetector.Builder(context)
                .setBarcodeFormats(viewModel?.barcodeFormat!!)
                .build()
        (activity as MainActivity).appComponent.inject(viewModel!!)
    }

    private var rectColors: Array<Int>? = arrayOf(R.color.colorPrimary,R.color.colorPrimaryDark,R.color.colorAccent)


    private var cameraFacing:Int = CameraSource.CAMERA_FACING_BACK

    /**
     * Initializes the UI and creates the detector pipeline.
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        graphicOverlay!!.isShowText = viewModel?.preferencis!!.shouldShowText
        graphicOverlay!!.rectColors = rectColors
        graphicOverlay!!.isDrawRect = viewModel?.preferencis!!.showDrawRect

        // read parameters from the intent used to launch the activity.


        requestCameraPermission()

        gestureDetector = GestureDetector(context, CaptureGestureListener())
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())


        view.setOnTouchListener { view1 : View, e : MotionEvent ->
            val b = scaleGestureDetector!!.onTouchEvent(e)

            val c = gestureDetector!!.onTouchEvent(e)
            b || c || view1.onTouchEvent(e)
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */

    private fun requestCameraPermission() {
        requestCameraPermission(false)
    }

    private fun requestCameraPermission(startSource: Boolean) {
        val rxPermissions = RxPermissionsXKtx(this)
        compositeDisposable.add(rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted!!) {
                        createCameraSource(viewModel?.preferencis!!.autofocus, viewModel?.preferencis!!.showFlash)
                        if (pendingPermission) {
                            handleSourceRefresh(forceRefresh)
                        } else if (startSource)
                            startCameraSource()
                    } else {
                        viewModel?.onPermissionRequestDenied()
                    }
                })
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */

    @SuppressLint("InlinedApi")
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        createCameraSource(viewModel?.barcodeDetector, autoFocus, useFlash)
    }

    private fun createCameraSource(barcodeDetector: Detector<Barcode>?, autoFocus: Boolean, useFlash: Boolean) {


        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.


        val barcodeFactory = object : BarcodeTrackerFactory(graphicOverlay) {
            override fun onCodeDetected(barcode: Barcode) {
                if (!viewModel?.preferencis!!.touchAsCallback && !viewModel?.preferencis!!.multipleScan){
                    if (!(viewModel?.pause)!!)
                        viewModel?.onRetrieved(barcode)
                }
            }
        }

        barcodeDetector?.setProcessor(
                MultiProcessor.Builder(barcodeFactory).build())


        if (!(barcodeDetector?.isOperational!!)) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Timber.tag(TAG).w("Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val hasLowStorage = activity!!.cacheDir.usableSpace*100/ activity!!.cacheDir.totalSpace<=10

            if (hasLowStorage) {
                Toast.makeText(context, R.string.low_storage_error, Toast.LENGTH_LONG).show()
                Timber.tag(TAG).w(getString(R.string.low_storage_error))
            }
        }

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        var builder: CameraSource.Builder = CameraSource.Builder(context!!, barcodeDetector)
                .setFacing(cameraFacing)
                .setRequestedPreviewSize(height, width)
                .setRequestedFps(15.0f)

        mCameraSource = builder
                .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else null)
                .build()


    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    fun refresh() {
        refresh(false)
    }

    fun refresh(forceRefresh: Boolean) {

        if (mCameraSource == null) {
            pendingPermission = true
            this.forceRefresh = forceRefresh
            requestCameraPermission(false)
        } else {
            handleSourceRefresh(forceRefresh)
        }

    }

    private fun handleSourceRefresh(forceRefresh: Boolean) {
        this.forceRefresh = false
        pendingPermission = false

        graphicOverlay!!.isDrawRect = viewModel?.preferencis!!.showDrawRect
        graphicOverlay!!.rectColors = rectColors
        graphicOverlay!!.isShowText = viewModel?.preferencis!!.shouldShowText
        mCameraSource!!.setFocusMode(if (viewModel?.preferencis!!.autofocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE else null)
        mCameraSource!!.setFlashMode(if (viewModel?.preferencis!!.showFlash) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF)
        if (cameraFacing != mCameraSource!!.cameraFacing || viewModel?.barcodeFormatUpdate!! || forceRefresh) {
            if (viewModel?.barcodeDetector?.isOperational!!)
                viewModel?.barcodeDetector?.release()
            viewModel?.barcodeFormatUpdate = false
            mCameraSource!!.cameraFacing = cameraFacing
            mCameraSource!!.stop()
            mCameraSource!!.release()
            requestCameraPermission(true)

        }
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (preview != null) {
            preview!!.stop()
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (preview != null) {
            preview!!.release()
        }
        compositeDisposable.dispose()
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellationt
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != Companion.RC_HANDLE_CAMERA_PERM) {
            Timber.tag(Companion.TAG).w("Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.tag(Companion.TAG).d("Camera permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            createCameraSource(viewModel?.preferencis!!.autofocus, viewModel?.preferencis!!.showFlash)
            return
        }

        viewModel?.onRetrievedFailed(getString(R.string.no_camera_permission))

    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                context!!)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(activity, code, Companion.RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                preview!!.start(mCameraSource!!, graphicOverlay)
            } catch (e: IOException) {
                Timber.tag(Companion.TAG).e(e, "Unable to start camera source.")
                mCameraSource!!.release()
                mCameraSource = null
            }

        }
    }

    /**
     * onTap returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        // Find tap point in preview frame coordinates.
        val location = IntArray(2)
        graphicOverlay!!.getLocationOnScreen(location)
        val x = (rawX - location[0]) / graphicOverlay!!.widthScaleFactor
        val y = (rawY - location[1]) / graphicOverlay!!.heightScaleFactor

        // Find the barcode whose center is closest to the tapped point.
        var best: Barcode? = null
        var bestDistance = java.lang.Float.MAX_VALUE
        val allRetrieved = ArrayList<Barcode>()
        for (graphic in graphicOverlay!!.graphics) {
            val barcode = graphic.getBarcode()
            allRetrieved.add(barcode!!)
            if (barcode.boundingBox.contains(x.toInt(), y.toInt())) {
                // Exact hit, no need to keep looking.
                best = barcode
                break
            }
            val dx = x - barcode.boundingBox.centerX()
            val dy = y - barcode.boundingBox.centerY()
            val distance = dx * dx + dy * dy  // actually squared distance
            if (distance < bestDistance) {
                best = barcode
                bestDistance = distance
            }
        }

        if (best != null) {
            if (viewModel != null)
                if (viewModel?.preferencis!!.multipleScan) {
                    viewModel!!.onRetrievedMultiple(best, graphicOverlay!!.graphics)
                } else {
                    viewModel!!.onRetrieved(best)
                }
            return true
        }
        return false
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mCameraSource!!.doZoom(detector.scaleFactor)
            return true
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         *
         *
         * Once a scale has ended, [ScaleGestureDetector.getFocusX]
         * and [ScaleGestureDetector.getFocusY] will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         */
        override fun onScaleEnd(detector: ScaleGestureDetector) {

        }
    }

    fun stopScanning() {

        if (viewModel?.barcodeDetector?.isOperational!!)
            viewModel?.barcodeDetector?.release()


    }





    /**
     * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
     * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
     */
    internal abstract class BarcodeTrackerFactory(private val mGraphicOverlay: GraphicOverlay?) : MultiProcessor.Factory<Barcode> {

        override fun create(barcode: Barcode): Tracker<Barcode> {
            onCodeDetected(barcode)
            val graphic = BarcodeGraphic(mGraphicOverlay!!)
            return BarcodeGraphicTracker(mGraphicOverlay, graphic)
        }

        internal abstract fun onCodeDetected(barcode: Barcode)
    }

    companion object {
        private const val TAG = "Barcode-reader"
        // intent request code to handle updating play services if needed.
        private const val RC_HANDLE_GMS = 9001
        // permission request codes need to be < 256
        private const val RC_HANDLE_CAMERA_PERM = 2
    }


}
