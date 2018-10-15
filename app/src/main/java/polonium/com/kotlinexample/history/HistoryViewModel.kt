package polonium.com.kotlinexample.history

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.queryAllAsFlowable
import io.reactivex.disposables.Disposable
import me.tatarka.bindingcollectionadapter2.ItemBinding
import polonium.com.kotlinexample.BR
import polonium.com.kotlinexample.IRouter
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.data.BarcodeRealm
import javax.inject.Inject


class HistoryViewModel: ViewModel() {
    @Inject
    lateinit var router: IRouter

    private val listener = object : OnItemClickListener{
        override fun onItemClick(item: BarcodeRealm) {
         router.openOverview(item)
        }
    }

    val items: ObservableList<BarcodeRealm> = ObservableArrayList()
    val itemBinding = ItemBinding.of<BarcodeRealm>(BR.barcode, R.layout.history_item)
            .bindExtra(BR.listener,listener)!!
    private var disposableRealm : Disposable? = null

    init {
        disposableRealm = BarcodeRealm().queryAllAsFlowable().subscribe {items.addAll(it)}
    }

    override fun onCleared() {
        super.onCleared()
        disposableRealm?.dispose()
    }

    interface OnItemClickListener{
        fun onItemClick(item:BarcodeRealm)
    }

}
