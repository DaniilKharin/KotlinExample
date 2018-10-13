package polonium.com.kotlinexample.codeOverview

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.barcode.Barcode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import polonium.com.kotlinexample.IRouter
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.Utils.createBitmap
import javax.inject.Inject

class CodeOverviewViewModel(val barcode: Barcode) : ViewModel() {

    @Inject
    lateinit var router: IRouter

    private val compositeDisposable = CompositeDisposable()

    var imageUri: ObservableField<Uri?> = ObservableField()

    val barcodeValueType: BarcodeValueType = BarcodeValueType.values()[barcode.valueFormat]

    enum class BarcodeValueType(val strId: Int) {
        ERR(R.string.err),
        CONTACT_INFO(R.string.contact),
        EMAIL(R.string.email),
        ISBN(R.string.ISBN),
        PHONE(R.string.phone),
        PRODUCT(R.string.product),
        SMS(R.string.sms),
        TEXT(R.string.text),
        URL(R.string.url),
        WIFI(R.string.wifi),
        GEO(R.string.geo),
        CALENDAR_EVENT(R.string.calendarEvent),
        DRIVER_LICENSE(R.string.driverLicense)


    }

    fun sendSms() {
        if (barcodeValueType == BarcodeValueType.SMS)
            router.sendSMS(barcode.sms)
        else
            throw RuntimeException("Не верный тип кода")
    }

    fun call() {
        router.callTelNumber(barcode.sms.phoneNumber)
    }

    fun shareCodeImage() {
        if (imageUri.get() == null) {
            compositeDisposable.add(Observable.fromCallable { barcode.createBitmap(640, 480) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        imageUri = router.shareBitmap(result)
                    })
        } else {
            compositeDisposable.add(Observable.fromCallable { imageUri }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        router.shareBitmap(result.get()!!)
                    })
        }

    }

    fun saveOpenCodeImage() {
        if (imageUri.get() == null) {
            compositeDisposable.add(Observable.fromCallable { barcode.createBitmap(640, 480) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        imageUri = router.saveBitmap(result)
                    })
        } else
            compositeDisposable.add(Observable.fromCallable { imageUri }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        router.openBitmap(imageUri.get()!!)
                    })
    }

    class Factory(val barcode: Barcode) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(Barcode::class.java).newInstance(barcode)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}