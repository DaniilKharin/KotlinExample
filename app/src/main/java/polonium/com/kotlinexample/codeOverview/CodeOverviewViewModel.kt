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
import polonium.com.kotlinexample.data.BarcodeRealm
import polonium.com.kotlinexample.utils.BarcodeValueType
import polonium.com.kotlinexample.utils.createBitmap
import polonium.com.kotlinexample.utils.valueType
import javax.inject.Inject

class CodeOverviewViewModel(val barcode: BarcodeRealm) : ViewModel() {

    @Inject
    lateinit var router: IRouter

    private val compositeDisposable = CompositeDisposable()

    var imageUri: ObservableField<Uri?> = ObservableField()

    fun getBarcodeValueType(): BarcodeValueType{
        return barcode.valueType
    }

    fun sendSms() {
        if (barcode.valueType == BarcodeValueType.SMS)
            barcode.sms?.let { router.sendSMS(it) }
        else
            throw RuntimeException("Не верный тип кода")
    }

    fun call() {
        barcode.sms?.phoneNumber?.let { router.callTelNumber(it) }
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

    class Factory(val barcode: BarcodeRealm) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(BarcodeRealm::class.java).newInstance(barcode)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}