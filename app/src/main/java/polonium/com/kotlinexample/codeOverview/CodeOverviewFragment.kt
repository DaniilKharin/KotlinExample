package polonium.com.kotlinexample.codeOverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.barcode.Barcode
import com.vicpin.krealmextensions.queryFirst
import polonium.com.kotlinexample.MainActivity
import polonium.com.kotlinexample.R
import polonium.com.kotlinexample.data.BarcodeRealm
import polonium.com.kotlinexample.utils.valueType
import polonium.com.kotlinexample.databinding.FragmentCodeOverviwBinding
import polonium.com.kotlinexample.databinding.FragmentSmsviewBinding
import polonium.com.kotlinexample.utils.BarcodeValueType.*
import timber.log.Timber

class CodeOverviewFragment : Fragment() {

    private var barcode: BarcodeRealm? = null
    private lateinit var binding: FragmentCodeOverviwBinding
    private lateinit var viewModel: CodeOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            barcode = BarcodeRealm().queryFirst { equalTo("ID", it.getString(ARG_BARCODE)) }
        }
        if (barcode == null) {
            Timber.tag(TAG).e("Barcode not found")
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCodeOverviwBinding.inflate(inflater, container, false)
        //Adding specific view for some bar/qr code content types
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (barcode == null) return

        viewModel = ViewModelProviders.of(this, CodeOverviewViewModel.Factory(barcode!!)).get(CodeOverviewViewModel::class.java)
        (activity as MainActivity).appComponent.inject(this.viewModel)
        binding.viewModel = viewModel
        when (viewModel.barcode.valueType) {
            SMS -> FragmentSmsviewBinding.inflate(layoutInflater, binding.parsedContent, true).viewModel = viewModel
            ERR -> Toast.makeText(context, R.string.err, Toast.LENGTH_LONG).show()
            /*CONTACT_INFO -> TODO()
            EMAIL -> TODO()
            ISBN -> TODO()
            PHONE -> TODO()
            PRODUCT -> TODO()
            TEXT -> TODO()
            URL -> TODO()
            WIFI -> TODO()
            GEO -> TODO()
            CALENDAR_EVENT -> TODO()
            DRIVER_LICENSE -> TODO()*/
        }
    }


    companion object {

        const val TAG = "CodeOverviewViewModel"
        const val ARG_BARCODE = "barcode"

        @JvmStatic
        fun newInstance(barcode: BarcodeRealm) =
                CodeOverviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_BARCODE, barcode.ID)
                    }
                }
    }
}
