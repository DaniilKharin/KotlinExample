package polonium.com.kotlinexample.Utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.gms.vision.barcode.Barcode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


abstract class DatabindingAdapters {

    /* @BindingAdapter
     public fun setTextFromStringId(textView: TextView,@StringRes stringRes: Int){
         textView.setText(stringRes)
     }*/
    companion object {

        @JvmStatic
        @BindingAdapter("barcode")
        public fun ImageView.setBarcode(barcode: Barcode) {
            val manager: WindowManager = this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            manager.defaultDisplay.getMetrics(displayMetrics)
            val minSide: Int = pxToDpInt(displayMetrics, if (displayMetrics.widthPixels < displayMetrics.heightPixels) displayMetrics.widthPixels else displayMetrics.heightPixels)


            val disposable = Observable.fromCallable {
                barcode.createBitmap(
                        if (this.layoutParams.width >= 0) pxToDpInt(displayMetrics, this.layoutParams.width) else minSide,
                        if (this.layoutParams.height >= 0) pxToDpInt(displayMetrics, this.layoutParams.height) else minSide)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        this.setImageBitmap(result)
                    }
            this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(var1: View) {

                }

                override fun onViewDetachedFromWindow(var1: View) {
                    disposable.dispose()
                }
            })
        }


        fun pxToDpInt(displayMetrics: DisplayMetrics, px: Int): Int {
            return (px *  DisplayMetrics.DENSITY_DEFAULT / displayMetrics.densityDpi.toFloat()).toInt()
        }

    }


}
