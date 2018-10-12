package polonium.com.kotlinexample.scan.mycapture

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.barcode.Barcode


class BarcodeGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {
    private var mId: Int = 0


    private var mCurrentColorIndex = 0

    private var mRectPaint: Paint? = null
    private var mTextPaint: Paint? = null
    @Volatile
    private var mBarcode: Barcode? = null
    private var graphicOverlay: GraphicOverlay? = null

    init {
        graphicOverlay = overlay
        mCurrentColorIndex = (mCurrentColorIndex + 1) % overlay.rectColors?.size!!
        val selectedColor = ContextCompat.getColor(overlay.context, overlay.rectColors!![mCurrentColorIndex])

        mRectPaint = Paint()
        mRectPaint!!.color = selectedColor
        mRectPaint!!.style = Paint.Style.STROKE
        mRectPaint!!.strokeWidth = 4.0f

        mTextPaint = Paint()
        mTextPaint!!.color = selectedColor
        mTextPaint!!.textSize = 36.0f
    }

    fun getId(): Int {
        return mId
    }

    fun setId(id: Int) {
        this.mId = id
    }

    fun getBarcode(): Barcode? {
        return mBarcode
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    internal fun updateItem(barcode: Barcode) {
        mBarcode = barcode
        postInvalidate()
    }

    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    override fun draw(canvas: Canvas) {
        val barcode = mBarcode ?: return

        // Draws the bounding box around the barcode.
        val rect = RectF(barcode.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        if (graphicOverlay?.isDrawRect!!)
            canvas.drawRect(rect, mRectPaint)

        // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
        if (graphicOverlay?.isShowText!!)
            canvas.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint)
    }
}